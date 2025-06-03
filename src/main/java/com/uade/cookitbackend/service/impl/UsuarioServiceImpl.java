package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.exception.UnauthorizedException;
import com.uade.cookitbackend.repository.db.UsuarioRepository;
import com.uade.cookitbackend.service.EmailService;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.service.VerificationService;
import com.uade.cookitbackend.service.mappers.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final EmailService emailService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final VerificationService verificationService;

    @Override
    @Transactional
    public Usuario createUsuario(CreateUsuarioDTO createUsuarioDTO) {
        // 1. Validar duplicado de email
        if (usuarioRepository.existsByMail(createUsuarioDTO.getMail())) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Email already exists: " + createUsuarioDTO.getMail()
            );
        }

        // 2. Validar duplicado de nickname
        if (usuarioRepository.existsByNickname(createUsuarioDTO.getNickname())) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Nickname already exists: " + createUsuarioDTO.getNickname()
            );
        }

        // 3. Mapear y persistir usuario
        Usuario usuario = usuarioMapper.toEntity(createUsuarioDTO);
        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            // Si por algún motivo la base de datos lanza violación (índices únicos),
            // la capturamos aquí y devolvemos un mensaje genérico o detallado.
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
