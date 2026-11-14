package com.example.backend_veterinaria.controller;

import com.example.backend_veterinaria.model.Producto;
import com.example.backend_veterinaria.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "2. Productos (v1 y v2)", description = "Operaciones CRUD de medicamentos e inventario veterinario")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/api/v1/productos")
    @Operation(summary = "Listar todos los productos (v1)", description = "Obtiene el catálogo completo de productos con su categoría y stock actual")
    public List<Producto> listarProductosV1() {
        return productoService.listarTodos();
    }

    @GetMapping("/api/v1/productos/{id}")
    @Operation(summary = "Obtener detalle de un producto por ID", description = "Devuelve los datos detallados de un medicamento específico")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/api/v1/productos/criticos")
    @Operation(summary = "Listar productos con stock crítico", description = "Filtra los productos cuyo stock sea menor o igual a 5 unidades")
    public List<Producto> listarCriticos() {
        return productoService.listarCriticos();
    }

    @PostMapping("/api/v1/productos")
    @Operation(summary = "Crear un nuevo producto (Solo ADMIN)", description = "Registra un nuevo medicamento en la base de datos MySQL")
    public ResponseEntity<Producto> crearProducto(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardarProducto(payload));
    }

    @PutMapping("/api/v1/productos/{id}")
    @Operation(summary = "Actualizar un producto existente (Solo ADMIN)", description = "Modifica nombre, categoría, precio o stock de un producto")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, payload));
    }

    @DeleteMapping("/api/v1/productos/{id}")
    @Operation(summary = "Eliminar un producto por ID (Solo ADMIN)", description = "Elimina físicamente el producto de la base de datos")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint v2 con estadisticas detalladas de inventario (Requisito de versionado Anexo 2)
    @GetMapping("/api/v2/productos")
    @Operation(summary = "Listar productos con estadísticas detalladas de inventario (v2)", description = "Versión 2 del endpoint que incluye valorización total y conteo de alertas de stock")
    public ResponseEntity listarProductosV2() {
        List<Producto> lista = productoService.listarTodos();
        int valorTotalInventario = lista.stream().mapToInt(p -> p.getPrecio() * p.getStock()).sum();
        long cantidadCriticos = lista.stream().filter(p -> p.getStock() <= 5).count();

        return ResponseEntity.ok(Map.of(
                "version", "v2",
                "totalProductos", lista.size(),
                "productosEnStockCritico", cantidadCriticos,
                "valorizacionTotalCLP", valorTotalInventario,
                "productos", lista
        ));
    }
}