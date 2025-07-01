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
        // SIN VALIDACIÓN DE TIEMPO - Registrar asistencia directamente
        asistenciaRepository.save(asistencia);
    }

    public HorarioCronograma obtenerHorarioCorrespondiente(AsistenciaCurso asistencia) {
        // Usar locale español para días de semana
        String diaAsistencia = asistencia.getFecha().getDayOfWeek()
                                         .getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
        LocalTime horaAsistencia = asistencia.getFecha().toLocalTime();
        List<HorarioCronograma> horarios = horarioRepository.findByIdCronograma(
                asistencia.getCronograma().getIdCronograma());
        
        for (HorarioCronograma horario : horarios) {
            if (horario.getDiaSemana().equalsIgnoreCase(diaAsistencia)) {
                // Permitir tolerancia de ±15 minutos
                LocalTime inicioPermitido = horario.getHoraInicio().minusMinutes(15);
                LocalTime finPermitido = horario.getHoraFin().plusMinutes(15);
                
                if (!horaAsistencia.isBefore(inicioPermitido) && 
                    !horaAsistencia.isAfter(finPermitido)) {
                    return horario;
                }
            }
        }
        return null;
    }

}
