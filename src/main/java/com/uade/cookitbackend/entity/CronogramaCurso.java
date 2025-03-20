package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "cronogramaCursos")
public class CronogramaCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCronograma")
    private Integer idCronograma;

    @ManyToOne
    @JoinColumn(name = "idSede", nullable = false)
    private Sede sede;

    @ManyToOne
    @JoinColumn(name = "idCurso", nullable = false)
    private Curso curso;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer vacantesDisponibles;

    @OneToMany(mappedBy = "cronograma", cascade = CascadeType.ALL)
    private List<AsistenciaCurso> asistencias;
} 