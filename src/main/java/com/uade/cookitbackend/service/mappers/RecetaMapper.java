package com.uade.cookitbackend.service.mappers;


import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.PasoDto;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.Paso;
import com.uade.cookitbackend.entity.Receta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PasoMapper.class, IngredienteMapper.class})
public interface RecetaMapper {

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "tipoReceta", ignore = true)
    @Mapping(target = "ingredientesUtilizados", ignore = true)
    @Mapping(target = "pasos", ignore = true)
    @Mapping(target = "fotos", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    Receta toEntity(CreateRecetaDTO createRecetaDTO);

    @Mapping(source = "usuario.nickname", target = "usuarioNickname")
    @Mapping(source = "tipoReceta.descripcion", target = "tipoRecetaDescripcion")
    @Mapping(source = "ingredientesUtilizados", target = "ingredientesUtilizados")
    RecetaResponseDTO recetaToRecetaResponseDTO(Receta receta);

    // Nuevo: mapeo sin pasos
    @Mapping(source = "usuario.nickname", target = "usuarioNickname")
    @Mapping(source = "tipoReceta.descripcion", target = "tipoRecetaDescripcion")
    @Mapping(source = "ingredientesUtilizados", target = "ingredientesUtilizados")
    @Mapping(target = "pasos", ignore = true)
    RecetaResponseDTO recetaToRecetaResponseDTOSinPasos(Receta receta);
}
