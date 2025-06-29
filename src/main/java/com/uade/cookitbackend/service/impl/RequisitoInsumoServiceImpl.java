package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateRequisitoInsumoDTO;
import com.uade.cookitbackend.dto.RequisitoInsumoResponseDTO;
import com.uade.cookitbackend.entity.RequisitoInsumo;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.CursoRepository;
import com.uade.cookitbackend.repository.db.RequisitoInsumoRepository;
import com.uade.cookitbackend.service.RequisitoInsumoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequisitoInsumoServiceImpl implements RequisitoInsumoService {

    private final RequisitoInsumoRepository requisitoRepository;
    private final CursoRepository cursoRepository;

    @Override
    @Transactional
    public RequisitoInsumoResponseDTO crearRequisito(CreateRequisitoInsumoDTO dto) {
        // Validar que el curso existe
        if (!cursoRepository.existsById(dto.getIdCurso())) {
            throw new ResourceNotFoundException(ErrorCode.CURSO_NOT_FOUND, 
                    "Curso no encontrado con ID: " + dto.getIdCurso());
        }

        // Validar que no existe un requisito con el mismo nombre para el mismo curso
        if (requisitoRepository.existsByIdCursoAndNombreInsumoIgnoreCase(dto.getIdCurso(), dto.getNombreInsumo())) {
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_RESOURCE, 
                    "Ya existe un requisito con el nombre '" + dto.getNombreInsumo() + "' para este curso");
        }

        RequisitoInsumo requisito = new RequisitoInsumo();
        requisito.setIdCurso(dto.getIdCurso());
        requisito.setNombreInsumo(dto.getNombreInsumo());
        requisito.setDescripcion(dto.getDescripcion());
        requisito.setObligatorio(dto.getObligatorio() != null ? dto.getObligatorio() : true);
        requisito.setCategoria(dto.getCategoria());
        requisito.setMarcaSugerida(dto.getMarcaSugerida());
        requisito.setCantidad(dto.getCantidad());
        requisito.setUnidadMedida(dto.getUnidadMedida());

        RequisitoInsumo savedRequisito = requisitoRepository.save(requisito);
        return mapToResponseDTO(savedRequisito);
    }

    @Override
    public RequisitoInsumoResponseDTO obtenerRequisito(Integer idRequisito) {
        RequisitoInsumo requisito = requisitoRepository.findById(idRequisito)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.REQUISITO_INSUMO_NOT_FOUND, 
                        "Requisito no encontrado con ID: " + idRequisito));
        return mapToResponseDTO(requisito);
    }

    @Override
    public List<RequisitoInsumoResponseDTO> obtenerRequisitosPorCurso(Integer idCurso) {
        return requisitoRepository.findByIdCursoOrderByCategoriaAscObligatorioDescNombreInsumoAsc(idCurso)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequisitoInsumoResponseDTO> obtenerRequisitosObligatoriosPorCurso(Integer idCurso) {
        return requisitoRepository.findByIdCursoAndObligatorioTrueOrderByNombreInsumoAsc(idCurso)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequisitoInsumoResponseDTO> obtenerRequisitosPorCursoYCategoria(Integer idCurso, String categoria) {
        return requisitoRepository.findByIdCursoAndCategoriaOrderByNombreInsumoAsc(idCurso, categoria)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> obtenerCategoriasPorCurso(Integer idCurso) {
        return requisitoRepository.findCategoriasByIdCurso(idCurso);
    }

    @Override
    @Transactional
    public RequisitoInsumoResponseDTO actualizarRequisito(Integer idRequisito, CreateRequisitoInsumoDTO dto) {
        RequisitoInsumo requisito = requisitoRepository.findById(idRequisito)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.REQUISITO_INSUMO_NOT_FOUND, 
                        "Requisito no encontrado con ID: " + idRequisito));

        // Validar duplicado solo si el nombre cambió
        if (!requisito.getNombreInsumo().equalsIgnoreCase(dto.getNombreInsumo()) && 
            requisitoRepository.existsByIdCursoAndNombreInsumoIgnoreCase(dto.getIdCurso(), dto.getNombreInsumo())) {
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_RESOURCE, 
                    "Ya existe un requisito con el nombre '" + dto.getNombreInsumo() + "' para este curso");
        }

        requisito.setNombreInsumo(dto.getNombreInsumo());
        requisito.setDescripcion(dto.getDescripcion());
        requisito.setObligatorio(dto.getObligatorio() != null ? dto.getObligatorio() : true);
        requisito.setCategoria(dto.getCategoria());
        requisito.setMarcaSugerida(dto.getMarcaSugerida());
        requisito.setCantidad(dto.getCantidad());
        requisito.setUnidadMedida(dto.getUnidadMedida());

        RequisitoInsumo updatedRequisito = requisitoRepository.save(requisito);
        return mapToResponseDTO(updatedRequisito);
    }

    @Override
    @Transactional
    public void eliminarRequisito(Integer idRequisito) {
        if (!requisitoRepository.existsById(idRequisito)) {
            throw new ResourceNotFoundException(ErrorCode.REQUISITO_INSUMO_NOT_FOUND, 
                    "Requisito no encontrado con ID: " + idRequisito);
        }
        requisitoRepository.deleteById(idRequisito);
    }

    @Override
    public List<RequisitoInsumoResponseDTO> obtenerTodosLosRequisitos() {
        return requisitoRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private RequisitoInsumoResponseDTO mapToResponseDTO(RequisitoInsumo requisito) {
        RequisitoInsumoResponseDTO dto = new RequisitoInsumoResponseDTO();
        dto.setIdRequisito(requisito.getIdRequisito());
        dto.setIdCurso(requisito.getIdCurso());
        dto.setNombreInsumo(requisito.getNombreInsumo());
        dto.setDescripcion(requisito.getDescripcion());
        dto.setObligatorio(requisito.getObligatorio());
        dto.setCategoria(requisito.getCategoria());
        dto.setMarcaSugerida(requisito.getMarcaSugerida());
        dto.setCantidad(requisito.getCantidad());
        dto.setUnidadMedida(requisito.getUnidadMedida());
        return dto;
    }
}