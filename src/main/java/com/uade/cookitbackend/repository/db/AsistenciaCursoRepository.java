package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.AsistenciaCurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsistenciaCursoRepository extends JpaRepository<AsistenciaCurso, Integer> {
    List<AsistenciaCurso> findByAlumno_IdAlumno(Integer idAlumno);
    List<AsistenciaCurso> findByCronograma_IdCronograma(Integer idCronograma);
    List<AsistenciaCurso> findByAlumno_IdAlumnoAndCronograma_IdCronograma(Integer idAlumno, Integer idCronograma);
    boolean existsByAlumno_IdAlumnoAndCronograma_IdCronograma(Integer idAlumno, Integer idCronograma);
}
