package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.enums.EstadoHabilitado;
import com.uade.cookitbackend.exception.*;
import com.uade.cookitbackend.repository.db.UsuarioRepository;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.service.mappers.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final VerificationService verificationService;

    @Override
    @Transactional
    public Usuario createUsuario(CreateUsuarioDTO createUsuarioDTO) {
        // 1. Validar duplicado de email pero chequeando estado habilitado
        Usuario usuarioExistente = usuarioRepository.findByMail(createUsuarioDTO.getMail()).orElse(null);
        if (usuarioExistente != null) {
            if (usuarioExistente.getHabilitado() != null && usuarioExistente.getHabilitado() == EstadoHabilitado.No) {
                throw new UserNotEnabledException(
                        ErrorCode.USER_NOT_ENABLED,
                        "El usuario ya está registrado pero no completó la validación de correo. Por favor, contactá a soporte."
                );
            }
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Email already exists: " + createUsuarioDTO.getMail()
            );
        }

        // 2. Validar duplicado de nickname
        if (usuarioRepository.existsByNickname(createUsuarioDTO.getNickname())) {
            List<String> sugerencias = sugerirNicknames(createUsuarioDTO.getNickname());
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Nickname already exists: " + createUsuarioDTO.getNickname(),
                    sugerencias
            );
        }

        // 3. Mapear y persistir usuario
        Usuario usuario = usuarioMapper.toEntity(createUsuarioDTO);
        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "El usuario no pudo guardarse (posible duplicado de mail/nickname)"
            );
        }

        // 4. Generar y almacenar código de verificación
        verificationService.generateAndStoreCodeVerificationMail(usuario.getMail());
        return usuario;
    }



    @Override
    public Usuario getUsuarioById(Integer id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        ErrorCode.USUARIO_NOT_FOUND,
                        "Usuario not found with id: " + id
                )
        );
    }

    @Override
    public Usuario login(String mail, String password) {
        Usuario usuario = usuarioRepository.findByMail(mail).orElseThrow(
                () -> new ResourceNotFoundException(
                        ErrorCode.USUARIO_NOT_FOUND,
                        "Usuario not found with email: " + mail
                )
        );

        if (!usuario.getPassword().equals(password)) {
            throw new UnauthorizedException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Invalid credentials"
            );
        }

        if (usuario.getHabilitado() == null || usuario.getHabilitado() != EstadoHabilitado.Si) {
            throw new UserNotEnabledException(
                    ErrorCode.USER_NOT_ENABLED,
                    "El usuario no está habilitado. Por favor, completá el registro o contactá a soporte. cktspprt@gmail.com"
            );
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

        private List<String> sugerirNicknames(String base) {
            List<String> sugerencias = new ArrayList<>();
            for (int i = 1; i <= 20 && sugerencias.size() < 3; i++) {
                String sugerido = base + i;
                if (!usuarioRepository.existsByNickname(sugerido)) {
                    sugerencias.add(sugerido);
                }
            }
            return sugerencias;
        }
}
