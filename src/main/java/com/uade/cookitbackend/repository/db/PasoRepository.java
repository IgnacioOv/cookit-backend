package com.uade.cookitbackend.repository.db;


import com.uade.cookitbackend.entity.Paso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasoRepository extends JpaRepository<Paso, Integer> {
}
