package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.TarjetaCreditoAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TarjetaCreditoAlumnoRepository extends JpaRepository<TarjetaCreditoAlumno, Integer> {
    
    List<TarjetaCreditoAlumno> findByAlumnoIdAlumno(Integer idAlumno);
    
    boolean existsByNumeroTarjeta(String numeroTarjeta);
}