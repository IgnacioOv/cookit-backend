package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.UnidadResponseDTO;
import com.uade.cookitbackend.service.UnidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "API para la gestión de unidades de medida")
public class UnidadController {

    private final UnidadService unidadService;

    @Operation(
        summary = "Obtener todas las unidades",
        description = "Retorna una lista de todas las unidades de medida disponibles con sus IDs y descripciones"
    )
    @GetMapping
    public ResponseEntity<List<UnidadResponseDTO>> getAllUnidades() {
        return ResponseEntity.ok(unidadService.getAllUnidades());
    }
}
