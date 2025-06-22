package com.uade.cookitbackend.service;

import com.uade.cookitbackend.entity.Ingrediente;
import java.util.List;

public interface IngredienteService {
    List<Ingrediente> buscarPorNombre(String nombre);
}