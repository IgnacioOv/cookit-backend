package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.IngredienteUtilizadoDto;
import com.uade.cookitbackend.entity.IngredienteUtilizado;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    IngredienteUtilizadoDto toDto(IngredienteUtilizado ingredienteUtilizado);

    IngredienteUtilizado toEntity(IngredienteUtilizadoDto ingredienteUtilizadoDto);
}

