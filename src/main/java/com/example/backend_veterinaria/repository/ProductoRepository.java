package com.example.backend_veterinaria.repository;

import com.example.backend_veterinaria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaNombreIgnoreCase(String nombreCategoria);
    List<Producto> findByStockLessThanEqual(Integer stockMaximo);
}