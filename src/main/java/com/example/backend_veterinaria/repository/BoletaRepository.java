package com.example.backend_veterinaria.repository;

import com.example.backend_veterinaria.model.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    List<Boleta> findByCorreoClienteIgnoreCase(String correoCliente);
}