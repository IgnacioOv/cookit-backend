package com.uade.cookitbackend.entity;

import com.uade.cookitbackend.enums.EstadoHabilitado;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(length = 150, unique = true)
    private String mail;

    @Column(length = 100, nullable = false)
    private String nickname;

    @Column(length = 40, nullable = false)
    private String password;

    @Column(length = 2)
    @Enumerated(EnumType.STRING)
    private EstadoHabilitado habilitado;

    @Column(length = 150)
    private String nombre;

    @Column(length = 150)
    private String direccion;

    @Column(length = 300)
    private String avatar;
} 