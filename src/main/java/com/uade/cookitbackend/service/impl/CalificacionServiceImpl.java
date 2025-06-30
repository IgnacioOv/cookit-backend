package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CalificacionRequestDTO;
import com.uade.cookitbackend.dto.CalificacionResponseDTO;
import com.uade.cookitbackend.entity.*;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.*;
import com.uade.cookitbackend.repository.notification.NotificationRepository;
import com.uade.cookitbackend.service.CalificacionService;
import com.uade.cookitbackend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final CalificacionApprovalRepository calificacionApprovalRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final RecetaRepository recetaRepository;
    private final NotificationRepository notificationRepository;
    private final UserSessionRepository userSessionRepository;

    @Override
    @Transactional
    public CalificacionResponseDTO crearCalificacion(CalificacionRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USUARIO_NOT_FOUND,"Usuario no encontrado"));

        Receta receta = recetaRepository.findById(request.getIdReceta())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RECETA_NOT_FOUND,"Receta no encontrada"));

        calificacionRepository.findByUsuarioIdUsuarioAndRecetaIdReceta(request.getIdUsuario(), request.getIdReceta())
                .ifPresent(c -> {
                    throw new DuplicateResourceException(ErrorCode.DUPLICATE_RESOURCE,"El usuario ya calificó esta receta");
                });

        Calificacion calificacion = new Calificacion();
        calificacion.setUsuario(usuario);
        calificacion.setReceta(receta);
        calificacion.setCalificacion(request.getCalificacion());
        calificacion.setComentarios(request.getComentarios());

        Calificacion savedCalificacion = calificacionRepository.save(calificacion);

        CalificacionApproval approval = new CalificacionApproval();
        approval.setCalificacion(savedCalificacion);
        approval.setApproved(false);
        calificacionApprovalRepository.save(approval);

        return mapToDTO(savedCalificacion);
    }

    @Override
    @Transactional
    public CalificacionResponseDTO actualizarCalificacion(Integer id, CalificacionRequestDTO request) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CALIFICACION_NOT_FOUND,"Calificación no encontrada"));

        calificacion.setCalificacion(request.getCalificacion());
        calificacion.setComentarios(request.getComentarios());

        return mapToDTO(calificacionRepository.save(calificacion));
    }

    @Override
    @Transactional
    public void eliminarCalificacion(Integer id) {
        if (!calificacionRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.CALIFICACION_NOT_FOUND,"Calificación no encontrada");
        }
        calificacionRepository.deleteById(id);
    }

    @Override
    public CalificacionResponseDTO obtenerCalificacion(Integer id) {
        return calificacionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CALIFICACION_NOT_FOUND,"Calificación no encontrada"));
    }

    @Override
    public List<CalificacionResponseDTO> obtenerCalificacionesPorReceta(Integer idReceta) {
        return calificacionRepository.findByRecetaIdRecetaWithApprovalStatus(idReceta)
                .stream()
                .map(this::mapToDTOWithApprovalStatus)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalificacionResponseDTO> obtenerTodasLasCalificaciones() {
        return calificacionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalificacionResponseDTO> obtenerCalificacionesNoAprobadas() {
        return calificacionApprovalRepository.findByApprovedFalse()
                .stream()
                .map(calificacionApproval -> mapToDTO(calificacionApproval.getCalificacion()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void aprobarCalificacion(Integer id) {
        CalificacionApproval approval = calificacionApprovalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CALIFICACION_NOT_FOUND, "Calificación no encontrada"));
        
        approval.setApproved(true);
        calificacionApprovalRepository.save(approval);
        this.enviarNotificacionConReintentos(approval.getCalificacion().getUsuario().getIdUsuario(),
                "Calificación aprobada",
                "Tu calificación ha sido aprobada y publicada en la receta: " + approval.getCalificacion().getReceta().getNombreReceta());
    }

    private void enviarNotificacionConReintentos(Integer userId, String titulo, String mensaje) {
        int maxReintentos = 3;
        int reintento = 0;

        while (reintento < maxReintentos) {
            try {
                Usuario usuarioToSendNot = usuarioService.getUsuarioById(userId);
                UserSession lastSesion = userSessionRepository.findLastUserSessionByUser(usuarioToSendNot).getFirst();

                notificationRepository.sendNotification(lastSesion.getFmc(), titulo, mensaje, userId);

                log.info("Notificación enviada exitosamente al usuario {} en el intento {}", userId, reintento + 1);
                return; // Éxito, salir del método

            } catch (ResourceNotFoundException e) {
                log.error("Usuario {} no encontrado para envío de notificación: {}", userId, e.getMessage());
                return; // No reintentar si el usuario no existe

            } catch (IndexOutOfBoundsException e) {
                log.warn("Usuario {} no tiene sesiones activas para notificación: {}", userId, e.getMessage());
                return; // No reintentar si no hay sesiones

            } catch (Exception e) {
                reintento++;
                log.warn("Error enviando notificación al usuario {} (intento {}/{}): {}",
                        userId, reintento, maxReintentos, e.getMessage());

                if (reintento >= maxReintentos) {
                    log.error("Falló el envío de notificación al usuario {} después de {} intentos. Error final: {}",
                            userId, maxReintentos, e.getMessage());
                    return;
                }

                // Esperar antes del siguiente intento (backoff exponencial)
                try {
                    Thread.sleep(1000 * (long) Math.pow(2, reintento - 1)); // 1s, 2s, 4s
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Interrupted durante reintento de notificación");
                    return;
                }
            }
        }
    }

    private CalificacionResponseDTO mapToDTO(Calificacion calificacion) {
        CalificacionResponseDTO dto = new CalificacionResponseDTO();
        dto.setId(calificacion.getIdCalificacion());
        dto.setIdUsuario(calificacion.getUsuario().getIdUsuario());
        dto.setNombreUsuario(calificacion.getUsuario().getNombre());
        dto.setIdReceta(calificacion.getReceta().getIdReceta());
        dto.setNombreReceta(calificacion.getReceta().getNombreReceta());
        dto.setCalificacion(calificacion.getCalificacion());
        dto.setComentarios(calificacion.getComentarios());
        return dto;
    }

    private CalificacionResponseDTO mapToDTOWithApprovalStatus(Object[] result) {
        Calificacion calificacion = (Calificacion) result[0];
        String comentariosAprobados = (String) result[1];
        
        CalificacionResponseDTO dto = new CalificacionResponseDTO();
        dto.setId(calificacion.getIdCalificacion());
        dto.setIdUsuario(calificacion.getUsuario().getIdUsuario());
        dto.setNombreUsuario(calificacion.getUsuario().getNombre());
        dto.setIdReceta(calificacion.getReceta().getIdReceta());
        dto.setNombreReceta(calificacion.getReceta().getNombreReceta());
        dto.setCalificacion(calificacion.getCalificacion());
        dto.setComentarios(comentariosAprobados); // Only approved comments are shown
        return dto;
    }
}
