package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.CreateTarjetaCreditoAlumnoDTO;
import com.uade.cookitbackend.dto.TarjetaCreditoAlumnoResponseDTO;
import com.uade.cookitbackend.dto.UpdateTarjetaCreditoAlumnoDTO;
import com.uade.cookitbackend.entity.Alumno;
import com.uade.cookitbackend.entity.TarjetaCreditoAlumno;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TarjetaCreditoAlumnoMapper {
    
    @Mapping(target = "idAlumno", source = "alumno.idAlumno")
    @Mapping(target = "nombreAlumno", source = "alumno.usuario.nombre")
    TarjetaCreditoAlumnoResponseDTO toResponseDTO(TarjetaCreditoAlumno tarjetaCreditoAlumno);

    @Mapping(target = "idTarjetaCredito", ignore = true)
    @Mapping(target = "alumno", source = "alumno")
    @Mapping(target = "numeroTarjeta", source = "dto.numeroTarjeta")
    @Mapping(target = "cvv", source = "dto.cvv")
    @Mapping(target = "fechaVencimiento", source = "dto.fechaVencimiento")
    TarjetaCreditoAlumno toEntity(CreateTarjetaCreditoAlumnoDTO dto, Alumno alumno);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idTarjetaCredito", ignore = true)
    @Mapping(target = "alumno", ignore = true)
    void updateEntityFromDTO(UpdateTarjetaCreditoAlumnoDTO dto, @MappingTarget TarjetaCreditoAlumno tarjetaCreditoAlumno);
}