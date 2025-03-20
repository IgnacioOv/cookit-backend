package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "multimedia")
public class Multimedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idContenido")
    private Integer idContenido;

    @ManyToOne
    @JoinColumn(name = "idPaso", nullable = false)
    private Paso paso;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private TipoContenido tipoContenido;

    @Column(length = 5)
    private String extension;

    @Column(length = 300)
    private String urlContenido;
}

enum TipoContenido {
    foto, video, audio
} 