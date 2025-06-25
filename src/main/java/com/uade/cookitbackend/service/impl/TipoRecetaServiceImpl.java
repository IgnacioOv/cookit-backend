package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.entity.TipoReceta;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.TipoRecetaRepository;
import com.uade.cookitbackend.service.TipoRecetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoRecetaServiceImpl implements TipoRecetaService {

    private final TipoRecetaRepository tipoRecetaRepository;

    @Override
    @Cacheable("tiposReceta")
    @Transactional(readOnly = true)
    public List<TipoReceta> getAllTiposReceta() {
        return tipoRecetaRepository.findAll();
    }

    @Override
    public TipoReceta getTipoRecetaById(Integer idTipo) {
        return tipoRecetaRepository.findById(idTipo).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.TIPO_RECETA_NOT_FOUND, "TipoReceta not found with id: " + idTipo));
    }

    @Override
    @Transactional
    public TipoReceta createTipoReceta(TipoReceta tipoReceta) {
        return tipoRecetaRepository.save(tipoReceta);
    }
}
