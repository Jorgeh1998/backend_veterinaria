package com.example.backend_veterinaria.service;

import com.example.backend_veterinaria.model.Categoria;
import com.example.backend_veterinaria.model.Producto;
import com.example.backend_veterinaria.repository.CategoriaRepository;
import com.example.backend_veterinaria.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public List<Producto> listarCriticos() {
        return productoRepository.findByStockLessThanEqual(5);
    }

    public Producto guardarProducto(Map<String, Object> payload) {
        String nombre = (String) payload.get("nombre");
        String nombreCategoria = (String) payload.get("categoria");
        Integer precio = Integer.valueOf(payload.get("precio").toString());
        Integer stock = payload.get("stock") != null ? Integer.valueOf(payload.get("stock").toString()) : 10;
        String img = (String) payload.getOrDefault("img", "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=400&auto=format&fit=crop");

        Categoria categoria = categoriaRepository.findByNombreIgnoreCase(nombreCategoria)
                .orElseGet(() -> categoriaRepository.save(new Categoria(nombreCategoria, "Categoría clínica veterinaria")));

        long correlativo = productoRepository.count() + 22;
        String codigo = (String) payload.getOrDefault("codigo", "ME" + String.format("%03d", correlativo));

        Producto nuevo = new Producto(codigo, nombre, precio, stock, img, categoria);
        return productoRepository.save(nuevo);
    }

    public Producto actualizarProducto(Long id, Map<String, Object> payload) {
        Producto existente = obtenerPorId(id);

        if (payload.containsKey("nombre")) existente.setNombre((String) payload.get("nombre"));
        if (payload.containsKey("precio")) existente.setPrecio(Integer.valueOf(payload.get("precio").toString()));
        if (payload.containsKey("stock")) existente.setStock(Integer.valueOf(payload.get("stock").toString()));
        if (payload.containsKey("img") && payload.get("img") != null) existente.setImg((String) payload.get("img"));

        if (payload.containsKey("categoria")) {
            String nombreCat = (String) payload.get("categoria");
            Categoria cat = categoriaRepository.findByNombreIgnoreCase(nombreCat)
                    .orElseGet(() -> categoriaRepository.save(new Categoria(nombreCat, "Categoría clínica veterinaria")));
            existente.setCategoria(cat);
        }

        return productoRepository.save(existente);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}