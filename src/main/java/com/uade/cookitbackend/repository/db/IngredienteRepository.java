package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer> {
    List<Ingrediente> findByNombreContainingIgnoreCase(String nombre);
}

