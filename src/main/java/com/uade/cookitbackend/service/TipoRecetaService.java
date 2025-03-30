package com.uade.cookitbackend.service;

import com.uade.cookitbackend.entity.TipoReceta;
import java.util.List;

public interface TipoRecetaService {
    List<TipoReceta> getAllTiposReceta();
    TipoReceta getTipoRecetaById(Integer idTipo);
    TipoReceta createTipoReceta(TipoReceta tipoReceta);
}
