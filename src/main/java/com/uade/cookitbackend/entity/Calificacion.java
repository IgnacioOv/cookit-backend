package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"usuario", "receta"})
@Entity
@Table(name = "calificaciones")
public class Calificacion {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCalificacion")
    private Integer idCalificacion;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idReceta")
    private Receta receta;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer calificacion;

    @Size(max = 500)
    @Column(length = 500)
    private String comentarios;
} 