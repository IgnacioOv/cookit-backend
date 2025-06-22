package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.RecetaAjustadaDTO;

public interface RecetaCalculadoraService {
    RecetaAjustadaDTO ajustarPorPorciones(Integer idReceta, Integer porcionesDeseadas);
    RecetaAjustadaDTO ajustarPorIngrediente(Integer idReceta, Integer idIngrediente, Float cantidadDeseada, Integer idUnidad);
}
