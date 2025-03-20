package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "recetas")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idReceta")
    private Integer idReceta;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @Column(length = 500)
    private String nombreReceta;

    @Column(length = 1000)
    private String descripcionReceta;

    @Column(length = 300)
    private String fotoPrincipal;

    private Integer porciones;

    private Integer cantidadPersonas;

    @ManyToOne
    @JoinColumn(name = "idTipo")
    private TipoReceta tipoReceta;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<IngredienteUtilizado> ingredientesUtilizados;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Paso> pasos;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Foto> fotos;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Calificacion> calificaciones;
} 