package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.dto.PasswordResetCompleteDTO;
import com.uade.cookitbackend.dto.RegisterStage1DTO;
import com.uade.cookitbackend.dto.RegisterStage2DTO;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.enums.EstadoHabilitado;
import com.uade.cookitbackend.exception.*;
import com.uade.cookitbackend.repository.db.UsuarioRepository;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.service.mappers.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario createUsuario(CreateUsuarioDTO createUsuarioDTO) {
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

        if (usuarioRepository.existsByNickname(createUsuarioDTO.getNickname())) {
            List<String> sugerencias = sugerirNicknames(createUsuarioDTO.getNickname());
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Nickname already exists: " + createUsuarioDTO.getNickname(),
                    sugerencias
            );
        }

        Usuario usuario = usuarioMapper.toEntity(createUsuarioDTO);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "El usuario no pudo guardarse (posible duplicado de mail/nickname)"
            );
        }

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

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
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

    @Override
    @Transactional
    public Usuario createUsuarioStage1(RegisterStage1DTO registerStage1DTO) {
        // Verificar que no exista email duplicado
        Usuario usuarioExistente = usuarioRepository.findByMail(registerStage1DTO.getMail()).orElse(null);
        if (usuarioExistente != null) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Email already exists: " + registerStage1DTO.getMail()
            );
        }

        // Verificar que no exista nickname duplicado
        if (usuarioRepository.existsByNickname(registerStage1DTO.getNickname())) {
            List<String> sugerencias = sugerirNicknames(registerStage1DTO.getNickname());
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Nickname already exists: " + registerStage1DTO.getNickname(),
                    sugerencias
            );
        }

        // Crear usuario con datos básicos y habilitado = No
        Usuario usuario = new Usuario();
        usuario.setMail(registerStage1DTO.getMail());
        usuario.setNickname(registerStage1DTO.getNickname());
        usuario.setHabilitado(EstadoHabilitado.No); // Usuario NO habilitado hasta validar código
        // Establecer contraseña temporal para cumplir con NOT NULL constraint (máximo 40 chars en DB)
        String tempPassword = "TEMP_" + UUID.randomUUID().toString().substring(0, 8);
        usuario.setPassword(tempPassword);
        // Otros campos quedan en null hasta el stage2

        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "El usuario no pudo guardarse (posible duplicado de mail/nickname)"
            );
        }

        return usuario;
    }

    @Override
    @Transactional
    public void validateRegistrationCode(String mail, String codigo) {
        // Validar que el usuario existe en estado No habilitado
        Usuario usuario = usuarioRepository.findByMail(mail).orElseThrow(
                () -> new ResourceNotFoundException(
                        ErrorCode.USUARIO_NOT_FOUND,
                        "Usuario no encontrado con email: " + mail
                )
        );

        if (usuario.getHabilitado() == EstadoHabilitado.Si) {
            throw new ValidationException("El usuario ya está habilitado");
        }

        // Validar código
        if (!verificationService.validateCode(mail, codigo)) {
            throw new UnauthorizedException(
                    ErrorCode.INVALID_VERIFICATION_CODE,
                    "Código de verificación inválido o expirado"
            );
        }

        // Habilitar usuario
        usuario.setHabilitado(EstadoHabilitado.Si);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario completeUsuarioStage2(RegisterStage2DTO registerStage2DTO) {
        // Buscar usuario existente
        Usuario usuario = usuarioRepository.findByMail(registerStage2DTO.getMail()).orElseThrow(
                () -> new ResourceNotFoundException(
                        ErrorCode.USUARIO_NOT_FOUND,
                        "Usuario no encontrado con email: " + registerStage2DTO.getMail()
                )
        );

        // Verificar que el usuario esté habilitado
        if (usuario.getHabilitado() != EstadoHabilitado.Si) {
            throw new UnauthorizedException(
                    ErrorCode.USER_NOT_ENABLED,
                    "Debe validar el código de verificación antes de completar el registro"
            );
        }

        // Completar datos del usuario
        usuario.setPassword(passwordEncoder.encode(registerStage2DTO.getPassword()));
        usuario.setNombre(registerStage2DTO.getNombre());
        usuario.setDireccion(registerStage2DTO.getDireccion());
        usuario.setAvatar(registerStage2DTO.getAvatar());

        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Error al actualizar el usuario"
            );
        }

        return usuario;
    }

    @Override
    public boolean isUserIncomplete(String mail) {
        Usuario usuario = usuarioRepository.findByMail(mail).orElse(null);
        if (usuario == null) return false;
        if (usuario.getHabilitado() != EstadoHabilitado.Si) return false;
        
        // Verificar si faltan campos esenciales (password o nombre)
        return (usuario.getPassword() == null || usuario.getPassword().isEmpty()) ||
               (usuario.getNombre() == null || usuario.getNombre().isEmpty());
    }

    @Override
    @Transactional
    public Usuario completeUserViaPasswordReset(PasswordResetCompleteDTO dto) {
        // El código ya fue validado en check-code, no necesitamos validarlo nuevamente

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByMail(dto.getMail()).orElseThrow(
                () -> new ResourceNotFoundException(
                        ErrorCode.USUARIO_NOT_FOUND,
                        "Usuario no encontrado con email: " + dto.getMail()
                )
        );

        // Verificar que el usuario esté habilitado pero incompleto
        if (usuario.getHabilitado() != EstadoHabilitado.Si) {
            throw new UnauthorizedException(
                    ErrorCode.USER_NOT_ENABLED,
                    "Usuario no habilitado"
            );
        }

        if (!isUserIncomplete(dto.getMail())) {
            throw new ValidationException(
                    "El usuario ya tiene todos los datos completos. Use el reset de password normal."
            );
        }

        // Completar datos del usuario
        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuario.setNombre(dto.getNombre());
        usuario.setDireccion(dto.getDireccion());
        usuario.setAvatar(dto.getAvatar());

        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Error al actualizar el usuario"
            );
        }

        return usuario;
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
