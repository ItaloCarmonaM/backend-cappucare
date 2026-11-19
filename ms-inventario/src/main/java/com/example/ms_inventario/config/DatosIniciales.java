package com.example.ms_inventario.config;

import com.example.ms_inventario.model.MotivoSalida;
import com.example.ms_inventario.model.Movimiento;
import com.example.ms_inventario.model.Producto;
import com.example.ms_inventario.model.TipoMovimiento;
import com.example.ms_inventario.repository.MovimientoRepository;
import com.example.ms_inventario.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Datos ficticios que se cargan la primera vez que se inicia el microservicio
@Configuration
public class DatosIniciales {

    private static final long DIA = 24L * 60 * 60 * 1000; // Día en milisegundos

    @Bean
    CommandLineRunner cargarInventario(ProductoRepository productos, MovimientoRepository movimientos) {
        return args -> {
            // Solo se cargan si la tabla está vacía
            if (productos.count() > 0) return;

            // Productos (disponibles, stock bajo y agotados)
            Producto guantes = productos.save(crearProducto("MED-001", "Guantes de nitrilo talla M", "Caja de 100 unidades", "Protección", 120, "caja", 30, "Bodega A - Estante 1"));
            Producto mascarillas = productos.save(crearProducto("MED-002", "Mascarillas quirúrgicas", "Caja de 50 unidades", "Protección", 25, "caja", 30, "Bodega A - Estante 1"));
            productos.save(crearProducto("MED-003", "Alcohol gel 500 ml", "Desinfectante de manos", "Higiene", 60, "unidad", 20, "Bodega A - Estante 2"));
            productos.save(crearProducto("MED-004", "Jeringas 5 ml", "Caja de 100 unidades", "Material clínico", 0, "caja", 15, "Bodega B - Estante 1"));
            Producto gasas = productos.save(crearProducto("MED-005", "Gasas estériles 10x10", "Paquete de 20 unidades", "Material clínico", 80, "paquete", 25, "Bodega B - Estante 1"));
            productos.save(crearProducto("MED-006", "Termómetro digital", "Medición de temperatura corporal", "Equipos", 12, "unidad", 10, "Bodega B - Estante 2"));
            productos.save(crearProducto("MED-007", "Tensiómetro digital", "Medidor de presión de brazo", "Equipos", 5, "unidad", 5, "Bodega B - Estante 2"));
            productos.save(crearProducto("MED-008", "Suero fisiológico 500 ml", "Solución salina estéril", "Soluciones", 40, "unidad", 15, "Bodega C - Refrigerado"));

            // Algunos movimientos para que el historial no parta vacío (usuarios 1 y 2 de ms-usuarios)
            long ahora = System.currentTimeMillis();
            movimientos.save(crearMovimiento(guantes, 1, "María González", TipoMovimiento.INGRESO, 100, null, ahora - 5 * DIA));
            movimientos.save(crearMovimiento(mascarillas, 2, "Pedro Soto", TipoMovimiento.SALIDA, 5, MotivoSalida.VENTA, ahora - 2 * DIA));
            movimientos.save(crearMovimiento(gasas, 1, "María González", TipoMovimiento.SALIDA, 10, MotivoSalida.PRODUCTO_VENCIDO, ahora - DIA));
        };
    }

    private Producto crearProducto(String codigo, String nombre, String descripcion, String categoria,
                                   int cantidad, String unidadMedida, int nivelMinimo, String ubicacion) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setCategoria(categoria);
        p.setCantidad(cantidad);
        p.setUnidadMedida(unidadMedida);
        p.setNivelMinimo(nivelMinimo);
        p.setUbicacion(ubicacion);
        return p;
    }

    private Movimiento crearMovimiento(Producto producto, int usuarioId, String usuarioNombre,
                                       TipoMovimiento tipo, int cantidad, MotivoSalida motivo, long fecha) {
        Movimiento m = new Movimiento();
        m.setProducto(producto);
        m.setUsuarioId(usuarioId);
        m.setUsuarioNombre(usuarioNombre);
        m.setTipo(tipo);
        m.setCantidad(cantidad);
        m.setMotivo(motivo);
        m.setFecha(fecha);
        return m;
    }
}   