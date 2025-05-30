package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.repository.db.UsuarioRepository;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.service.mappers.UsuarioMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public Usuario createUsuario(CreateUsuarioDTO createUsuarioDTO) {
        try {
            Usuario usuario = usuarioMapper.toEntity(createUsuarioDTO);
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Email already exists: " + createUsuarioDTO.getMail());
        }
    }

    @Override
    public Usuario getUsuarioById(Integer id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Usuario not found with id: " + id));
    }

    @Override
    public Usuario login(String mail, String password) {
        Usuario usuario = usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario not found with email: " + mail));
        if (!usuario.getPassword().equals(password)) {
            throw new EntityNotFoundException("Invalid credentials");
        }
        return usuario;
    }

    @Override
    public Usuario getUsuarioByMail(String mail) {
        return usuarioRepository.findByMail(mail).orElse(null);
    }

    @Override
    @Transactional
    public Usuario updateUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

}
