package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "pasos")
public class Paso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paso")
    private Integer idPaso;

    @ManyToOne
    @JoinColumn(name = "id_receta")
    private Receta receta;

    @Column(name = "nro_paso")
    private Integer numeroPaso;

    @Column(length = 4000)
    private String texto;

    @OneToMany(mappedBy = "paso", cascade = CascadeType.ALL)
    private List<Multimedia> multimedia;
} 