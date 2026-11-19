package com.example.ms_usuarios.dto;

import jakarta.validation.constraints.NotBlank;

// Cuerpo del login
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String clave
) {
}