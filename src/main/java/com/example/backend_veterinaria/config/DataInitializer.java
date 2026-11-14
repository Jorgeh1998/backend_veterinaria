package com.example.backend_veterinaria.config;

import com.example.backend_veterinaria.model.*;
import com.example.backend_veterinaria.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final BoletaRepository boletaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           CategoriaRepository categoriaRepository,
                           ProductoRepository productoRepository,
                           BoletaRepository boletaRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.boletaRepository = boletaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Crear los 3 usuarios con los roles oficiales si no existen
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario(
                    "Administrador General",
                    "admin@gmail.com",
                    passwordEncoder.encode("admin123"),
                    "ADMIN",
                    "Región de O'Higgins",
                    "Rancagua"
            ));
            usuarioRepository.save(new Usuario(
                    "Vendedor Sucursal",
                    "vendedor@duoc.cl",
                    passwordEncoder.encode("vendedor123"),
                    "VENDEDOR",
                    "Región de O'Higgins",
                    "Rancagua"
            ));
            usuarioRepository.save(new Usuario(
                    "Camila Soto (Cliente)",
                    "cliente@duoc.cl",
                    passwordEncoder.encode("cliente123"),
                    "CLIENTE",
                    "Región de Valparaíso",
                    "Viña del Mar"
            ));
        }

        // 2. Poblar categorias y productos si no se ejecutó el script SQL antes
        if (categoriaRepository.count() == 0) {
            Categoria c1 = categoriaRepository.save(new Categoria("Antibióticos", "Infecciones bacterianas"));
            Categoria c2 = categoriaRepository.save(new Categoria("Antiparasitarios", "Control de parásitos"));
            Categoria c3 = categoriaRepository.save(new Categoria("Antiinflamatorios", "Alivio del dolor"));
            Categoria c4 = categoriaRepository.save(new Categoria("Dermatología", "Cuidado de piel"));
            Categoria c5 = categoriaRepository.save(new Categoria("Cardíaco", "Salud cardíaca"));
            Categoria c6 = categoriaRepository.save(new Categoria("Suplementos", "Vitaminas"));

            productoRepository.saveAll(List.of(
                new Producto("ME001", "Amoxibay 250mg", 4200, 15, "https://plus.unsplash.com/premium_photo-1786961711020-1b55448cedb2?q=80&w=1170&auto=format&fit=crop", c1),
                new Producto("ME004", "Nexgard Masticable", 9500, 3, "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=400&auto=format&fit=crop", c2),
                new Producto("ME005", "Bravecto Masticable", 18900, 12, "https://images.unsplash.com/photo-1623387641168-d9803ddd3f35?q=80&w=400&auto=format&fit=crop", c2),
                new Producto("ME006", "Revolution Plus Gato", 14500, 2, "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=400&auto=format&fit=crop", c2),
                new Producto("ME009", "Meloxicam 1mg", 4500, 20, "https://images.unsplash.com/photo-1587854692152-cbe660dbde88?q=80&w=400&auto=format&fit=crop", c3),
                new Producto("ME011", "Clorhexidina Shampoo", 8900, 4, "https://images.unsplash.com/photo-1597595735781-6a57fb8e3e3d?q=80&w=687&auto=format&fit=crop", c4),
                new Producto("ME016", "Vetmedin 2.5mg", 28000, 8, "https://images.unsplash.com/photo-1673134768453-ffaf2f279b81?q=80&w=1174&auto=format&fit=crop", c5),
                new Producto("ME021", "Omega vet 3-6-9", 9900, 10, "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?q=80&w=400&auto=format&fit=crop", c6)
            ));
        }

        // 3. Crear una boleta inicial de ejemplo si la tabla está vacía
        if (boletaRepository.count() == 0 && productoRepository.count() > 0) {
            Producto prod1 = productoRepository.findAll().get(0);
            Usuario cliente = usuarioRepository.findByCorreo("cliente@duoc.cl").orElse(null);

            Boleta b = new Boleta();
            b.setCodigoOrden("ORD-10001");
            b.setEstado("Pagado");
            b.setNombreCliente("Camila");
            b.setApellidosCliente("Soto");
            b.setCorreoCliente("cliente@duoc.cl");
            b.setCalle("Av. Libertad 450");
            b.setDepto("Depto 302");
            b.setRegion("Región de Valparaíso");
            b.setComuna("Viña del Mar");
            b.setIndicaciones("Entregar en recepción");
            b.setUsuario(cliente);

            DetalleBoleta det = new DetalleBoleta(2, prod1.getPrecio(), b, prod1);
            b.getDetalles().add(det);
            b.setTotal(det.getSubtotal());

            boletaRepository.save(b);
        }
    }
}