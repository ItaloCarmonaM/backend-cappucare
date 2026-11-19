package com.example.ms_inventario.repository;

import com.example.ms_inventario.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Acceso a la tabla movimientos
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    // Historial del más reciente al más antiguo
    List<Movimiento> findAllByOrderByFechaDesc();

    boolean existsByProductoId(Integer productoId);
}