package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.PasoDto;
import com.uade.cookitbackend.entity.Paso;

public interface PasoService {
    Paso createPaso(PasoDto createPasoDTO);
}
