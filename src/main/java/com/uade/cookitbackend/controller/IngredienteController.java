package com.uade.cookitbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import com.uade.cookitbackend.entity.Ingrediente;
import com.uade.cookitbackend.service.IngredienteService;
import com.uade.cookitbackend.dto.IngredienteNombreDto;

import java.util.List;

@RestController
@RequestMapping("/ingrediente")
public class IngredienteController {

    @Autowired
    private IngredienteService ingredienteService;

    @GetMapping("/{nombreIngrediente}")
    public List<IngredienteNombreDto> getIngrediente(@PathVariable String nombreIngrediente) {
        return ingredienteService.buscarPorNombre(nombreIngrediente)
                .stream()
                .map(i -> new IngredienteNombreDto(i.getIdIngrediente(), i.getNombre()))
                .toList();
    }

}
