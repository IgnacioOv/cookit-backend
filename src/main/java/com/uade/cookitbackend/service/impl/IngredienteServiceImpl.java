package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateIngredienteDTO;
import com.uade.cookitbackend.dto.IngredienteNombreDto;
import com.uade.cookitbackend.entity.Ingrediente;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.repository.db.IngredienteRepository;
import com.uade.cookitbackend.service.IngredienteService;
import com.uade.cookitbackend.service.mappers.IngredienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public IngredienteNombreDto createIngrediente(CreateIngredienteDTO createIngredienteDTO) {
        // Verificar que no exista un ingrediente con el mismo nombre (insensible a mayúsculas)
        String nombreNormalizado = createIngredienteDTO.getNombre().trim();
        List<Ingrediente> existentes = ingredienteRepository.findByNombreContainingIgnoreCase(nombreNormalizado);
        
        boolean nombreExactoExiste = existentes.stream()
                .anyMatch(ing -> ing.getNombre().equalsIgnoreCase(nombreNormalizado));
        
        if (nombreExactoExiste) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Ya existe un ingrediente con el nombre: " + nombreNormalizado
            );
        }

        // Crear nuevo ingrediente
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(nombreNormalizado);

        try {
            ingrediente = ingredienteRepository.save(ingrediente);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Error al crear el ingrediente: posible nombre duplicado"
            );
        }

        return ingredienteMapper.toIngredienteNombreDto(ingrediente);
    }
}


