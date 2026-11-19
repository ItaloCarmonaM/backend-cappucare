package com.example.ms_inventario.exception;

import org.springframework.http.HttpStatus;

// Error de negocio con el código HTTP que se debe responder
public class ApiException extends RuntimeException {

    private final HttpStatus estado;

    public ApiException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}