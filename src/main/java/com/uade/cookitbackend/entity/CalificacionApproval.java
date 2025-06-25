package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "calificacion_approval")
public class CalificacionApproval {
    @Id
    @Column(name = "id_calificacion")
    private Integer idCalificacion;

    @Column(name = "approved", nullable = false)
    private Boolean approved = false;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_calificacion")
    private Calificacion calificacion;
}