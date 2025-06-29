package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO de respuesta para requisitos e insumos")
public class RequisitoInsumoResponseDTO {
    
    @Schema(description = "ID único del requisito", example = "1")
    private Integer idRequisito;
    
    @Schema(description = "ID del curso asociado", example = "1")
    private Integer idCurso;
    
    @Schema(description = "Nombre del insumo o utensilio", example = "Cuchillo de chef")
    private String nombreInsumo;
    
    @Schema(description = "Descripción detallada del requisito", example = "Cuchillo de chef de 20cm, bien afilado")
    private String descripcion;
    
    @Schema(description = "Indica si es obligatorio traerlo", example = "true")
    private Boolean obligatorio;
    
    @Schema(description = "Categoría del requisito", example = "UTENSILIO")
    private String categoria;
    
    @Schema(description = "Marca sugerida (opcional)", example = "Tramontina")
    private String marcaSugerida;
    
    @Schema(description = "Cantidad necesaria", example = "1")
    private Integer cantidad;
    
    @Schema(description = "Unidad de medida", example = "unidades")
    private String unidadMedida;
}