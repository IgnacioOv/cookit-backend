package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.UpdateRecetaDTO;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.*;
import com.uade.cookitbackend.exception.BadRequestException;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.*;
import com.uade.cookitbackend.repository.notification.NotificationRepository;
import com.uade.cookitbackend.service.RecetaService;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.service.impl.TipoRecetaServiceImpl;
import com.uade.cookitbackend.service.mappers.RecetaMapper;
import com.uade.cookitbackend.utils.SessionUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final RecetaFavoritaRepository recetaFavoritaRepository;
    private final RecetaApprovalRepository recetaApprovalRepository;
    private final RecetaMapper recetaMapper;
    private final UsuarioService usuarioService;
    private final TipoRecetaServiceImpl tipoRecetaServiceImpl;
    private final IngredienteRepository ingredienteRepository;
    private final UnidadRepository unidadRepository;
    private final NotificationRepository notificationRepository;
    private final UserSessionRepository userSessionRepository;

    @Override
    @Transactional
    public RecetaResponseDTO createReceta(CreateRecetaDTO createRecetaDTO) {
        Usuario usuario = usuarioService.getUsuarioById(createRecetaDTO.getIdUsuario());
        if (usuario == null) {
            throw new ResourceNotFoundException(
                    ErrorCode.USUARIO_NOT_FOUND,
                    "Usuario con ID " + createRecetaDTO.getIdUsuario() + " no encontrado."
            );
        }

        TipoReceta tipoReceta = tipoRecetaServiceImpl.getTipoRecetaById(createRecetaDTO.getIdTipo());
        if (tipoReceta == null) {
            throw new ResourceNotFoundException(
                    ErrorCode.TIPO_RECETA_NOT_FOUND,
                    "TipoReceta con ID " + createRecetaDTO.getIdTipo() + " no encontrado."
            );
        }

        Receta receta = recetaMapper.toEntity(createRecetaDTO);
        receta.setUsuario(usuario);
        receta.setTipoReceta(tipoReceta);

        if (createRecetaDTO.getIngredientesUtilizados() != null) {
            List<IngredienteUtilizado> ingredientesUtilizados = createRecetaDTO.getIngredientesUtilizados().stream()
                    .map(dto -> {
                        IngredienteUtilizado iu = new IngredienteUtilizado();
                        iu.setReceta(receta);
                        iu.setCantidad(dto.getCantidad());
                        iu.setObservaciones(dto.getObservaciones());

                        if (dto.getIdIngrediente() != null) {
                            ingredienteRepository.findById(dto.getIdIngrediente())
                                    .ifPresent(i -> iu.setIngrediente(i));
                        }
                        if (dto.getIdUnidad() != null) {
                            unidadRepository.findById(dto.getIdUnidad())
                                    .ifPresent(u -> iu.setUnidad(u));
                        }
                        return iu;
                    })
                    .collect(Collectors.toList());
            receta.setIngredientesUtilizados(ingredientesUtilizados);
        }

        if (createRecetaDTO.getFotoPrincipal() != null && !createRecetaDTO.getFotoPrincipal().isEmpty()) {
            Foto foto = crearFotoPrincipal(createRecetaDTO.getFotoPrincipal(), receta);
            receta.setFotos(List.of(foto));
        }

        if (receta.getPasos() != null) {
            for (Paso paso : receta.getPasos()) {
                paso.setReceta(receta);
                if (paso.getMultimedia() != null) {
                    paso.getMultimedia().forEach(m -> m.setPaso(paso));
                }
            }
        }

        val savedReceta = recetaRepository.save(receta);

        // Crear entrada en RecetaApproval con approved = false
        RecetaApproval recetaApproval = new RecetaApproval();
        recetaApproval.setReceta(savedReceta);
        recetaApproval.setApproved(false);
        recetaApprovalRepository.save(recetaApproval);

        return recetaMapper.recetaToRecetaResponseDTO(savedReceta);
    }

    @Override
    public List<RecetaResponseDTO> getRecetasByNombre(String nombreReceta) {
        List<Receta> recetas = recetaRepository
                .findApprovedByNombreRecetaContainingIgnoreCaseOrderByIdRecetaDesc(nombreReceta);

        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResponseDTO> getRecetaByIdUsuario(Integer userId) {
        List<Receta> recetas = recetaRepository.findApprovedRecetaByUsuario_IdUsuario(userId);
        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResponseDTO> getRecetasWithoutIngrediente(String ingrediente, String orden) {
        Sort sort = "fecha".equalsIgnoreCase(orden)
                ? Sort.by(Sort.Direction.DESC, "idReceta")
                : "usuario".equalsIgnoreCase(orden)
                ? Sort.by(Sort.Direction.ASC, "usuario.nombreUsuario")
                : Sort.by(Sort.Direction.ASC, "nombreReceta");

        List<Receta> recetas = recetaRepository.findApprovedRecetasSinIngrediente(ingrediente, sort);
        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResponseDTO> getRecetasWithIngrediente(String ingrediente, String orden) {
        Sort sort = "fecha".equalsIgnoreCase(orden)
                ? Sort.by(Sort.Direction.DESC, "idReceta")
                : "usuario".equalsIgnoreCase(orden)
                ? Sort.by(Sort.Direction.ASC, "usuario.nombreUsuario")
                : Sort.by(Sort.Direction.ASC, "nombreReceta");

        List<Receta> recetas = recetaRepository.findApprovedRecetasConIngrediente(ingrediente, sort);
        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RecetaResponseDTO getRecetaById(Integer id) {
        Receta receta = recetaRepository.findApprovedByIdWithIngredientes(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECETA_NOT_FOUND,
                        "Receta con ID " + id + " no encontrada o no aprobada."
                ));
        return recetaMapper.recetaToRecetaResponseDTO(receta);
    }

    @Override
    public List<RecetaResponseDTO> getFeed() {
        List<Receta> recetas = recetaRepository.findAllApproved();
        // Mapear manualmente usando el método individual para evitar ambigüedad
        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTOSinPasos)
                .collect(Collectors.toList());
    }

    private Foto crearFotoPrincipal(String urlFoto, Receta receta) {
        Foto foto = new Foto();
        foto.setReceta(receta);
        foto.setUrlFoto(urlFoto);

        String extension = null;
        int lastDot = urlFoto.lastIndexOf('.');
        if (lastDot != -1 && lastDot < urlFoto.length() - 1) {
            extension = urlFoto.substring(lastDot + 1);
            if (extension.length() > 5) {
                extension = extension.substring(0, 5);
            }
        }
        foto.setExtension(extension);
        return foto;
    }

    @Override
    public RecetaResponseDTO createReceta(CreateRecetaDTO createRecetaDTO, Boolean reemplazar) {
        // Verificar si existe receta duplicada
        boolean exists = recetaRepository.existsByNombreRecetaAndUsuario_IdUsuario(
                createRecetaDTO.getNombreReceta(), createRecetaDTO.getIdUsuario());

        if (exists && !reemplazar) {
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_RECIPE_NAME,
                    "Ya existe una receta con el nombre '" + createRecetaDTO.getNombreReceta() +
                            "' para este usuario. Use reemplazar=true para sobrescribir.");
        }

        if (exists && reemplazar) {
            // Eliminar receta existente
            Optional<Receta> recetaExistente = recetaRepository.findByNombreRecetaAndUsuario_IdUsuario(
                    createRecetaDTO.getNombreReceta(), createRecetaDTO.getIdUsuario());
            if (recetaExistente.isPresent()) {
                recetaRepository.delete(recetaExistente.get());
            }
        }

        return createReceta(createRecetaDTO);
    }

    @Override
    @Transactional
    public RecetaResponseDTO updateReceta(Integer idReceta, UpdateRecetaDTO updateRecetaDTO, Integer idUsuario) {
        // Buscar la receta existente
        Receta receta = recetaRepository.findById(idReceta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECETA_NOT_FOUND,
                        "Receta con ID " + idReceta + " no encontrada."
                ));

        // Verificar que el usuario es el propietario de la receta
        if (!receta.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new BadRequestException(
                    ErrorCode.UNAUTHORIZED_OPERATION,
                    "No tienes permisos para editar esta receta."
            );
        }

        // Actualizar campos básicos
        if (updateRecetaDTO.getNombreReceta() != null) {
            receta.setNombreReceta(updateRecetaDTO.getNombreReceta());
        }
        if (updateRecetaDTO.getDescripcionReceta() != null) {
            receta.setDescripcionReceta(updateRecetaDTO.getDescripcionReceta());
        }
        if (updateRecetaDTO.getFotoPrincipal() != null) {
            receta.setFotoPrincipal(updateRecetaDTO.getFotoPrincipal());
        }
        if (updateRecetaDTO.getPorciones() != null) {
            receta.setPorciones(updateRecetaDTO.getPorciones());
        }
        if (updateRecetaDTO.getCantidadPersonas() != null) {
            receta.setCantidadPersonas(updateRecetaDTO.getCantidadPersonas());
        }

        // Actualizar tipo de receta si se proporciona
        if (updateRecetaDTO.getIdTipo() != null) {
            TipoReceta tipoReceta = tipoRecetaServiceImpl.getTipoRecetaById(updateRecetaDTO.getIdTipo());
            if (tipoReceta == null) {
                throw new ResourceNotFoundException(
                        ErrorCode.TIPO_RECETA_NOT_FOUND,
                        "TipoReceta con ID " + updateRecetaDTO.getIdTipo() + " no encontrado."
                );
            }
            receta.setTipoReceta(tipoReceta);
        }

        // Actualizar ingredientes utilizados
        if (updateRecetaDTO.getIngredientesUtilizados() != null) {
            // Crear nueva lista mutable de ingredientes
            List<IngredienteUtilizado> ingredientesUtilizados = updateRecetaDTO.getIngredientesUtilizados().stream()
                    .map(dto -> {
                        IngredienteUtilizado iu = new IngredienteUtilizado();
                        iu.setReceta(receta);
                        iu.setCantidad(dto.getCantidad());
                        iu.setObservaciones(dto.getObservaciones());

                        if (dto.getIdIngrediente() != null) {
                            ingredienteRepository.findById(dto.getIdIngrediente())
                                    .ifPresent(iu::setIngrediente);
                        }
                        if (dto.getIdUnidad() != null) {
                            unidadRepository.findById(dto.getIdUnidad())
                                    .ifPresent(iu::setUnidad);
                        }
                        return iu;
                    })
                    .collect(Collectors.toList());
            receta.setIngredientesUtilizados(new ArrayList<>(ingredientesUtilizados));
        }

        // Actualizar pasos
        if (updateRecetaDTO.getPasos() != null) {
            // Crear nueva lista mutable de pasos
            List<Paso> pasos = updateRecetaDTO.getPasos().stream()
                    .map(pasoDto -> {
                        Paso paso = new Paso();
                        paso.setReceta(receta);
                        paso.setNumeroPaso(pasoDto.getNumeroPaso());
                        paso.setTexto(pasoDto.getTexto());
                        
                        if (pasoDto.getMultimedia() != null) {
                            List<Multimedia> multimedia = pasoDto.getMultimedia().stream()
                                    .map(multimediaDto -> {
                                        Multimedia m = new Multimedia();
                                        m.setPaso(paso);
                                        m.setUrlContenido(multimediaDto.getUrlContenido());
                                        m.setExtension(multimediaDto.getExtension());
                                        m.setTipoContenido(multimediaDto.getTipoContenido());
                                        return m;
                                    })
                                    .collect(Collectors.toList());
                            paso.setMultimedia(new ArrayList<>(multimedia));
                        }
                        return paso;
                    })
                    .collect(Collectors.toList());
            receta.setPasos(new ArrayList<>(pasos));
        }

        // Actualizar foto principal si se proporciona
        if (updateRecetaDTO.getFotoPrincipal() != null && !updateRecetaDTO.getFotoPrincipal().isEmpty()) {
            // Crear nueva lista mutable de fotos
            Foto foto = crearFotoPrincipal(updateRecetaDTO.getFotoPrincipal(), receta);
            receta.setFotos(new ArrayList<>(List.of(foto)));
        }

        // Guardar la receta actualizada
        val savedReceta = recetaRepository.save(receta);

        // Marcar como no aprobada después de la edición
        RecetaApproval approval = recetaApprovalRepository.findById(idReceta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECETA_NOT_FOUND,
                        "RecetaApproval con ID " + idReceta + " no encontrada."
                ));
        approval.setApproved(false);
        recetaApprovalRepository.save(approval);

        return recetaMapper.recetaToRecetaResponseDTO(savedReceta);
    }

    @Override
    public Boolean existsRecetaByNombreAndUsuario(String nombreReceta, Integer idUsuario) {
        return recetaRepository.existsByNombreRecetaAndUsuario_IdUsuario(nombreReceta, idUsuario);
    }

    @Override
    public List<RecetaResponseDTO> getRecetasByTipo(Integer idTipo, String orden) {
        Sort sort = "fecha".equalsIgnoreCase(orden)
                ? Sort.by(Sort.Direction.DESC, "idReceta")
                : "usuario".equalsIgnoreCase(orden)
                ? Sort.by(Sort.Direction.ASC, "usuario.nombreUsuario")
                : Sort.by(Sort.Direction.ASC, "nombreReceta");

        List<Receta> recetas = recetaRepository.findApprovedByTipoReceta(idTipo, sort);
        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    // Nuevo método para obtener solo los pasos de una receta
    public List<Paso> getPasosByRecetaId(Integer id) {
        Receta receta = recetaRepository.findApprovedByIdWithIngredientes(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECETA_NOT_FOUND,
                        "Receta con ID " + id + " no encontrada o no aprobada."
                ));
        return receta.getPasos();
    }

    @Override
    @Transactional
    public void agregarAFavoritos(Integer idUsuario, Integer idReceta) {
        // Verificar que la receta existe y está aprobada
        Receta receta = recetaRepository.findApprovedByIdWithIngredientes(idReceta)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RECETA_NOT_FOUND,
                        "Receta no encontrada o no aprobada"));

        // Verificar que el usuario existe
        Usuario usuario = usuarioService.getUsuarioById(idUsuario);
        if (usuario == null) {
            throw new ResourceNotFoundException(ErrorCode.USUARIO_NOT_FOUND, "Usuario no encontrado");
        }

        // Verificar que no esté ya en favoritos
        if (recetaFavoritaRepository.existsByUsuario_IdUsuarioAndReceta_IdReceta(idUsuario, idReceta)) {
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_FAVORITE,
                    "La receta ya está en favoritos del usuario");
        }

        // Verificar límite de 10 favoritos
        long cantidadFavoritos = recetaFavoritaRepository.countByUsuario_IdUsuario(idUsuario);
        if (cantidadFavoritos >= 10) {
            throw new BadRequestException(ErrorCode.FAVORITES_LIMIT_EXCEEDED,
                    "El usuario ya tiene el máximo de 10 recetas favoritas");
        }

        // Crear favorito
        RecetaFavorita favorita = new RecetaFavorita();
        favorita.setUsuario(usuario);
        favorita.setReceta(receta);
        recetaFavoritaRepository.save(favorita);
    }

    @Override
    @Transactional
    public void quitarDeFavoritos(Integer idUsuario, Integer idReceta) {
        RecetaFavorita favorita = recetaFavoritaRepository
                .findByUsuario_IdUsuarioAndReceta_IdReceta(idUsuario, idReceta)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.FAVORITE_NOT_FOUND,
                        "La receta no está en favoritos del usuario"));

        recetaFavoritaRepository.delete(favorita);
    }

    @Override
    public List<RecetaResponseDTO> getRecetasFavoritas(Integer idUsuario) {
        List<RecetaFavorita> favoritas = recetaFavoritaRepository
                .findByUsuario_IdUsuarioOrderByFechaAgregadaDesc(idUsuario);

        return favoritas.stream()
                .map(favorita -> recetaMapper.recetaToRecetaResponseDTO(favorita.getReceta()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResponseDTO> getRecetasNoAprobadas() {
        List<RecetaApproval> recetasNoAprobadas = recetaApprovalRepository.findUnapprovedRecetas();
        return recetasNoAprobadas.stream()
                .map(approval -> recetaMapper.recetaToRecetaResponseDTO(approval.getReceta()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResponseDTO> getRecetasNoAprobadasByUsuario(Integer idUsuario) {
        List<RecetaApproval> recetasNoAprobadas = recetaApprovalRepository.findUnapprovedRecetasByUsuario(idUsuario);
        return recetasNoAprobadas.stream()
                .map(approval -> recetaMapper.recetaToRecetaResponseDTO(approval.getReceta()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void aprobarReceta(Integer idReceta) {
        RecetaApproval approval = recetaApprovalRepository.findById(idReceta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECETA_NOT_FOUND,
                        "Receta con ID " + idReceta + " no encontrada."
                ));

        if (approval.getApproved()) {
            throw new BadRequestException(
                    ErrorCode.RECETA_ALREADY_APPROVED,
                    "La receta con ID " + idReceta + " ya está aprobada."
            );
        }

        approval.setApproved(true);
        recetaApprovalRepository.save(approval);

        try {

            Usuario usuarioToSendNot = usuarioService.getUsuarioById(approval.getReceta().getUsuario().getIdUsuario());
            UserSession lastSesion = userSessionRepository.findLastUserSessionByUser(usuarioToSendNot).getFirst();

            notificationRepository.sendNotification(lastSesion.getFmc(),
                    "Receta aprobada",
                    "La receta  ha sido aprobada por el administrador.");

        } catch (Exception e) {
            log.error("Error al enviar notificación de aprobación de receta: " + e.getMessage());
        }
    }
}
