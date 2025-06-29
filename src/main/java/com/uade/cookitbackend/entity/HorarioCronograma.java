package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "horarios_cronograma")
public class HorarioCronograma {
    
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Integer idHorario;
    
    @NotNull
    @Column(name = "id_cronograma", nullable = false)
    private Integer idCronograma;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cronograma", insertable = false, updatable = false)
    private CronogramaCurso cronograma;
    
    @NotNull
    @Column(name = "dia_semana", nullable = false, length = 20)
    private String diaSemana; // LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
    
    @NotNull
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    @NotNull
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    
    @Column(name = "observaciones", length = 500)
    private String observaciones;
}