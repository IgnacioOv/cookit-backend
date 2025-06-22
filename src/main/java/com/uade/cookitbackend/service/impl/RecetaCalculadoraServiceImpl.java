package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.IngredienteAjustadoDTO;
import com.uade.cookitbackend.dto.RecetaAjustadaDTO;
import com.uade.cookitbackend.entity.Conversion;
import com.uade.cookitbackend.entity.IngredienteUtilizado;
import com.uade.cookitbackend.entity.Receta;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.ConversionRepository;
import com.uade.cookitbackend.repository.db.RecetaRepository;
import com.uade.cookitbackend.service.RecetaCalculadoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecetaCalculadoraServiceImpl implements RecetaCalculadoraService {

    private final RecetaRepository recetaRepository;
    private final ConversionRepository conversionRepository;

    @Override
    public RecetaAjustadaDTO ajustarPorPorciones(Integer idReceta, Integer porcionesDeseadas) {
        Receta receta = obtenerReceta(idReceta);
        float factor = calcularFactorPorPorciones(receta.getPorciones(), porcionesDeseadas);
        return crearRecetaAjustada(receta, factor, porcionesDeseadas);
    }

    @Override
    public RecetaAjustadaDTO ajustarPorIngrediente(Integer idReceta, Integer idIngrediente, Float cantidadDeseada, Integer idUnidad) {
        Receta receta = obtenerReceta(idReceta);

        Optional<IngredienteUtilizado> ingredienteBase = receta.getIngredientesUtilizados().stream()
                .filter(iu -> iu.getIngrediente().getIdIngrediente().equals(idIngrediente))
                .findFirst();

        if (ingredienteBase.isEmpty()) {
            throw new ResourceNotFoundException(ErrorCode.INGREDIENTE_NOT_FOUND, "Ingrediente no encontrado en la receta");
        }

        IngredienteUtilizado ing = ingredienteBase.get();
        float cantidadOriginalConvertida = convertirCantidad(
            ing.getCantidad(),
            ing.getUnidad().getIdUnidad(),
            idUnidad
        );

        float factor = cantidadDeseada / cantidadOriginalConvertida;
        int porcionesEstimadas = Math.round(receta.getPorciones() * factor);

        return crearRecetaAjustada(receta, factor, porcionesEstimadas);
    }

    private float convertirCantidad(float cantidad, Integer idUnidadOrigen, Integer idUnidadDestino) {
        if (idUnidadOrigen.equals(idUnidadDestino)) {
            return cantidad;
        }

        Optional<Conversion> conversion = conversionRepository
            .findByUnidadOrigenIdUnidadAndUnidadDestinoIdUnidad(idUnidadOrigen, idUnidadDestino);

        if (conversion.isPresent()) {
            return cantidad * conversion.get().getFactorConversiones();
        }

        // Intentar conversión inversa
        conversion = conversionRepository
            .findByUnidadOrigenIdUnidadAndUnidadDestinoIdUnidad(idUnidadDestino, idUnidadOrigen);

        if (conversion.isPresent()) {
            return cantidad / conversion.get().getFactorConversiones();
        }

        throw new IllegalArgumentException("No existe conversión entre las unidades especificadas");
    }

    private Receta obtenerReceta(Integer idReceta) {
        return recetaRepository.findByIdWithIngredientes(idReceta)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.RECETA_NOT_FOUND,
                    "Receta con ID " + idReceta + " no encontrada"
                ));
    }

    private float calcularFactorPorPorciones(Integer porcionesOriginales, Integer porcionesDeseadas) {
        return (float) porcionesDeseadas / porcionesOriginales;
    }

    private RecetaAjustadaDTO crearRecetaAjustada(Receta receta, float factor, Integer porcionesAjustadas) {
        RecetaAjustadaDTO recetaAjustada = new RecetaAjustadaDTO();
        recetaAjustada.setIdReceta(receta.getIdReceta());
        recetaAjustada.setNombreReceta(receta.getNombreReceta());
        recetaAjustada.setPorcionesOriginales(receta.getPorciones());
        recetaAjustada.setPorcionesAjustadas(porcionesAjustadas);

        List<IngredienteAjustadoDTO> ingredientesAjustados = new ArrayList<>();

        for (IngredienteUtilizado iu : receta.getIngredientesUtilizados()) {
            IngredienteAjustadoDTO ajustado = new IngredienteAjustadoDTO();
            ajustado.setIdIngrediente(iu.getIngrediente().getIdIngrediente());
            ajustado.setNombreIngrediente(iu.getIngrediente().getNombre());
            ajustado.setCantidadOriginal(Float.valueOf(iu.getCantidad()));
            ajustado.setCantidadAjustada(iu.getCantidad() * factor);
            ajustado.setIdUnidad(iu.getUnidad().getIdUnidad());
            ajustado.setDescripcionUnidad(iu.getUnidad().getDescripcion());
            ajustado.setObservaciones(iu.getObservaciones());
            ingredientesAjustados.add(ajustado);
        }

        recetaAjustada.setIngredientesAjustados(ingredientesAjustados);
        return recetaAjustada;
    }
}
