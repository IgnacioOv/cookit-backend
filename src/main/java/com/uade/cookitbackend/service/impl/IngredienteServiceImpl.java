package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.IngredienteNombreDto;
import com.uade.cookitbackend.entity.Ingrediente;
import com.uade.cookitbackend.repository.db.IngredienteRepository;
import com.uade.cookitbackend.service.IngredienteService;
import com.uade.cookitbackend.service.mappers.IngredienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredienteServiceImpl implements IngredienteService {
    private final IngredienteRepository ingredienteRepository;
    private final IngredienteMapper ingredienteMapper;

    @Override
    public List<Ingrediente> buscarPorNombre(String nombre) {
        return ingredienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<IngredienteNombreDto> getAllIngredientes() {
        return ingredienteRepository.findAll()
                .stream()
                .map(ingredienteMapper::toIngredienteNombreDto)
                .collect(Collectors.toList());
    }
}


