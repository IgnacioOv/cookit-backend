package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "tarjeta_credito_alumnos")
public class TarjetaCreditoAlumno {
    
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarjeta_credito")
    private Integer idTarjetaCredito;
    
    @Column(name = "numero_tarjeta", nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String numeroTarjeta;
    
    @Column(name = "cvv", nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String cvv;
    
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_alumno"))
    private Alumno alumno;
}