package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.HorarioCronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioCronogramaRepository extends JpaRepository<HorarioCronograma, Integer> {
    
    List<HorarioCronograma> findByIdCronogramaOrderByDiaSemanaAscHoraInicioAsc(Integer idCronograma);
    
    @Query("SELECT h FROM HorarioCronograma h WHERE h.idCronograma = :idCronograma ORDER BY " +
           "CASE h.diaSemana " +
           "WHEN 'LUNES' THEN 1 " +
           "WHEN 'MARTES' THEN 2 " +
           "WHEN 'MIERCOLES' THEN 3 " +
           "WHEN 'JUEVES' THEN 4 " +
           "WHEN 'VIERNES' THEN 5 " +
           "WHEN 'SABADO' THEN 6 " +
           "WHEN 'DOMINGO' THEN 7 " +
           "END, h.horaInicio")
    List<HorarioCronograma> findByIdCronogramaOrderedByWeekday(@Param("idCronograma") Integer idCronograma);
}