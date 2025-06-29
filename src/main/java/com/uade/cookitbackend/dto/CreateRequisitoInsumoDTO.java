package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para crear un requisito de insumo")
public class CreateRequisitoInsumoDTO {
    
    @NotNull(message = "El ID del curso es obligatorio")
    @Schema(description = "ID del curso asociado", example = "1")
    private Integer idCurso;
    
    @NotBlank(message = "El nombre del insumo es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder los 200 caracteres")
    @Schema(description = "Nombre del insumo o utensilio", example = "Cuchillo de chef")
    private String nombreInsumo;
    
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Schema(description = "Descripción detallada del requisito", example = "Cuchillo de chef de 20cm, bien afilado")
    private String descripcion;
    
    @Schema(description = "Indica si es obligatorio traerlo", example = "true", defaultValue = "true")
    private Boolean obligatorio = true;
    
    @Size(max = 50, message = "La categoría no puede exceder los 50 caracteres")
    @Schema(description = "Categoría del requisito", example = "UTENSILIO")
    private String categoria;
    
    @Size(max = 100, message = "La marca sugerida no puede exceder los 100 caracteres")
    @Schema(description = "Marca sugerida (opcional)", example = "Tramontina")
    private String marcaSugerida;
    
    @Schema(description = "Cantidad necesaria", example = "1")
    private Integer cantidad;
    
    @Size(max = 20, message = "La unidad de medida no puede exceder los 20 caracteres")
    @Schema(description = "Unidad de medida", example = "unidades")
    private String unidadMedida;
}