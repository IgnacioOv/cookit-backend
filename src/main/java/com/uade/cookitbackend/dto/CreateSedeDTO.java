package com.uade.cookitbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSedeDTO {
    
    @NotBlank(message = "El nombre de la sede es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombreSede;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 250, message = "La dirección no puede exceder 250 caracteres")
    private String direccionSede;
    
    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]+$", message = "Formato de teléfono inválido")
    private String telefonoSede;
    
    @Email(message = "Formato de email inválido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String mailSede;
    
    @Size(max = 15, message = "El WhatsApp no puede exceder 15 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]+$", message = "Formato de WhatsApp inválido")
    private String whatsApp;
    
    @Size(max = 20, message = "El tipo de bonificación no puede exceder 20 caracteres")
    private String tipoBonificacion;
    
    private BigDecimal bonificacionCursos;
    
    @Size(max = 20, message = "El tipo de promoción no puede exceder 20 caracteres")
    private String tipoPromocion;
    
    private BigDecimal promocionCursos;
}