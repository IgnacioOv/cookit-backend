package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "multimedia")
public class Multimedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contenido")
    private Integer idContenido;

    @ManyToOne
    @JoinColumn(name = "id_paso", nullable = false)
    private Paso paso;

    @Column(length = 10)
    private String tipoContenido;

    @Column(length = 5)
    private String extension;

    @Column(length = 300)
    private String urlContenido;
}
