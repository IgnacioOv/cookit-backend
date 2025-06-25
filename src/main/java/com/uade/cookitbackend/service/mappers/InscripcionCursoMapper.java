package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.InscripcionCursoResponseDTO;
import com.uade.cookitbackend.entity.InscripcionCurso;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InscripcionCursoMapper {
    @Mapping(source = "alumno.idAlumno", target = "idAlumno")
    @Mapping(source = "cronograma.idCronograma", target = "idCronograma")
    InscripcionCursoResponseDTO toDTO(InscripcionCurso entity);
}