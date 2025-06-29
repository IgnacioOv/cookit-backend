package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SedeRepository extends JpaRepository<Sede, Integer> {
    
    Optional<Sede> findByNombreSede(String nombreSede);
    
    List<Sede> findByNombreSedeContainingIgnoreCase(String nombreSede);
    
    @Query("SELECT s FROM Sede s WHERE s.bonificacionCursos > 0")
    List<Sede> findSedesWithBonificacion();
    
    @Query("SELECT s FROM Sede s WHERE s.promocionCursos > 0")
    List<Sede> findSedesWithPromocion();
    
    @Query("SELECT s FROM Sede s JOIN s.cronogramas c WHERE c.curso.idCurso = :idCurso")
    List<Sede> findSedesByCurso(@Param("idCurso") Integer idCurso);
}