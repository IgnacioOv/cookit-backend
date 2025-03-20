package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "alumnos")
public class Alumno {
    @Id
    @Column(name = "idAlumno")
    private Integer idAlumno;

    @Column(length = 12)
    private String numeroTarjeta;

    @Column(length = 300)
    private String dniFrente;

    @Column(length = 300)
    private String dniFondo;

    @Column(length = 12)
    private String tramite;

    @Column(precision = 12, scale = 2)
    private BigDecimal cuentaCorriente;

    @OneToOne
    @MapsId
    @JoinColumn(name = "idAlumno")
    private Usuario usuario;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<AsistenciaCurso> asistencias;
} 