package com.uade.cookitbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateTarjetaCreditoAlumnoDTO {
    
    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Size(min = 16, max = 16, message = "El número de tarjeta debe tener exactamente 16 dígitos")
    @Pattern(regexp = "\\d{16}", message = "El número de tarjeta debe contener solo dígitos")
    private String numeroTarjeta;
    
    @NotBlank(message = "El CVV es obligatorio")
    @Size(min = 3, max = 3, message = "El CVV debe tener exactamente 3 dígitos")
    @Pattern(regexp = "\\d{3}", message = "El CVV debe contener solo dígitos")
    private String cvv;
    
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;
    
    @NotNull(message = "El ID del alumno es obligatorio")
    private Integer idAlumno;
}