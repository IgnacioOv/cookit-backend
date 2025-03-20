package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "unidades")
public class Unidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUnidad")
    private Integer idUnidad;

    @Column(length = 50, nullable = false)
    private String descripcion;

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL)
    private List<IngredienteUtilizado> ingredientesUtilizados;

    @OneToMany(mappedBy = "unidadOrigen", cascade = CascadeType.ALL)
    private List<Conversion> conversionesOrigen;

    @OneToMany(mappedBy = "unidadDestino", cascade = CascadeType.ALL)
    private List<Conversion> conversionesDestino;
} 