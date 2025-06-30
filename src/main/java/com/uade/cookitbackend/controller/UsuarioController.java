package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.enums.EstadoHabilitado;
import com.uade.cookitbackend.exception.ApiError;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.exception.UnauthorizedException;
import com.uade.cookitbackend.service.AlumnoService;
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
    private final AlumnoService alumnoService;
    private final JwtUtil jwtUtil;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    @Operation(
        summary = "Autenticar usuario en el sistema",
        description = """
            Autentica un usuario registrado usando email y contraseña.
            
            **Flujo de autenticación:**
            1. Valida credenciales (email + password)
            2. Verifica que el usuario esté habilitado
            3. Genera token JWT con validez de 24 horas
            4. Crea sesión activa con token FCM (opcional)
            
            **El token JWT incluye:**
            - ID del usuario
            - Email del usuario
            - Tiempo de expiración (24h)
            
            **Usar el token en requests posteriores:**
            ```
            Authorization: Bearer <token>
            ```
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa - Token JWT generado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Formato de datos incorrecto o campos faltantes",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "No existe usuario con ese email",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Contraseña incorrecta o usuario no habilitado",
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
        response.setIsAlumno(alumnoService.isUsuarioAlumno(usuario.getIdUsuario()));
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

    @Operation(
        summary = "🔐 Solicitar recuperación de contraseña",
        description = """
            Inicia el proceso de recuperación de contraseña enviando un código de verificación por email.
            
            **🔄 Flujo de recuperación de contraseña:**
            ```
            1. [AQUÍ] /reset-password → Solicitar código
            2. /reset-password/check-code → Verificar código
            3a. /reset-password/confirm → Solo cambiar contraseña
            3b. /reset-password/complete → Cambiar contraseña + completar datos
            ```
            
            **📨 Proceso:**
            1. Verifica que el email esté registrado
            2. Genera código de 6 dígitos
            3. Envía email con el código
            4. El código expira en 24 horas
            
            **💡 Casos de uso:**
            - Usuario olvidó su contraseña
            - Usuario registrado parcialmente (stage 1 completado pero no stage 2)
            - Usuario necesita actualizar datos faltantes
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "✅ Código de recuperación enviado por email"),
            @ApiResponse(responseCode = "404", description = "❌ No existe usuario registrado con ese email",
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

    @Operation(
        summary = "📧 Etapa 1: Iniciar registro con email y nickname",
        description = """
            **Primera etapa del registro en 3 pasos** - Inicia el proceso de registro verificando email y nickname únicos.
            
            **📋 Flujo completo de registro:**
            ```
            1. [AQUÍ] /register/stage1 → Enviar email y nickname
            2. /register/check-code → Verificar código de 6 dígitos
            3. /register/stage2 → Completar datos y generar token
            ```
            
            **🔍 Validaciones:**
            - Email único en el sistema
            - Nickname único (3-100 caracteres)
            - Formato de email válido
            
            **📨 Proceso:**
            1. Crea usuario temporal (habilitado=No)
            2. Genera código de verificación de 6 dígitos
            3. Envía email con el código
            4. El código expira en 24 horas
            
            **⏭️ Siguiente paso:** Usar `/register/check-code` con el código recibido
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "✅ Usuario temporal creado - Código de verificación enviado por email",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterStage1ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "❌ Datos de entrada inválidos (email malformado, nickname muy corto, etc.)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "⚠️ Email o nickname ya existe - Incluye sugerencias de nicknames alternativos",
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

    @Operation(
        summary = "✅ Etapa 2: Verificar código de registro",
        description = """
            **Segunda etapa del registro en 3 pasos** - Verifica el código de 6 dígitos enviado por email.
            
            **📋 Flujo completo de registro:**
            ```
            1. /register/stage1 → ✅ Completado
            2. [AQUÍ] /register/check-code → Verificar código de 6 dígitos
            3. /register/stage2 → Completar datos y generar token
            ```
            
            **🔐 Proceso de verificación:**
            1. Valida el código de 6 dígitos
            2. Verifica que no haya expirado (24h límite)
            3. Habilita el usuario temporalmente (habilitado=Si)
            4. Limpia el código de verificación del sistema
            
            **⚠️ Importante:**
            - El código solo se puede usar una vez
            - Después de verificar, tienes tiempo limitado para completar el Stage 2
            - Si el código expira, debes volver a Stage 1
            
            **⏭️ Siguiente paso:** Usar `/register/stage2` para completar datos del usuario
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "✅ Código verificado correctamente - Usuario habilitado para completar registro"),
            @ApiResponse(responseCode = "401", description = "❌ Código inválido, expirado o ya utilizado",
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

    @Operation(
        summary = "🎯 Etapa 3: Completar registro y generar sesión",
        description = """
            **Tercera y última etapa del registro en 3 pasos** - Completa los datos del usuario y genera token de sesión.
            
            **📋 Flujo completo de registro:**
            ```
            1. /register/stage1 → ✅ Completado  
            2. /register/check-code → ✅ Completado
            3. [AQUÍ] /register/stage2 → Completar datos y generar token
            ```
            
            **👤 Datos requeridos:**
            - Password (8-30 caracteres)
            - Nombre completo
            - Dirección (opcional)
            - Avatar URL (opcional)
            - Token FCM para notificaciones (opcional)
            
            **🔐 Proceso final:**
            1. Verifica que el usuario esté habilitado (stage 2 completado)
            2. Encripta y guarda la contraseña
            3. Completa todos los datos del perfil
            4. Genera token JWT con validez de 24 horas
            5. Crea sesión activa (si se proporciona FCM token)
            
            **🎉 Resultado:** Usuario completamente registrado y autenticado
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "🎉 Registro completado exitosamente - Usuario creado y autenticado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "❌ Datos inválidos (contraseña muy corta, email incorrecto, etc.)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "⚠️ Código de verificación no validado - Debe completar Stage 2 primero",
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

    @Operation(
        summary = "Obtener usuario por nickname",
        description = """
            Obtiene la información pública de un usuario específico mediante su nickname.
            
            **Características:**
            - Endpoint público (no requiere autenticación)
            - Devuelve información básica del perfil del usuario
            - Útil para mostrar perfiles públicos de otros usuarios
            
            **Casos de uso:**
            - Mostrar perfil de autor de una receta
            - Buscar usuarios por nickname
            - Mostrar información de usuario en comentarios
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el nickname especificado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping(
            path = "/nickname/{nickname}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserProfileResponseDTO> getUserByNickname(
            @PathVariable String nickname
    ) {
        Usuario usuario = usuarioService.getUsuarioByNickname(nickname);
        UserProfileResponseDTO response = usuarioMapper.toUserProfileResponseDTO(usuario);
        return ResponseEntity.ok(response);
    }
}
