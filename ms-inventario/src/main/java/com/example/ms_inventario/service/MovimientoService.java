package com.example.ms_inventario.service;

import com.example.ms_inventario.dto.MovimientoDetalleResponse;
import com.example.ms_inventario.dto.MovimientoRequest;
import com.example.ms_inventario.dto.ResultadoMovimientoResponse;
import com.example.ms_inventario.exception.ApiException;
import com.example.ms_inventario.model.Movimiento;
import com.example.ms_inventario.model.Producto;
import com.example.ms_inventario.model.TipoMovimiento;
import com.example.ms_inventario.repository.MovimientoRepository;
import com.example.ms_inventario.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Registro de movimientos y consulta del historial
@Service
public class MovimientoService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public MovimientoService(ProductoRepository productoRepository, MovimientoRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public List<MovimientoDetalleResponse> obtenerHistorial() {
        return movimientoRepository.findAllByOrderByFechaDesc().stream()
                .map(MovimientoDetalleResponse::de)
                .toList();
    }

    // Todo en una transacción: si algo falla, no se guarda nada
    @Transactional
    public ResultadoMovimientoResponse registrar(MovimientoRequest request) {
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto no existe"));

        if (request.tipo() == TipoMovimiento.SALIDA) {
            // RN5: toda salida lleva un motivo
            if (request.motivo() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Debes indicar el motivo de la salida");
            }
            // RN3: no puede superar el stock disponible
            if (request.cantidad() > producto.getCantidad()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Stock insuficiente: disponible " + producto.getCantidad());
            }
        }

        int cantidadAnterior = producto.getCantidad();
        int nuevaCantidad = request.tipo() == TipoMovimiento.INGRESO
                ? cantidadAnterior + request.cantidad()
                : cantidadAnterior - request.cantidad();

        // RN6: actualiza el stock y deja el movimiento en el historial
        producto.setCantidad(nuevaCantidad);
        productoRepository.save(producto);

        Movimiento movimiento = new Movimiento();
        movimiento.setProducto(producto);
        movimiento.setUsuarioId(request.usuarioId());
        movimiento.setUsuarioNombre(request.usuarioNombre().trim());
        movimiento.setTipo(request.tipo());
        movimiento.setCantidad(request.cantidad());
        movimiento.setMotivo(request.tipo() == TipoMovimiento.SALIDA ? request.motivo() : null);
        movimiento.setFecha(System.currentTimeMillis());
        movimientoRepository.save(movimiento);

        // RN8: avisa solo cuando recién llega al nivel mínimo
        boolean alcanzoNivelMinimo = cantidadAnterior > producto.getNivelMinimo()
                && nuevaCantidad <= producto.getNivelMinimo();

        return new ResultadoMovimientoResponse(producto, alcanzoNivelMinimo);
    }
}