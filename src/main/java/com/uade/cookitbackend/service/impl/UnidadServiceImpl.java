package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.UnidadResponseDTO;
import com.uade.cookitbackend.entity.Unidad;
import com.uade.cookitbackend.repository.db.UnidadRepository;
import com.uade.cookitbackend.service.UnidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnidadServiceImpl implements UnidadService {

    private final UnidadRepository unidadRepository;

    @Override
    @Cacheable("unidades")
    @Transactional(readOnly = true)
    public List<UnidadResponseDTO> getAllUnidades() {
        return unidadRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private UnidadResponseDTO mapToDTO(Unidad unidad) {
        UnidadResponseDTO dto = new UnidadResponseDTO();
        dto.setId(unidad.getIdUnidad());
        dto.setDescripcion(unidad.getDescripcion());
        return dto;
    }
}
