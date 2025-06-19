package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.entity.Ingrediente;
import com.uade.cookitbackend.repository.db.IngredienteRepository;
import com.uade.cookitbackend.service.IngredienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredienteServiceImpl implements IngredienteService {
    private final IngredienteRepository ingredienteRepository;

    @Override
    public List<Ingrediente> buscarPorNombre(String nombre) {
        return ingredienteRepository.findByNombreContainingIgnoreCase(nombre);
    }
}


