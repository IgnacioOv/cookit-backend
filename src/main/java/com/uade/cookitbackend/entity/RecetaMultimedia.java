package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"receta"})
@Entity
@Table(name = "receta_multimedia")
public class RecetaMultimedia {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_multimedia")
    private Integer idMultimedia;

    @ManyToOne
    @JoinColumn(name = "id_receta", nullable = false)
    private Receta receta;

    @Column(name = "url_multimedia", length = 500, nullable = false)
    private String urlMultimedia;

    @Column(name = "fecha_subida", nullable = false)
    private ZonedDateTime fechaSubida;

    @PrePersist
    protected void onCreate() {
        if (fechaSubida == null) {
            fechaSubida = ZonedDateTime.now();
        }
    }
}