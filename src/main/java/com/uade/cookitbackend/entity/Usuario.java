package com.uade.cookitbackend.entity;

import com.uade.cookitbackend.enums.EstadoHabilitado;
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
@ToString(exclude = {"recetas", "calificaciones"})
@Entity
@Table(name = "usuarios")
public class Usuario {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @NotBlank
    @Email
    @Column(length = 150, unique = true)
    private String mail;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String nickname;

    @Column(length = 40, nullable = false)
    private String password;

    @Column(length = 2)
    @Enumerated(EnumType.STRING)
    private EstadoHabilitado habilitado;

    @NotBlank
    @Size(max = 150)
    @Column(length = 150)
    private String nombre;

    @Column(length = 150)
    private String direccion;

    @Column(length = 300)
    private String avatar;
}