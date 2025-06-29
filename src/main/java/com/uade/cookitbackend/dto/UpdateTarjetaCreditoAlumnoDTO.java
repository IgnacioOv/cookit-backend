package com.uade.cookitbackend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateTarjetaCreditoAlumnoDTO {
    
    @Size(min = 16, max = 16, message = "El número de tarjeta debe tener exactamente 16 dígitos")
    @Pattern(regexp = "\\d{16}", message = "El número de tarjeta debe contener solo dígitos")
    private String numeroTarjeta;
    
    @Size(min = 3, max = 3, message = "El CVV debe tener exactamente 3 dígitos")
    @Pattern(regexp = "\\d{3}", message = "El CVV debe contener solo dígitos")
    private String cvv;
    
    private LocalDate fechaVencimiento;
}