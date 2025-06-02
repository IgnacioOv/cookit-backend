package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.service.SessionService;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.config.JwtUtil;
import com.uade.cookitbackend.service.VerificationService;
import com.uade.cookitbackend.service.mappers.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuario", description = "API for managing users")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final SessionService sessionService;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final VerificationService passwordResetService;

    @Operation(summary = "Registro de un nuevo usuario")
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserSessionResponse> register(
            @Valid @RequestBody CreateUsuarioDTO createUsuarioDTO
    ) {
        Usuario createdUsuario = usuarioService.createUsuario(createUsuarioDTO);
        String token = jwtUtil.generateToken(createdUsuario.getIdUsuario(), createdUsuario.getMail());
        sessionService.newSession(createUsuarioDTO.getFcm(), createdUsuario);
        UserSessionResponse response = new UserSessionResponse();
        response.setToken(token);
        response.setTtl("86400"); // 1 día en segundos
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User login")
    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserSessionResponse> login(
            @Valid @RequestBody UserLogin usuarioLogin
    ) {
        Usuario usuario = usuarioService.login(usuarioLogin.getMail(), usuarioLogin.getPassword());
        sessionService.newSession(usuarioLogin.getFcm(), usuario);
        String token = jwtUtil.generateToken(usuario.getIdUsuario(), usuario.getMail());
        UserSessionResponse response = new UserSessionResponse();
        response.setToken(token);
        response.setTtl("86400"); // 1 día en segundos
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "User profile")
    @GetMapping(
            path = "/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserProfileResponseDTO> profile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioService.getUsuarioById(userId);
        UserProfileResponseDTO response = usuarioMapper.toUserProfileResponseDTO(usuario);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(
            path = "/config",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity setUserConfig(@RequestBody UserConfig config) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Solicitar código de recuperación de contraseña")
    @PostMapping("/reset-password")
    public ResponseEntity<String> requestPasswordReset(@RequestBody @Valid PasswordResetRequestDTO dto) {
        // Genera y almacena el código
        String code = passwordResetService.generateAndStoreCodeResetPassword(dto.getMail());
        // Devuelve el código en el body (solo para pruebas, no en producción)
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Validar código de recuperación de contraseña")
    @PostMapping("/reset-password/check-code")
    public ResponseEntity<Void> checkPasswordResetCode(@RequestBody @Valid PasswordResetCodeDTO dto) {
        boolean valid = passwordResetService.validateCode(dto.getMail(), dto.getCode());
        if (!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Confirmar cambio de contraseña")
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<Void> confirmPasswordReset(@RequestBody @Valid PasswordResetConfirmDTO dto) {
        boolean valid = passwordResetService.validateCode(dto.getMail(), dto.getCode());
        if (!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Usuario usuario = usuarioService.getUsuarioByMail(dto.getMail());
        if (usuario == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        usuario.setPassword(dto.getNewPassword());
        usuarioService.updateUsuario(usuario);
        passwordResetService.removeCode(dto.getMail());
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Confirmar cambio de contraseña")
    @PostMapping("/mail-verify/confirm")
    public ResponseEntity<Void> confirmVerifiMail(@RequestBody @Valid VerifyMailConfirmDTO dto) {
        boolean valid = passwordResetService.validateCode(dto.getMail(), dto.getCode());
        if (!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Usuario usuario = usuarioService.getUsuarioByMail(dto.getMail());
        if (usuario == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        passwordResetService.removeCode(dto.getMail());
        return ResponseEntity.ok().build();
    }

}
