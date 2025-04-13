package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.MultimediaDTO;
import com.uade.cookitbackend.entity.Multimedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MultimediaMapper {

    MultimediaMapper INSTANCE = Mappers.getMapper(MultimediaMapper.class);

    @Mapping(target = "idPaso", source = "paso.idPaso")
    MultimediaDTO toDto(Multimedia multimedia);
    Multimedia toEntity(MultimediaDTO multimediaDTO);
}
