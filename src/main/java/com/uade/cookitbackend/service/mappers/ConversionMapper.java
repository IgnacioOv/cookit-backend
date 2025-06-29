package com.uade.cookitbackend.service.mappers;

import com.uade.cookitbackend.dto.ConversionResponseDTO;
import com.uade.cookitbackend.dto.CreateConversionDTO;
import com.uade.cookitbackend.dto.UpdateConversionDTO;
import com.uade.cookitbackend.entity.Conversion;
import com.uade.cookitbackend.entity.Unidad;
import org.springframework.stereotype.Component;

@Component
public class ConversionMapper {
    
    public Conversion toEntity(CreateConversionDTO dto, Unidad unidadOrigen, Unidad unidadDestino) {
        Conversion conversion = new Conversion();
        conversion.setUnidadOrigen(unidadOrigen);
        conversion.setUnidadDestino(unidadDestino);
        conversion.setFactorConversiones(dto.getFactorConversiones());
        return conversion;
    }
    
    public ConversionResponseDTO toDTO(Conversion conversion) {
        ConversionResponseDTO dto = new ConversionResponseDTO();
        dto.setIdConversion(conversion.getIdConversion());
        dto.setIdUnidadOrigen(conversion.getUnidadOrigen().getIdUnidad());
        dto.setNombreUnidadOrigen(conversion.getUnidadOrigen().getDescripcion());
        dto.setIdUnidadDestino(conversion.getUnidadDestino().getIdUnidad());
        dto.setNombreUnidadDestino(conversion.getUnidadDestino().getDescripcion());
        dto.setFactorConversiones(conversion.getFactorConversiones());
        return dto;
    }
    
    public void updateEntity(Conversion conversion, UpdateConversionDTO dto, Unidad unidadOrigen, Unidad unidadDestino) {
        if (unidadOrigen != null) {
            conversion.setUnidadOrigen(unidadOrigen);
        }
        if (unidadDestino != null) {
            conversion.setUnidadDestino(unidadDestino);
        }
        if (dto.getFactorConversiones() != null) {
            conversion.setFactorConversiones(dto.getFactorConversiones());
        }
    }
}