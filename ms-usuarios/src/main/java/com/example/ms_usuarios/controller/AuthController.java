package com.example.ms_usuarios.controller;

import com.example.ms_usuarios.dto.LoginRequest;
import com.example.ms_usuarios.dto.UsuarioResponse;
import com.example.ms_usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Inicio de sesión
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService servicio;

    public AuthController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request) {
        return UsuarioResponse.de(servicio.login(request));
    }
}