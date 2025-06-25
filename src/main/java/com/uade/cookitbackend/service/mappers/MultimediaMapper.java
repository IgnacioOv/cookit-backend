package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.MultimediaDTO;
import com.uade.cookitbackend.entity.Multimedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MultimediaMapper {

    @Mapping(target = "idPaso", source = "paso.idPaso")
    MultimediaDTO toDto(Multimedia multimedia);
    @Mapping(target = "paso", ignore = true)
    Multimedia toEntity(MultimediaDTO multimediaDTO);
}
