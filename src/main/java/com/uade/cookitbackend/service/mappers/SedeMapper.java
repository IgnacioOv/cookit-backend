package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.CreateSedeDTO;
import com.uade.cookitbackend.dto.SedeResponseDTO;
import com.uade.cookitbackend.dto.UpdateSedeDTO;
import com.uade.cookitbackend.entity.Sede;
import org.springframework.stereotype.Component;

@Component
public class SedeMapper {
    
    public Sede toEntity(CreateSedeDTO dto) {
        Sede sede = new Sede();
        sede.setNombreSede(dto.getNombreSede());
        sede.setDireccionSede(dto.getDireccionSede());
        sede.setTelefonoSede(dto.getTelefonoSede());
        sede.setMailSede(dto.getMailSede());
        sede.setWhatsApp(dto.getWhatsApp());
        sede.setTipoBonificacion(dto.getTipoBonificacion());
        sede.setBonificacionCursos(dto.getBonificacionCursos());
        sede.setTipoPromocion(dto.getTipoPromocion());
        sede.setPromocionCursos(dto.getPromocionCursos());
        return sede;
    }
    
    public SedeResponseDTO toDTO(Sede sede) {
        SedeResponseDTO dto = new SedeResponseDTO();
        dto.setIdSede(sede.getIdSede());
        dto.setNombreSede(sede.getNombreSede());
        dto.setDireccionSede(sede.getDireccionSede());
        dto.setTelefonoSede(sede.getTelefonoSede());
        dto.setMailSede(sede.getMailSede());
        dto.setWhatsApp(sede.getWhatsApp());
        dto.setTipoBonificacion(sede.getTipoBonificacion());
        dto.setBonificacionCursos(sede.getBonificacionCursos());
        dto.setTipoPromocion(sede.getTipoPromocion());
        dto.setPromocionCursos(sede.getPromocionCursos());
        dto.setTotalCursosDisponibles(sede.getCronogramas() != null ? sede.getCronogramas().size() : 0);
        return dto;
    }
    
    public void updateEntity(Sede sede, UpdateSedeDTO dto) {
        if (dto.getNombreSede() != null) {
            sede.setNombreSede(dto.getNombreSede());
        }
        if (dto.getDireccionSede() != null) {
            sede.setDireccionSede(dto.getDireccionSede());
        }
        if (dto.getTelefonoSede() != null) {
            sede.setTelefonoSede(dto.getTelefonoSede());
        }
        if (dto.getMailSede() != null) {
            sede.setMailSede(dto.getMailSede());
        }
        if (dto.getWhatsApp() != null) {
            sede.setWhatsApp(dto.getWhatsApp());
        }
        if (dto.getTipoBonificacion() != null) {
            sede.setTipoBonificacion(dto.getTipoBonificacion());
        }
        if (dto.getBonificacionCursos() != null) {
            sede.setBonificacionCursos(dto.getBonificacionCursos());
        }
        if (dto.getTipoPromocion() != null) {
            sede.setTipoPromocion(dto.getTipoPromocion());
        }
        if (dto.getPromocionCursos() != null) {
            sede.setPromocionCursos(dto.getPromocionCursos());
        }
    }
}