package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.entity.Alumno;
import com.uade.cookitbackend.entity.Usuario;
import org.mapstruct.*;

// AlumnoMapper.java
@Mapper(componentModel = "spring")
public interface AlumnoMapper {
    AlumnoResponseDTO toResponseDTO(Alumno alumno);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "asistencias", ignore = true)
    void updateEntityFromDTO(AlumnoUpdateDTO dto, @MappingTarget Alumno alumno);

    default Alumno toEntityFromComposedDTO(com.uade.cookitbackend.dto.AlumnoWithUsuarioDTO dto, Usuario usuario) {
        Alumno alumno = new Alumno();
        alumno.setUsuario(usuario);
        alumno.setNumeroTarjeta(dto.getNumeroTarjeta());
        alumno.setDniFrente(dto.getDniFrente());
        alumno.setDniFondo(dto.getDniFondo());
        alumno.setTramite(dto.getTramite());
        alumno.setCuentaCorriente(dto.getCuentaCorriente());
        return alumno;
    }
}
