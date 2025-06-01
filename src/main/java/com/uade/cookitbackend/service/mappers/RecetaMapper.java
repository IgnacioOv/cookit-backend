package com.uade.cookitbackend.service.mappers;


import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.PasoDto;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.Paso;
import com.uade.cookitbackend.entity.Receta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {PasoMapper.class, IngredienteMapper.class})

public interface RecetaMapper {

    RecetaMapper INSTANCE = Mappers.getMapper(RecetaMapper.class);

    Receta toEntity(CreateRecetaDTO createRecetaDTO);

    @Mapping(source = "receta.usuario.nickname", target = "usuarioNickname")
    @Mapping(source = "receta.tipoReceta.descripcion", target = "tipoRecetaDescripcion")
    @Mapping(source = "receta.ingredientesUtilizados", target = "ingredientesUtilizados")
    RecetaResponseDTO recetaToRecetaResponseDTO(Receta receta);

}
