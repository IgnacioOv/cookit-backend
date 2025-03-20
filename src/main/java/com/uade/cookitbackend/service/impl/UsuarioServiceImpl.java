package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.repository.UsuarioRepository;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.service.mappers.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usarioMapper = UsuarioMapper.INSTANCE;

    @Override
    @Transactional
    public Usuario createUsuario(CreateUsuarioDTO createUsuarioDTO) {
        try {
            Usuario usuario = usarioMapper.toEntity(createUsuarioDTO);
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Email already exists: " + createUsuarioDTO.getMail());
        }
    }
}