package com.uade.cookitbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "inscripcion_cursos")
public class InscripcionCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscripcion")
    private Integer idInscripcion;

    @ManyToOne
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "id_cronograma", nullable = false)
    private CronogramaCurso cronograma;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @Column(name = "estado", nullable = false)
    private String estado; // inscripto, baja, finalizado

    @Column(name = "monto_pagado", nullable = false)
    private java.math.BigDecimal montoPagado;

    @Column(name = "monto_reintegrado")
    private java.math.BigDecimal montoReintegrado;

    @Column(name = "tipo_pago")
    private String tipoPago; // tarjeta_credito, cuenta_corriente, efectivo_sede

    @Column(name = "numero_transaccion")
    private String numeroTransaccion;

    @Column(name = "saldo_cuenta_corriente", precision = 12, scale = 2)
    private java.math.BigDecimal saldoCuentaCorriente;

    @Column(name = "factura_enviada")
    private Boolean facturaEnviada = false;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    @Column(name = "motivo_baja")
    private String motivoBaja;
}
