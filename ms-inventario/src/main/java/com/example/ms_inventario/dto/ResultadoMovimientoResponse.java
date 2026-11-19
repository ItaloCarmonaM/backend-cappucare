package com.example.ms_inventario.dto;

import com.example.ms_inventario.model.Producto;

// Resultado de registrar un movimiento: producto actualizado y aviso de nivel mínimo (RN8)
public record ResultadoMovimientoResponse(
        Producto producto,
        boolean alcanzoNivelMinimo
) {
}