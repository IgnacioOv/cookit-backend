package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.entity.TipoReceta;
import com.uade.cookitbackend.service.TipoRecetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-receta")
@RequiredArgsConstructor
public class TipoRecetaController {

    private final TipoRecetaService tipoRecetaService;

    @GetMapping
    public ResponseEntity<List<TipoReceta>> getAllTiposReceta() {
        List<TipoReceta> tiposReceta = tipoRecetaService.getAllTiposReceta();
        return ResponseEntity.ok(tiposReceta);
    }

    @GetMapping("/{idTipo}")
    public ResponseEntity<TipoReceta> getTipoRecetaById(@PathVariable Integer idTipo) {
        TipoReceta tipoReceta = tipoRecetaService.getTipoRecetaById(idTipo);
        if (tipoReceta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tipoReceta);
    }

    @PostMapping
    public ResponseEntity<TipoReceta> createTipoReceta(@RequestBody TipoReceta tipoReceta) {
        TipoReceta createdTipoReceta = tipoRecetaService.createTipoReceta(tipoReceta);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTipoReceta);
    }
}
