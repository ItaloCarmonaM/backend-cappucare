package com.example.ms_inventario.dto;

import com.example.ms_inventario.model.Movimiento;

// Movimiento junto al nombre del producto y del responsable (historial)
public record MovimientoDetalleResponse(
        MovimientoResponse movimiento,
        String productoNombre,
        String usuarioNombre
) {
    public static MovimientoDetalleResponse de(Movimiento m) {
        return new MovimientoDetalleResponse(
                MovimientoResponse.de(m), m.getProducto().getNombre(), m.getUsuarioNombre()
        );
    }
}