package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.InscripcionCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InscripcionCursoRepository extends JpaRepository<InscripcionCurso, Integer> {
    List<InscripcionCurso> findByAlumno_IdAlumno(Integer idAlumno);
    List<InscripcionCurso> findByCronograma_IdCronograma(Integer idCronograma);
    boolean existsByAlumno_IdAlumnoAndCronograma_IdCronograma(Integer idAlumno, Integer idCronograma);
}