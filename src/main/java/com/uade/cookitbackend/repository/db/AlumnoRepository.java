package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    Optional<Alumno> findByUsuarioIdUsuario(Integer idUsuario);
}
