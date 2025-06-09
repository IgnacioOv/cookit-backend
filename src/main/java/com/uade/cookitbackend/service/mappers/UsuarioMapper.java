package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.dto.UserProfileResponseDTO;
import com.uade.cookitbackend.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

// UsuarioMapper.java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(CreateUsuarioDTO createUsuarioDTO);
    CreateUsuarioDTO toDto(Usuario usuario);
    UserProfileResponseDTO toUserProfileResponseDTO(Usuario usuario);

    // Nuevo: convertir desde el DTO compuesto al DTO de usuario
    default CreateUsuarioDTO fromComposedDTO(com.uade.cookitbackend.dto.AlumnoWithUsuarioDTO dto) {
        CreateUsuarioDTO usuarioDTO = new CreateUsuarioDTO();
        usuarioDTO.setMail(dto.getMail());
        usuarioDTO.setNickname(dto.getNickname());
        usuarioDTO.setPassword(dto.getPassword());
        usuarioDTO.setNombre(dto.getNombre());
        usuarioDTO.setDireccion(dto.getDireccion());
        usuarioDTO.setAvatar(dto.getAvatar());
        usuarioDTO.setFcm(dto.getFcm());
        return usuarioDTO;
    }
}
