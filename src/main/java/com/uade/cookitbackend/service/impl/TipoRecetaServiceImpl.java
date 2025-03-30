package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.entity.TipoReceta;
import com.uade.cookitbackend.repository.db.TipoRecetaRepository;
import com.uade.cookitbackend.service.TipoRecetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoRecetaServiceImpl implements TipoRecetaService {

    private final TipoRecetaRepository tipoRecetaRepository;

    @Override
    public List<TipoReceta> getAllTiposReceta() {
        return tipoRecetaRepository.findAll();
    }

    @Override
    public TipoReceta getTipoRecetaById(Integer idTipo) {
        return tipoRecetaRepository.findById(idTipo).orElseThrow(
                () -> new RuntimeException("TipoReceta not found with id: " + idTipo));
    }

    @Override
    public TipoReceta createTipoReceta(TipoReceta tipoReceta) {
        return tipoRecetaRepository.save(tipoReceta);
    }
}
