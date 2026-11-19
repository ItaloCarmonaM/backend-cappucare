package com.example.ms_inventario.dto;

import com.example.ms_inventario.model.MotivoSalida;
import com.example.ms_inventario.model.TipoMovimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Cuerpo para registrar un ingreso o una salida
public record MovimientoRequest(
        @NotNull(message = "Selecciona un producto")
        Integer productoId,

        @NotNull(message = "El responsable es obligatorio")
        Integer usuarioId,

        @NotBlank(message = "El nombre del responsable es obligatorio")
        String usuarioNombre,

        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipo,

        @NotNull(message = "Ingresa la cantidad")
        @Positive(message = "La cantidad debe ser mayor a 0") // RN4
        Integer cantidad,

        // Solo aplica a las salidas
        MotivoSalida motivo
) {
}