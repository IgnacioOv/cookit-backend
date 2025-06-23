package com.uade.cookitbackend.service.mappers;


import com.uade.cookitbackend.dto.CronogramaCursoResponseDTO;
import com.uade.cookitbackend.entity.CronogramaCurso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CronogramaCursoMapper {
    @Mapping(source = "sede.idSede", target = "idSede")
    @Mapping(source = "sede.nombreSede", target = "nombreSede")
    CronogramaCursoResponseDTO toDTO(CronogramaCurso entity);
}
