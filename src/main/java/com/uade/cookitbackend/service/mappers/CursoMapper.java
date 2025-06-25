package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.CursoResponseDTO;
import com.uade.cookitbackend.entity.Curso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CronogramaCursoMapper.class})
public interface CursoMapper {
    CursoResponseDTO toDTO(Curso entity);
}