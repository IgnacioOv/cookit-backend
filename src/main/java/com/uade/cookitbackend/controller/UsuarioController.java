package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.enums.EstadoHabilitado;
import com.uade.cookitbackend.exception.ApiError;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.exception.UnauthorizedException;
import com.uade.cookitbackend.service.SessionService;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.config.JwtUtil;
import com.uade.cookitbackend.service.impl.VerificationService;
import com.uade.cookitbackend.service.mappers.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuario", description = "API para gestionar usuarios")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final SessionService sessionService;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Registro de un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Email duplicado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserSessionResponse> register(
            @Valid @RequestBody CreateUsuarioDTO createUsuarioDTO
    ) {
        Usuario createdUsuario = usuarioService.createUsuario(createUsuarioDTO);
        String token = jwtUtil.generateToken(
                createdUsuario.getIdUsuario(),
                createdUsuario.getMail()
        );
        sessionService.newSession(createUsuarioDTO.getFcm(), createdUsuario);

        UserSessionResponse response = new UserSessionResponse();
        response.setToken(token);
        response.setTtl("86400"); // 1 día en segundos
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Login de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserSessionResponse> login(
            @Valid @RequestBody UserLogin usuarioLogin
    ) {
        Usuario usuario = usuarioService.login(
                usuarioLogin.getMail(),
                usuarioLogin.getPassword()
        );
        sessionService.newSession(usuarioLogin.getFcm(), usuario);

        String token = jwtUtil.generateToken(
                usuario.getIdUsuario(),
                usuario.getMail()
        );
        UserSessionResponse response = new UserSessionResponse();
        response.setToken(token);
        response.setTtl("86400");
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil obtenido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido o ausente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping(
            path = "/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserProfileResponseDTO> profile(
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(
                    ErrorCode.UNAUTHORIZED,
                    "Authorization header missing or invalid"
            );
        }
        String token = authHeader.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new UnauthorizedException(
                    ErrorCode.UNAUTHORIZED,
                    "Invalid token"
            );
        }

        Usuario usuario = usuarioService.getUsuarioById(userId);
        UserProfileResponseDTO response = usuarioMapper.toUserProfileResponseDTO(usuario);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Configurar datos del usuario (mock, sin lógica interna)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración guardada"),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(
            path = "/config",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> setUserConfig(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserConfig config
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(
                    ErrorCode.UNAUTHORIZED,
                    "Authorization header missing or invalid"
            );
        }
        String token = authHeader.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new UnauthorizedException(
                    ErrorCode.UNAUTHORIZED,
                    "Invalid token"
            );
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Solicitar código de recuperación de contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código generado y enviado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(
            path = "/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDTO dto
    ) {
        Usuario usuario = usuarioService.getUsuarioByMail(dto.getMail());
        if (usuario == null) {
            throw new ResourceNotFoundException(
                    ErrorCode.USUARIO_NOT_FOUND,
                    "Usuario no encontrado con email: " + dto.getMail()
            );
        }

        verificationService.generateAndStoreCodeResetPassword(dto.getMail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Validar código de recuperación de contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código válido - incluye si el usuario necesita completar datos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PasswordResetStatusDTO.class))),
            @ApiResponse(responseCode = "401", description = "Código inválido o expirado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/reset-password/check-code")
    public ResponseEntity<PasswordResetStatusDTO> checkPasswordResetCode(
            @Valid @RequestBody PasswordResetCodeDTO dto
    ) {
        boolean valid = verificationService.validateCode(dto.getMail(), dto.getCode());
        if (!valid) {
            throw new UnauthorizedException(
                    ErrorCode.INVALID_RESET_CODE,
                    "Código de recuperación inválido o expirado"
            );
        }

        // Verificar si el usuario necesita completar datos
        boolean needsCompletion = usuarioService.isUserIncomplete(dto.getMail());
        
        PasswordResetStatusDTO response = new PasswordResetStatusDTO(
                needsCompletion,
                needsCompletion ? 
                    "Usuario requiere completar datos de registro" : 
                    "Código válido, puede proceder a cambiar contraseña",
                dto.getMail()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Confirmar cambio de contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
            @ApiResponse(responseCode = "401", description = "Código inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<Void> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmDTO dto
    ) {
        boolean valid = verificationService.validateCode(dto.getMail(), dto.getCode());
        if (!valid) {
            throw new UnauthorizedException(
                    ErrorCode.INVALID_RESET_CODE,
                    "Código de recuperación inválido o expirado"
            );
        }

        Usuario usuario = usuarioService.getUsuarioByMail(dto.getMail());
        if (usuario == null) {
            throw new ResourceNotFoundException(
                    ErrorCode.USUARIO_NOT_FOUND,
                    "Usuario no encontrado con email: " + dto.getMail()
            );
        }

        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuarioService.updateUsuario(usuario);
        verificationService.removeCode(dto.getMail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Confirmar verificación de mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mail verificado"),
            @ApiResponse(responseCode = "401", description = "Código inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/mail-verify/confirm")
    public ResponseEntity<Void> confirmVerifyMail(
            @Valid @RequestBody VerifyMailConfirmDTO dto
    ) {
        boolean valid = verificationService.validateCode(dto.getMail(), dto.getCode());
        if (!valid) {
            throw new UnauthorizedException(
                    ErrorCode.INVALID_VERIFICATION_CODE,
                    "Código de verificación inválido o expirado"
            );
        }

        Usuario usuario = usuarioService.getUsuarioByMail(dto.getMail());
        if (usuario == null) {
            throw new ResourceNotFoundException(
                    ErrorCode.USUARIO_NOT_FOUND,
                    "Usuario no encontrado con email: " + dto.getMail()
            );
        }

        usuario.setHabilitado(EstadoHabilitado.Si);
        usuarioService.updateUsuario(usuario);
        verificationService.removeCode(dto.getMail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Primera etapa del registro - Verificación de email y alias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código de verificación enviado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterStage1ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Email o nickname duplicado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(
            path = "/register/stage1",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RegisterStage1ResponseDTO> registerStage1(
            @Valid @RequestBody RegisterStage1DTO registerStage1DTO
    ) {
        // Crear usuario con datos básicos (habilitado = No)
        Usuario usuario = usuarioService.createUsuarioStage1(registerStage1DTO);
        
        // Generar y enviar código de verificación
        verificationService.generateAndStoreCodeVerificationMail(usuario.getMail());

        RegisterStage1ResponseDTO response = new RegisterStage1ResponseDTO(
                "Código de verificación enviado exitosamente",
                registerStage1DTO.getMail(),
                "Revisa tu email y completa el registro con el código de 6 dígitos. El código expira en 24 horas."
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verificar código de registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código válido"),
            @ApiResponse(responseCode = "401", description = "Código inválido o expirado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/register/check-code")
    public ResponseEntity<Void> checkRegistrationCode(
            @Valid @RequestBody RegisterCheckCodeDTO dto
    ) {
        // Validar código y habilitar usuario en DB
        usuarioService.validateRegistrationCode(dto.getMail(), dto.getCodigo());
        
        // Limpiar código de verificación
        verificationService.removeCode(dto.getMail());
        
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Segunda etapa del registro - Completar datos y crear usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Código no validado previamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(
            path = "/register/stage2",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserSessionResponse> registerStage2(
            @Valid @RequestBody RegisterStage2DTO registerStage2DTO
    ) {
        // Completar datos del usuario (password, nombre, etc.)
        Usuario completedUsuario = usuarioService.completeUsuarioStage2(registerStage2DTO);
        
        // Generar token JWT
        String token = jwtUtil.generateToken(
                completedUsuario.getIdUsuario(),
                completedUsuario.getMail()
        );
        
        // Crear sesión si se proporcionó FCM token
        if (registerStage2DTO.getFcm() != null && !registerStage2DTO.getFcm().trim().isEmpty()) {
            sessionService.newSession(registerStage2DTO.getFcm(), completedUsuario);
        }

        UserSessionResponse response = new UserSessionResponse();
        response.setToken(token);
        response.setTtl("86400"); // 1 día en segundos
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Completar datos de usuario incompleto via reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario completado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Usuario ya completo o datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Código inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(
            path = "/reset-password/complete",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserSessionResponse> completeUserViaPasswordReset(
            @Valid @RequestBody PasswordResetCompleteDTO dto
    ) {
        // Completar datos del usuario incompleto
        Usuario completedUsuario = usuarioService.completeUserViaPasswordReset(dto);
        
        // Generar token JWT
        String token = jwtUtil.generateToken(
                completedUsuario.getIdUsuario(),
                completedUsuario.getMail()
        );
        
        // Limpiar código de verificación
        verificationService.removeCode(dto.getMail());

        UserSessionResponse response = new UserSessionResponse();
        response.setToken(token);
        response.setTtl("86400"); // 1 día en segundos
        return ResponseEntity.ok(response);
    }
}
