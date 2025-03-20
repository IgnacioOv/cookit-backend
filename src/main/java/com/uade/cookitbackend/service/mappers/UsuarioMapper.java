package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);
    Usuario toEntity(CreateUsuarioDTO createUsuarioDTO);
    CreateUsuarioDTO toDto(Usuario usuario);
}
