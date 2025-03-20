package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "utilizados")
public class IngredienteUtilizado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUtilizado")
    private Integer idUtilizado;

    @ManyToOne
    @JoinColumn(name = "idReceta")
    private Receta receta;

    @ManyToOne
    @JoinColumn(name = "idIngrediente")
    private Ingrediente ingrediente;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "idUnidad")
    private Unidad unidad;

    @Column(length = 500)
    private String observaciones;
} 