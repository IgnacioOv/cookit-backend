package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.Foto;
import com.uade.cookitbackend.entity.IngredienteUtilizado;
import com.uade.cookitbackend.entity.Paso;
import com.uade.cookitbackend.entity.Receta;
import com.uade.cookitbackend.entity.TipoReceta;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.IngredienteRepository;
import com.uade.cookitbackend.repository.db.RecetaRepository;
import com.uade.cookitbackend.repository.db.UnidadRepository;
import com.uade.cookitbackend.service.RecetaService;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.service.impl.TipoRecetaServiceImpl;
import com.uade.cookitbackend.service.mappers.RecetaMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final RecetaMapper recetaMapper = RecetaMapper.INSTANCE;
    private final UsuarioService usuarioService;
    private final TipoRecetaServiceImpl tipoRecetaServiceImpl;
    private final IngredienteRepository ingredienteRepository;
    private final UnidadRepository unidadRepository;

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

        return recetaMapper.recetaToRecetaResponseDTO(savedReceta);
    }

    @Override
    public List<RecetaResponseDTO> getRecetasByNombre(String nombreReceta) {
        List<Receta> recetas = recetaRepository
                .findByNombreRecetaContainingIgnoreCaseOrderByIdRecetaDesc(nombreReceta);

        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResponseDTO> getRecetaByIdUsuario(Integer userId) {
        List<Receta> recetas = recetaRepository.findRecetaByUsuario_IdUsuario(userId);
        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResponseDTO> getRecetasWithoutIngrediente(String ingrediente, String orden) {
        Sort sort = orden.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, "idReceta")
                : Sort.by(Sort.Direction.DESC, "idReceta");

        List<Receta> recetas = recetaRepository.findRecetasSinIngrediente(ingrediente, sort);
        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResponseDTO> getRecetasWithIngrediente(String ingrediente, String orden) {
        Sort sort = orden.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, "idReceta")
                : Sort.by(Sort.Direction.DESC, "idReceta");

        List<Receta> recetas = recetaRepository.findRecetasConIngrediente(ingrediente, sort);
        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RecetaResponseDTO getRecetaById(Integer id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECETA_NOT_FOUND,
                        "Receta con ID " + id + " no encontrada."
                ));
        return recetaMapper.recetaToRecetaResponseDTO(receta);
    }

    @Override
    public List<RecetaResponseDTO> getFeed() {
        List<Receta> recetas = recetaRepository.findAll(Sort.by(Sort.Direction.DESC, "idReceta"));
        return recetaMapper.recetaToRecetaResponseDTO(recetas);
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
}
