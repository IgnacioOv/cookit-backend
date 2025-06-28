package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateIngredienteDTO;
import com.uade.cookitbackend.dto.IngredienteNombreDto;
import com.uade.cookitbackend.entity.Ingrediente;
import java.util.List;

public interface IngredienteService {
    List<Ingrediente> buscarPorNombre(String nombre);
    List<IngredienteNombreDto> getAllIngredientes();
    IngredienteNombreDto createIngrediente(CreateIngredienteDTO createIngredienteDTO);
}