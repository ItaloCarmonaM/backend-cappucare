package com.example.ms_usuarios.dto;

import com.example.ms_usuarios.model.Perfil;
import com.example.ms_usuarios.model.Usuario;

// Usuario que se devuelve a la app (nunca incluye la clave)
public record UsuarioResponse(
        Integer id,
        String nombre,
        String correo,
        Perfil perfil
) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getPerfil());
    }
}