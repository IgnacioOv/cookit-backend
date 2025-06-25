package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.PasoDto;
import com.uade.cookitbackend.entity.Paso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MultimediaMapper.class)
public interface PasoMapper {
    @Mapping(target = "receta", ignore = true)
    Paso toEntity(PasoDto createPasoDTO);
    @Mapping(target = "idReceta", source = "receta.idReceta")
    PasoDto toDto(Paso paso);

}
