package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "sedes")
public class Sede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSede")
    private Integer idSede;

    @Column(length = 150, nullable = false)
    private String nombreSede;

    @Column(length = 250, nullable = false)
    private String direccionSede;

    @Column(length = 15)
    private String telefonoSede;

    @Column(length = 150)
    private String mailSede;

    @Column(length = 15)
    private String whatsApp;

    @Column(length = 20)
    private String tipoBonificacion;

    @Column(precision = 10, scale = 2)
    private BigDecimal bonificacionCursos;

    @Column(length = 20)
    private String tipoPromocion;

    @Column(precision = 10, scale = 2)
    private BigDecimal promocionCursos;

    @OneToMany(mappedBy = "sede", cascade = CascadeType.ALL)
    private List<CronogramaCurso> cronogramas;
} 