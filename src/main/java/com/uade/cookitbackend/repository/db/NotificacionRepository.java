package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Integer usuarioId);
}