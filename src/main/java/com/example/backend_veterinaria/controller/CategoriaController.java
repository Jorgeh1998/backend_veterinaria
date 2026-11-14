package com.example.backend_veterinaria.controller;

import com.example.backend_veterinaria.model.Categoria;
import com.example.backend_veterinaria.repository.CategoriaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "3. Categorías", description = "Gestión de categorías terapéuticas de medicamentos")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todas las categorías")
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoría (Solo ADMIN)")
    public ResponseEntity<Categoria> crear(@RequestBody Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaRepository.save(categoria));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría por ID (Solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}