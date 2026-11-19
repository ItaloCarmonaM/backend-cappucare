package com.example.ms_inventario.repository;

import com.example.ms_inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// Acceso a la tabla productos
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findAllByOrderByNombreAsc();

    Optional<Producto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    // Búsqueda por nombre o código
    @Query("SELECT p FROM Producto p " +
            "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "ORDER BY p.nombre")
    List<Producto> buscar(@Param("texto") String texto);

    // Productos en stock bajo o agotados
    @Query("SELECT p FROM Producto p WHERE p.cantidad <= p.nivelMinimo ORDER BY p.cantidad")
    List<Producto> obtenerStockBajo();
}