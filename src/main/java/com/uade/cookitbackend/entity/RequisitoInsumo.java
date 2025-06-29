package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "requisitos_insumos")
public class RequisitoInsumo {
    
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requisito")
    private Integer idRequisito;
    
    @NotNull
    @Column(name = "id_curso", nullable = false)
    private Integer idCurso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso", insertable = false, updatable = false)
    private Curso curso;
    
    @NotNull
    @Size(max = 200)
    @Column(name = "nombre_insumo", nullable = false, length = 200)
    private String nombreInsumo;
    
    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    
    @NotNull
    @Column(name = "obligatorio", nullable = false)
    private Boolean obligatorio = true;
    
    @Size(max = 50)
    @Column(name = "categoria", length = 50)
    private String categoria; // UTENSILIO, INGREDIENTE, MATERIAL, EQUIPO
    
    @Size(max = 100)
    @Column(name = "marca_sugerida", length = 100)
    private String marcaSugerida;
    
    @Column(name = "cantidad")
    private Integer cantidad;
    
    @Size(max = 20)
    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida; // unidades, gramos, ml, etc.
}