package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateTarjetaCreditoAlumnoDTO;
import com.uade.cookitbackend.dto.TarjetaCreditoAlumnoResponseDTO;
import com.uade.cookitbackend.dto.UpdateTarjetaCreditoAlumnoDTO;
import com.uade.cookitbackend.entity.Alumno;
import com.uade.cookitbackend.entity.TarjetaCreditoAlumno;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.AlumnoRepository;
import com.uade.cookitbackend.repository.db.TarjetaCreditoAlumnoRepository;
import com.uade.cookitbackend.service.TarjetaCreditoAlumnoService;
import com.uade.cookitbackend.service.mappers.TarjetaCreditoAlumnoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TarjetaCreditoAlumnoServiceImpl implements TarjetaCreditoAlumnoService {

    private final TarjetaCreditoAlumnoRepository tarjetaCreditoAlumnoRepository;
    private final AlumnoRepository alumnoRepository;
    private final TarjetaCreditoAlumnoMapper tarjetaCreditoAlumnoMapper;

    @Override
    @Transactional
    public TarjetaCreditoAlumnoResponseDTO createTarjetaCredito(CreateTarjetaCreditoAlumnoDTO dto) {
        if (tarjetaCreditoAlumnoRepository.existsByNumeroTarjeta(dto.getNumeroTarjeta())) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Ya existe una tarjeta de crédito con el número: " + dto.getNumeroTarjeta()
            );
        }

        Alumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ALUMNO_NOT_FOUND,
                        "Alumno no encontrado con id: " + dto.getIdAlumno()
                ));

        TarjetaCreditoAlumno tarjetaCredito = tarjetaCreditoAlumnoMapper.toEntity(dto, alumno);
        tarjetaCredito = tarjetaCreditoAlumnoRepository.save(tarjetaCredito);

        return tarjetaCreditoAlumnoMapper.toResponseDTO(tarjetaCredito);
    }

    @Override
    @Transactional(readOnly = true)
    public TarjetaCreditoAlumnoResponseDTO getTarjetaCreditoById(Integer id) {
        TarjetaCreditoAlumno tarjetaCredito = tarjetaCreditoAlumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Tarjeta de crédito no encontrada con id: " + id
                ));

        return tarjetaCreditoAlumnoMapper.toResponseDTO(tarjetaCredito);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarjetaCreditoAlumnoResponseDTO> getAllTarjetasCredito() {
        List<TarjetaCreditoAlumno> tarjetasCredito = tarjetaCreditoAlumnoRepository.findAll();
        return tarjetasCredito.stream()
                .map(tarjetaCreditoAlumnoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarjetaCreditoAlumnoResponseDTO> getTarjetasCreditoByAlumnoId(Integer idAlumno) {
        if (!alumnoRepository.existsById(idAlumno)) {
            throw new ResourceNotFoundException(
                    ErrorCode.ALUMNO_NOT_FOUND,
                    "Alumno no encontrado con id: " + idAlumno
            );
        }

        List<TarjetaCreditoAlumno> tarjetasCredito = tarjetaCreditoAlumnoRepository.findByAlumnoIdAlumno(idAlumno);
        return tarjetasCredito.stream()
                .map(tarjetaCreditoAlumnoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public TarjetaCreditoAlumnoResponseDTO updateTarjetaCredito(Integer id, UpdateTarjetaCreditoAlumnoDTO dto) {
        TarjetaCreditoAlumno tarjetaCredito = tarjetaCreditoAlumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Tarjeta de crédito no encontrada con id: " + id
                ));

        if (dto.getNumeroTarjeta() != null && 
            !dto.getNumeroTarjeta().equals(tarjetaCredito.getNumeroTarjeta()) &&
            tarjetaCreditoAlumnoRepository.existsByNumeroTarjeta(dto.getNumeroTarjeta())) {
            throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Ya existe una tarjeta de crédito con el número: " + dto.getNumeroTarjeta()
            );
        }

        tarjetaCreditoAlumnoMapper.updateEntityFromDTO(dto, tarjetaCredito);
        tarjetaCredito = tarjetaCreditoAlumnoRepository.save(tarjetaCredito);

        return tarjetaCreditoAlumnoMapper.toResponseDTO(tarjetaCredito);
    }

    @Override
    @Transactional
    public void deleteTarjetaCredito(Integer id) {
        if (!tarjetaCreditoAlumnoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "Tarjeta de crédito no encontrada con id: " + id
            );
        }

        tarjetaCreditoAlumnoRepository.deleteById(id);
    }
}