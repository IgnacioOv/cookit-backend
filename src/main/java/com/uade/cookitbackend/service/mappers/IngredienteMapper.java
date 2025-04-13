package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.IngredienteDto;
import com.uade.cookitbackend.entity.IngredienteUtilizado;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IngredienteMapper {
    IngredienteMapper INSTANCE = Mappers.getMapper(IngredienteMapper.class);

    IngredienteDto toDto(IngredienteUtilizado ingredienteUtilizado);

    IngredienteUtilizado toEntity(IngredienteDto ingredienteDto);
}