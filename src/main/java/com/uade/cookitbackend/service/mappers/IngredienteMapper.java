package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.IngredienteNombreDto;
import com.uade.cookitbackend.dto.IngredienteUtilizadoDto;
import com.uade.cookitbackend.entity.Ingrediente;
import com.uade.cookitbackend.entity.IngredienteUtilizado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    @Mapping(source = "ingrediente.idIngrediente", target = "idIngrediente")
    @Mapping(source = "unidad.idUnidad", target = "idUnidad")
    IngredienteUtilizadoDto toDto(IngredienteUtilizado ingredienteUtilizado);

    @Mapping(target = "idUtilizado", ignore = true)
    @Mapping(target = "receta", ignore = true)
    @Mapping(target = "ingrediente", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    IngredienteUtilizado toEntity(IngredienteUtilizadoDto ingredienteUtilizadoDto);

    IngredienteNombreDto toIngredienteNombreDto(Ingrediente ingrediente);
}

