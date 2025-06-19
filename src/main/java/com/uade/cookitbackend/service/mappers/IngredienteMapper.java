package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.IngredienteUtilizadoDto;
import com.uade.cookitbackend.entity.IngredienteUtilizado;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IngredienteMapper {
    IngredienteMapper INSTANCE = Mappers.getMapper(IngredienteMapper.class);

    IngredienteUtilizadoDto toDto(IngredienteUtilizado ingredienteUtilizado);

    IngredienteUtilizado toEntity(IngredienteUtilizadoDto ingredienteUtilizadoDto);
}

