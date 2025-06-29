package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateIngredienteDTO;
import com.uade.cookitbackend.dto.IngredienteNombreDto;
import com.uade.cookitbackend.dto.UpdateIngredienteDTO;
import com.uade.cookitbackend.entity.Ingrediente;
import java.util.List;

public interface IngredienteService {
    List<Ingrediente> buscarPorNombre(String nombre);
    List<IngredienteNombreDto> getAllIngredientes();
    IngredienteNombreDto getIngredienteById(Integer idIngrediente);
    IngredienteNombreDto createIngrediente(CreateIngredienteDTO createIngredienteDTO);
    IngredienteNombreDto updateIngrediente(Integer idIngrediente, UpdateIngredienteDTO updateIngredienteDTO);
    void deleteIngrediente(Integer idIngrediente);
}