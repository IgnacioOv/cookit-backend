package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuario Management", description = "Endpoints for managing users")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

   
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Usuario> createUsuario(
        @Parameter(
            description = "User creation data",
            required = true,
            schema = @Schema(implementation = CreateUsuarioDTO.class)
        )
        @Valid @RequestBody CreateUsuarioDTO createUsuarioDTO
    ) {
        Usuario createdUsuario = usuarioService.createUsuario(createUsuarioDTO);
        return new ResponseEntity<>(createdUsuario, HttpStatus.CREATED);
    }
} 