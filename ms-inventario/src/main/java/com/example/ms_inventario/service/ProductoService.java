package com.example.ms_inventario.service;

import com.example.ms_inventario.dto.ProductoRequest;
import com.example.ms_inventario.exception.ApiException;
import com.example.ms_inventario.model.Producto;
import com.example.ms_inventario.repository.MovimientoRepository;
import com.example.ms_inventario.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

// Lógica de productos
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public ProductoService(ProductoRepository productoRepository, MovimientoRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    // Sin texto devuelve todos los productos; con texto, busca por nombre o código
    public List<Producto> listar(String buscar) {
        if (buscar == null || buscar.isBlank()) {
            return productoRepository.findAllByOrderByNombreAsc();
        }
        return productoRepository.buscar(buscar.trim());
    }

    public List<Producto> obtenerStockBajo() {
        return productoRepository.obtenerStockBajo();
    }

    public Producto obtener(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto no existe"));
    }

    public Producto crear(ProductoRequest request) {
        String codigo = request.codigo().trim();
        // RN1: el código no puede estar repetido
        if (productoRepository.existsByCodigo(codigo)) {
            throw new ApiException(HttpStatus.CONFLICT, "Este código ya está en uso");
        }
        Producto producto = new Producto();
        producto.setCantidad(request.cantidad());
        copiarDatos(producto, request, codigo);
        return productoRepository.save(producto);
    }

    public Producto actualizar(Integer id, ProductoRequest request) {
        Producto producto = obtener(id);
        String codigo = request.codigo().trim();
        boolean usadoPorOtro = productoRepository.findByCodigo(codigo)
                .filter(otro -> !otro.getId().equals(id))
                .isPresent();
        if (usadoPorOtro) {
            throw new ApiException(HttpStatus.CONFLICT, "Este código ya está en uso");
        }
        // RN6: la cantidad no se modifica aquí, solo mediante movimientos
        copiarDatos(producto, request, codigo);
        return productoRepository.save(producto);
    }

    public void eliminar(Integer id) {
        Producto producto = obtener(id);
        // Un producto con historial no se puede eliminar
        if (movimientoRepository.existsByProductoId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "No se puede eliminar un producto con movimientos");
        }
        productoRepository.delete(producto);
    }

    // Copia los datos del formulario (no incluye la cantidad)
    private void copiarDatos(Producto producto, ProductoRequest request, String codigo) {
        producto.setCodigo(codigo);
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion() == null ? "" : request.descripcion().trim());
        producto.setCategoria(request.categoria().trim());
        producto.setUnidadMedida(request.unidadMedida().trim());
        producto.setNivelMinimo(request.nivelMinimo());
        producto.setUbicacion(request.ubicacion().trim());
    }
}