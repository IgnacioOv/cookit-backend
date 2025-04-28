package com.uade.cookitbackend.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "tiposReceta")
public class TipoReceta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTipo")
    private Integer idTipo;

    @Column(length = 250)
    private String descripcion;

    @JsonIgnore
    @OneToMany(mappedBy = "tipoReceta", cascade = CascadeType.ALL)
    private List<Receta> recetas;
} 