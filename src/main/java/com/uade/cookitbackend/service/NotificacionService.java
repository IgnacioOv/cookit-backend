package com.uade.cookitbackend.service;

import com.uade.cookitbackend.entity.Notificacion;

import java.util.List;

public interface NotificacionService {
    Notificacion guardarNotificacion(Integer usuarioId, String body);
    List<Notificacion> obtenerNotificacionesPorUsuario(Integer usuarioId);
}