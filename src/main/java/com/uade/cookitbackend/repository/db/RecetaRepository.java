package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, UUID> {
    List<Receta> findByNombreRecetaContainingIgnoreCaseOrderByIdRecetaDesc(String nombreReceta);
}