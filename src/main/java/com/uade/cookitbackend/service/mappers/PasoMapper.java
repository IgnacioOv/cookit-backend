package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.PasoDto;
import com.uade.cookitbackend.entity.Paso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = MultimediaMapper.class)
public interface PasoMapper {
    PasoMapper INSTANCE = Mappers.getMapper(PasoMapper.class);
    @Mapping(target = "receta.idReceta", source = "idReceta")
    Paso toEntity(PasoDto createPasoDTO);
    @Mapping(target = "idReceta", source = "receta.idReceta")
    PasoDto toDto(Paso paso);

}
