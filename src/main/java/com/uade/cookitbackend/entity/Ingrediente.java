package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "ingredientes")
public class Ingrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idIngrediente")
    private Integer idIngrediente;

    @Column(length = 200)
    private String nombre;

    @OneToMany(mappedBy = "ingrediente", cascade = CascadeType.ALL)
    private List<IngredienteUtilizado> utilizados;
} 