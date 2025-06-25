package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CalificacionRequestDTO;
import com.uade.cookitbackend.dto.CalificacionResponseDTO;
import com.uade.cookitbackend.entity.Calificacion;
import com.uade.cookitbackend.entity.Receta;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.CalificacionRepository;
import com.uade.cookitbackend.repository.db.RecetaRepository;
import com.uade.cookitbackend.repository.db.UsuarioRepository;
import com.uade.cookitbackend.service.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecetaRepository recetaRepository;

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

        return mapToDTO(calificacionRepository.save(calificacion));
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
