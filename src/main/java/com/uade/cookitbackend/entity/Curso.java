package com.uade.cookitbackend.entity;

import com.uade.cookitbackend.enums.ModalidadCurso;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "cursos")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCurso")
    private Integer idCurso;

    @Column(length = 300)
    private String descripcion;

    @Column(length = 500)
    private String contenidos;

    @Column(length = 500)
    private String requerimientos;

    private Integer duracion;

    @Column(precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ModalidadCurso modalidad;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<CronogramaCurso> cronogramas;
} 