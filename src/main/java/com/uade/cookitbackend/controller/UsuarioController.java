package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Usuario", description = "API for managing users")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Registro de un nuevo usuario")
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> createUsuario(
        @Valid @RequestBody(
            description = "User creation data",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateUsuarioDTO.class))
        ) CreateUsuarioDTO createUsuarioDTO
    ) {
        Usuario createdUsuario = usuarioService.createUsuario(createUsuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsuario);
    }

    @Operation(summary = "User login")
    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> login(
        @Valid @RequestBody(
            description = "User login data",
            required = true,
            content = @Content(schema = @Schema(implementation = UserLogin.class))
        ) UserLogin createUsuarioDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "User profile")
    @GetMapping(
            path = "/profile",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> profile(@RequestHeader("Authorization") String token) {

        return ResponseEntity.status(HttpStatus.OK).body(new Usuario());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Recuperacion de password")
    @PostMapping(
            path = "/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> resetPassword(
    ) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Recuperacion de password checkcode")
    @PostMapping(
            path = "/reset-password/check-code",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> resetPasswordCheckCode(
        @Valid @RequestBody(
            description = "Reset code",
            required = true,
            content = @Content(schema = @Schema(implementation = ResetCode.class))
        ) ResetCode code
    ) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user configuration")
    @GetMapping(
            path = "/config",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> getUserConfig() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user configuration")
    @GetMapping(
            path = "/config/notifications",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> getUserConfigNotification() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user configuration")
    @GetMapping(
            path = "/config/security",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> getUserConfigSecurity() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user configuration")
    @GetMapping(
            path = "/config/language",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> getUserConfigLanguage() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user configuration")
    @PostMapping(
            path = "/config",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> setUserConfig(@org.springframework.web.bind.annotation.RequestBody UserConfig config) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Data
    public class UserLogin{
         String mail;
         String password;
    }

    @Data
    public class ResetCode{
        String code;
    }

    @Data
    public class UserConfig{
        String language;
        boolean notification;
        boolean security;
    }
}
