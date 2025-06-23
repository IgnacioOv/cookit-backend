package com.uade.cookitbackend.repository.db;


import com.uade.cookitbackend.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
}