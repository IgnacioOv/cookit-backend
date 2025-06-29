package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.TipoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoRecetaRepository extends JpaRepository<TipoReceta, Integer> {
    Optional<TipoReceta> findByDescripcionIgnoreCase(String descripcion);
}
