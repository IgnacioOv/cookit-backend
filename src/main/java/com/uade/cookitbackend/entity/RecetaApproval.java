package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "receta_approval")
public class RecetaApproval {
    @Id
    @Column(name = "id_receta")
    private Integer idReceta;

    @Column(name = "approved", nullable = false)
    private Boolean approved = false;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_receta")
    private Receta receta;
}