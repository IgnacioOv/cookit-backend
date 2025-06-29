package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateHorarioCronogramaDTO;
import com.uade.cookitbackend.dto.HorarioCronogramaResponseDTO;
import com.uade.cookitbackend.entity.HorarioCronograma;
import com.uade.cookitbackend.exception.BadRequestException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.CronogramaCursoRepository;
import com.uade.cookitbackend.repository.db.HorarioCronogramaRepository;
import com.uade.cookitbackend.service.HorarioCronogramaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioCronogramaServiceImpl implements HorarioCronogramaService {

    private final HorarioCronogramaRepository horarioRepository;
    private final CronogramaCursoRepository cronogramaRepository;

    @Override
    @Transactional
    public HorarioCronogramaResponseDTO crearHorario(CreateHorarioCronogramaDTO dto) {
        // Validar que el cronograma existe
        if (!cronogramaRepository.existsById(dto.getIdCronograma())) {
            throw new ResourceNotFoundException(ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND, 
                    "Cronograma no encontrado con ID: " + dto.getIdCronograma());
        }

        // Validar que la hora de fin es posterior a la hora de inicio
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, 
                    "La hora de fin debe ser posterior a la hora de inicio");
        }

        HorarioCronograma horario = new HorarioCronograma();
        horario.setIdCronograma(dto.getIdCronograma());
        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        horario.setObservaciones(dto.getObservaciones());

        HorarioCronograma savedHorario = horarioRepository.save(horario);
        return mapToResponseDTO(savedHorario);
    }

    @Override
    public HorarioCronogramaResponseDTO obtenerHorario(Integer idHorario) {
        HorarioCronograma horario = horarioRepository.findById(idHorario)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.HORARIO_CRONOGRAMA_NOT_FOUND, 
                        "Horario no encontrado con ID: " + idHorario));
        return mapToResponseDTO(horario);
    }

    @Override
    public List<HorarioCronogramaResponseDTO> obtenerHorariosPorCronograma(Integer idCronograma) {
        return horarioRepository.findByIdCronogramaOrderedByWeekday(idCronograma)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HorarioCronogramaResponseDTO actualizarHorario(Integer idHorario, CreateHorarioCronogramaDTO dto) {
        HorarioCronograma horario = horarioRepository.findById(idHorario)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.HORARIO_CRONOGRAMA_NOT_FOUND, 
                        "Horario no encontrado con ID: " + idHorario));

        // Validar que la hora de fin es posterior a la hora de inicio
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, 
                    "La hora de fin debe ser posterior a la hora de inicio");
        }

        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        horario.setObservaciones(dto.getObservaciones());

        HorarioCronograma updatedHorario = horarioRepository.save(horario);
        return mapToResponseDTO(updatedHorario);
    }

    @Override
    @Transactional
    public void eliminarHorario(Integer idHorario) {
        if (!horarioRepository.existsById(idHorario)) {
            throw new ResourceNotFoundException(ErrorCode.HORARIO_CRONOGRAMA_NOT_FOUND, 
                    "Horario no encontrado con ID: " + idHorario);
        }
        horarioRepository.deleteById(idHorario);
    }

    @Override
    public List<HorarioCronogramaResponseDTO> obtenerTodosLosHorarios() {
        return horarioRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private HorarioCronogramaResponseDTO mapToResponseDTO(HorarioCronograma horario) {
        HorarioCronogramaResponseDTO dto = new HorarioCronogramaResponseDTO();
        dto.setIdHorario(horario.getIdHorario());
        dto.setIdCronograma(horario.getIdCronograma());
        dto.setDiaSemana(horario.getDiaSemana());
        dto.setHoraInicio(horario.getHoraInicio());
        dto.setHoraFin(horario.getHoraFin());
        dto.setObservaciones(horario.getObservaciones());
        return dto;
    }
}