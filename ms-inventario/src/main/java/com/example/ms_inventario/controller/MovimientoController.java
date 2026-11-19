package com.example.ms_inventario.controller;

import com.example.ms_inventario.dto.MovimientoDetalleResponse;
import com.example.ms_inventario.dto.MovimientoRequest;
import com.example.ms_inventario.dto.ResultadoMovimientoResponse;
import com.example.ms_inventario.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Movimientos de inventario
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService servicio;

    public MovimientoController(MovimientoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<MovimientoDetalleResponse> obtenerHistorial() {
        return servicio.obtenerHistorial();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultadoMovimientoResponse registrar(@Valid @RequestBody MovimientoRequest request) {
        return servicio.registrar(request);
    }
}