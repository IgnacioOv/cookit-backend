package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.RecetaMultimedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaMultimediaRepository extends JpaRepository<RecetaMultimedia, Integer> {
    List<RecetaMultimedia> findByReceta_IdReceta(Integer idReceta);
}