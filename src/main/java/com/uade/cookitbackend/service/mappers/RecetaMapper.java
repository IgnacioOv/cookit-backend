package com.uade.cookitbackend.service.mappers;


import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.PasoDto;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.Paso;
import com.uade.cookitbackend.entity.Receta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RecetaMapper {

    RecetaMapper INSTANCE = Mappers.getMapper(RecetaMapper.class);

    Receta toEntity(CreateRecetaDTO createRecetaDTO);
    Paso toEntity(PasoDto pasoDto);

    @Mapping(source = "receta.usuario.nickname", target = "usuarioNickname")
    @Mapping(source = "receta.tipoReceta.descripcion", target = "tipoRecetaDescripcion")
    RecetaResponseDTO recetaToRecetaResponseDTO(Receta receta);

}
