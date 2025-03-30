package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.*;
import com.uade.cookitbackend.repository.db.RecetaRepository;
import com.uade.cookitbackend.service.PasoService;
import com.uade.cookitbackend.service.RecetaService;
import com.uade.cookitbackend.service.UsuarioService;
import com.uade.cookitbackend.service.mappers.RecetaMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final RecetaMapper recetaMapper = RecetaMapper.INSTANCE;
    private final UsuarioService usuarioService;
    private final TipoRecetaServiceImpl tipoRecetaServiceImpl;

    @Override
    @Transactional
    public RecetaResponseDTO createReceta(CreateRecetaDTO createRecetaDTO) {
        Usuario usuario = usuarioService.getUsuarioById(createRecetaDTO.getIdUsuario());
        TipoReceta tipoReceta = tipoRecetaServiceImpl.getTipoRecetaById(createRecetaDTO.getIdTipo());

        Receta receta = recetaMapper.toEntity(createRecetaDTO);
        receta.setUsuario(usuario);
        receta.setTipoReceta(tipoReceta);


        for (Paso paso : receta.getPasos()) {
            paso.setReceta(receta);


            if (paso.getMultimedia() != null) {
                for (Multimedia multimedia : paso.getMultimedia()) {
                    multimedia.setPaso(paso);
                }
            }
        }


        val savedReceta =  recetaRepository.save(receta);
        return recetaMapper.recetaToRecetaResponseDTO(savedReceta);

    }
}
