package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.repository.UsuarioRepository;
import com.uade.cookitbackend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Usuario createUsuario(CreateUsuarioDTO createUsuarioDTO) {
        // Create new usuario
        Usuario usuario = new Usuario();
        usuario.setMail(createUsuarioDTO.getMail());
        usuario.setNickname(createUsuarioDTO.getNickname());
        usuario.setPassword(createUsuarioDTO.getPassword()); // In a real app, you should hash the password
        usuario.setHabilitado(createUsuarioDTO.getHabilitado());
        usuario.setNombre(createUsuarioDTO.getNombre());
        usuario.setDireccion(createUsuarioDTO.getDireccion());
        usuario.setAvatar(createUsuarioDTO.getAvatar());

        return usuarioRepository.save(usuario);
    }
} 