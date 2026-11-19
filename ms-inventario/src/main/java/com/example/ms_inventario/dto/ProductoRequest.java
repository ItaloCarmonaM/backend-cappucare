package com.example.ms_inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Cuerpo para crear o editar un producto
public record ProductoRequest(
        @NotBlank(message = "Ingresa el código")
        String codigo,

        @NotBlank(message = "Ingresa el nombre")
        String nombre,

        // Opcional
        String descripcion,

        @NotBlank(message = "Ingresa la categoría")
        String categoria,

        @NotNull(message = "Ingresa la cantidad")
        @Min(value = 0, message = "La cantidad no puede ser menor a 0") // RN2
        Integer cantidad,

        @NotBlank(message = "Ingresa la unidad de medida")
        String unidadMedida,

        @NotNull(message = "Ingresa el nivel mínimo")
        @Min(value = 0, message = "El nivel mínimo no puede ser menor a 0")
        Integer nivelMinimo,

        @NotBlank(message = "Ingresa la ubicación")
        String ubicacion
) {
}