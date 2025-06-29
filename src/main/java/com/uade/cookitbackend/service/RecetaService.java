package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.FavoritoResponseDTO;
import com.uade.cookitbackend.dto.RecetaAprobacionResponseDTO;
import com.uade.cookitbackend.dto.UpdateRecetaDTO;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.Paso;
import com.uade.cookitbackend.entity.Receta;

import java.util.List;
import java.util.UUID;

public interface RecetaService {
    RecetaResponseDTO createReceta(CreateRecetaDTO createRecetaDTO);
    RecetaResponseDTO createReceta(CreateRecetaDTO createRecetaDTO, Boolean reemplazar);
    RecetaResponseDTO updateReceta(Integer idReceta, UpdateRecetaDTO updateRecetaDTO, Integer idUsuario);
    Boolean existsRecetaByNombreAndUsuario(String nombreReceta, Integer idUsuario);
    List<RecetaResponseDTO> getRecetasByNombre(String nombreReceta);
    List<RecetaResponseDTO> getRecetasByTipo(Integer idTipo, String orden);
    List<RecetaResponseDTO> getRecetaByIdUsuario(Integer userId);
    List<RecetaResponseDTO> getRecetasWithoutIngrediente(String ingrediente, String orden);
    List<RecetaResponseDTO> getRecetasWithIngrediente(String ingrediente, String orden);
    RecetaResponseDTO getRecetaById(Integer id);
    List<RecetaResponseDTO> getFeed();
    List<Paso> getPasosByRecetaId(Integer id);
    FavoritoResponseDTO agregarAFavoritos(Integer idUsuario, Integer idReceta);
    FavoritoResponseDTO quitarDeFavoritos(Integer idUsuario, Integer idReceta);
    List<RecetaResponseDTO> getRecetasFavoritas(Integer idUsuario);
    List<RecetaResponseDTO> getRecetasNoAprobadas();
    List<RecetaResponseDTO> getRecetasNoAprobadasByUsuario(Integer idUsuario);
    RecetaAprobacionResponseDTO aprobarReceta(Integer idReceta);
}
