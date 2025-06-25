package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.CronogramaCurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CronogramaCursoRepository extends JpaRepository<CronogramaCurso, Integer> {
    List<CronogramaCurso> findByCurso_IdCurso(Integer idCurso);
    List<CronogramaCurso> findBySede_IdSede(Integer idSede);
    List<CronogramaCurso> findByCurso_IdCursoAndSede_IdSede(Integer idCurso, Integer idSede);
    List<CronogramaCurso> findByFechaInicioAfter(java.time.LocalDate fecha);
}
