package com.example.backend_veterinaria.controller;

import com.example.backend_veterinaria.model.Boleta;
import com.example.backend_veterinaria.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/boletas")
@Tag(name = "4. Boletas y Órdenes de Compra", description = "Emisión de boletas en Checkout y consulta de órdenes para ADMIN y VENDEDOR")
public class BoletaController {

    private final BoletaService boletaService;

    public BoletaController(BoletaService boletaService) {
        this.boletaService = boletaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las boletas emitidas (ADMIN y VENDEDOR)")
    public List<Boleta> listarBoletas() {
        return boletaService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle completo de una boleta por ID (ADMIN y VENDEDOR)")
    public ResponseEntity<Boleta> obtenerBoleta(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Emitir nueva boleta de compra y descontar stock", description = "Registra la orden del cliente, sus detalles y descuenta el stock de cada producto")
    public ResponseEntity<?> crearBoleta(@RequestBody Map<String, Object> payload) {
        try {
            Boleta nueva = boletaService.emitirBoleta(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}