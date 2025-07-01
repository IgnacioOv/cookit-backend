package com.uade.cookitbackend.service;

import com.uade.cookitbackend.entity.AsistenciaCurso;
import com.uade.cookitbackend.entity.CronogramaCurso;
import com.uade.cookitbackend.entity.HorarioCronograma;
import com.uade.cookitbackend.repository.db.AsistenciaCursoRepository;
import com.uade.cookitbackend.repository.db.HorarioCronogramaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class AsistenciaService {

    private final HorarioCronogramaRepository horarioRepository;
    private final AsistenciaCursoRepository asistenciaRepository;

    @Autowired
    public AsistenciaService(HorarioCronogramaRepository horarioRepository,
                             AsistenciaCursoRepository asistenciaRepository) {
        this.horarioRepository = horarioRepository;
        this.asistenciaRepository = asistenciaRepository;
    }

    public void registrarAsistencia(AsistenciaCurso asistencia) {
        HorarioCronograma horario = obtenerHorarioCorrespondiente(asistencia);
        if (horario != null) {

            asistenciaRepository.save(asistencia);
        } else {
            throw new RuntimeException("La asistencia no corresponde a ningún horario programado");
        }
    }

    public HorarioCronograma obtenerHorarioCorrespondiente(AsistenciaCurso asistencia) {
        String diaAsistencia = asistencia.getFecha().getDayOfWeek()
                                         .getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase();
        LocalTime horaAsistencia = asistencia.getFecha().toLocalTime();
        List<HorarioCronograma> horarios = horarioRepository.findByIdCronograma(
                asistencia.getCronograma().getIdCronograma());
        for (HorarioCronograma horario : horarios) {
            if (horario.getDiaSemana().equalsIgnoreCase(diaAsistencia) &&
               (horaAsistencia.equals(horario.getHoraInicio()) ||
                (horaAsistencia.isAfter(horario.getHoraInicio()) && horaAsistencia.isBefore(horario.getHoraFin())) ||
                horaAsistencia.equals(horario.getHoraFin()))) {
                return horario;
            }
        }
        return null;
    }

}
