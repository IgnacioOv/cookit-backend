package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"ingredientesUtilizados", "pasos", "fotos", "calificaciones"})
@Entity
@Table(name = "recetas")
public class Receta {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta")
    private Integer idReceta;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
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
    @BatchSize(size = 10)
    private List<IngredienteUtilizado> ingredientesUtilizados;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<Paso> pasos;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Foto> fotos;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<Calificacion> calificaciones;
} 