package com.example.ms_inventario.dto;

import com.example.ms_inventario.model.MotivoSalida;
import com.example.ms_inventario.model.Movimiento;
import com.example.ms_inventario.model.TipoMovimiento;

// Movimiento tal como lo espera la app
public record MovimientoResponse(
        Integer id,
        Integer productoId,
        Integer usuarioId,
        TipoMovimiento tipo,
        Integer cantidad,
        MotivoSalida motivo,
        Long fecha
) {
    public static MovimientoResponse de(Movimiento m) {
        return new MovimientoResponse(
                m.getId(), m.getProducto().getId(), m.getUsuarioId(),
                m.getTipo(), m.getCantidad(), m.getMotivo(), m.getFecha()
        );
    }
}