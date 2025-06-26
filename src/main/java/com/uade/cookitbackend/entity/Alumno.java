package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"asistencias"})
@Entity
@Table(name = "alumnos")
public class Alumno {
    @EqualsAndHashCode.Include
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