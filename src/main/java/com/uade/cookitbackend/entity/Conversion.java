package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "conversiones")
public class Conversion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idConversion")
    private Integer idConversion;

    @ManyToOne
    @JoinColumn(name = "idUnidadOrigen", nullable = false)
    private Unidad unidadOrigen;

    @ManyToOne
    @JoinColumn(name = "idUnidadDestino", nullable = false)
    private Unidad unidadDestino;

    private Float factorConversiones;
} 