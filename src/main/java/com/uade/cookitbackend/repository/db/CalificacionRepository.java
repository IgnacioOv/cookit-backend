package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {
    List<Calificacion> findByRecetaIdReceta(Integer idReceta);
    Optional<Calificacion> findByUsuarioIdUsuarioAndRecetaIdReceta(Integer idUsuario, Integer idReceta);
}
