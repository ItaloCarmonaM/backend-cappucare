package com.example.ms_inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Registro de un ingreso o salida de productos (tabla movimientos)
@Entity
@Table(name = "movimientos")
@Getter
@Setter
@NoArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Responsable: el usuario vive en otro microservicio, por eso se guarda una copia de su nombre
    @Column(nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private String usuarioNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private Integer cantidad;

    // Solo aplica a las salidas
    @Enumerated(EnumType.STRING)
    private MotivoSalida motivo;

    // Fecha en milisegundos
    @Column(nullable = false)
    private Long fecha;
}