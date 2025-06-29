package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Integer> {
    Optional<Unidad> findByDescripcionIgnoreCase(String descripcion);
}

