package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.NotificacionResponseDTO;
import com.uade.cookitbackend.entity.Notificacion;
import com.uade.cookitbackend.repository.db.NotificacionRepository;
import com.uade.cookitbackend.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Override
    @Transactional
    public Notificacion guardarNotificacion(Integer usuarioId, String body) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(usuarioId);
        notificacion.setBody(body);
        return notificacionRepository.save(notificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notificacion> obtenerNotificacionesPorUsuario(Integer usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> obtenerNotificacionesDTOPorUsuario(Integer usuarioId) {
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
        return notificaciones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NotificacionResponseDTO convertToDTO(Notificacion notificacion) {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setIdNotificacion(notificacion.getIdNotificacion());
        dto.setUsuarioId(notificacion.getUsuarioId());
        dto.setBody(notificacion.getBody());
        dto.setFechaCreacion(notificacion.getFechaCreacion());
        return dto;
    }
}