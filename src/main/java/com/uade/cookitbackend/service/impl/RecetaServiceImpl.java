package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.*;
import com.uade.cookitbackend.repository.db.RecetaRepository;
import com.uade.cookitbackend.repository.db.IngredienteRepository;
import com.uade.cookitbackend.repository.db.UnidadRepository;
import com.uade.cookitbackend.service.RecetaService;
import com.uade.cookitbackend.service.UsuarioService;
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
        TipoReceta tipoReceta = tipoRecetaServiceImpl.getTipoRecetaById(createRecetaDTO.getIdTipo());

        Receta receta = recetaMapper.toEntity(createRecetaDTO);
        receta.setUsuario(usuario);
        receta.setTipoReceta(tipoReceta);

        if (createRecetaDTO.getIngredientesUtilizados() != null) {
            List<IngredienteUtilizado> ingredientesUtilizados = createRecetaDTO.getIngredientesUtilizados().stream().map(dto -> {
                IngredienteUtilizado iu = new IngredienteUtilizado();
                iu.setReceta(receta);
                iu.setCantidad(dto.getCantidad());
                iu.setObservaciones(dto.getObservaciones());
                if (dto.getIdIngrediente() != null) {
                    iu.setIngrediente(ingredienteRepository.findById(dto.getIdIngrediente()).orElse(null));
                }
                if (dto.getIdUnidad() != null) {
                    iu.setUnidad(unidadRepository.findById(dto.getIdUnidad()).orElse(null));
                }
                return iu;
            }).collect(Collectors.toList());
            receta.setIngredientesUtilizados(ingredientesUtilizados);
        }

        // Persistir foto principal como entidad Foto
        if (createRecetaDTO.getFotoPrincipal() != null && !createRecetaDTO.getFotoPrincipal().isEmpty()) {
            Foto foto = crearFotoPrincipal(createRecetaDTO.getFotoPrincipal(), receta);
            receta.setFotos(List.of(foto));
        }

        for (Paso paso : receta.getPasos()) {
            paso.setReceta(receta);
            if (paso.getMultimedia() != null) {
                for (Multimedia multimedia : paso.getMultimedia()) {
                    multimedia.setPaso(paso);
                }
            }
        }

        val savedReceta = recetaRepository.save(receta);
        return recetaMapper.recetaToRecetaResponseDTO(savedReceta);
    }

    @Override
    public List<RecetaResponseDTO> getRecetasByNombre(String nombreReceta) {
        List<Receta> recetas = recetaRepository.findByNombreRecetaContainingIgnoreCaseOrderByIdRecetaDesc(nombreReceta);
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

        List<Receta> recetas = recetaRepository.findRecetasConIngrediente(ingrediente,sort);
        return recetas.stream()
                .map(recetaMapper::recetaToRecetaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RecetaResponseDTO getRecetaById(Integer id) {
        Optional<Receta> recetaOptional = recetaRepository.findById(id);
        if (recetaOptional.isPresent()) {
            Receta receta = recetaOptional.get();
            return recetaMapper.recetaToRecetaResponseDTO(receta);
        } else {
            throw new EntityNotFoundException(
                    "Receta con ID " + id + " no encontrada."
            );
        }
    }

    private Foto crearFotoPrincipal(String urlFoto, Receta receta) {
        Foto foto = new Foto();
        foto.setReceta(receta);
        foto.setUrlFoto(urlFoto);
        String extension = null;
        int lastDot = urlFoto.lastIndexOf('.');
        if (lastDot != -1 && lastDot < urlFoto.length() - 1) {
            extension = urlFoto.substring(lastDot + 1);
            if (extension.length() > 5) extension = extension.substring(0, 5);
        }
        foto.setExtension(extension);
        return foto;
    }
}
