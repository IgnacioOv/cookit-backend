package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.entity.Alumno;
import com.uade.cookitbackend.entity.Usuario;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AlumnoMapper {
    AlumnoResponseDTO toResponseDTO(Alumno alumno);

    @Mapping(target = "idAlumno", ignore = true)
    @Mapping(target = "asistencias", ignore = true)
    Alumno toEntity(AlumnoCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AlumnoUpdateDTO dto, @MappingTarget Alumno alumno);

    // Este método default te deja setear el usuario manualmente
    default Alumno toEntity(AlumnoCreateDTO dto, Usuario usuario) {
        Alumno alumno = toEntity(dto);
        alumno.setUsuario(usuario);
        return alumno;
    }
}
