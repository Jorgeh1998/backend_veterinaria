package com.example.backend_veterinaria.service;

import com.example.backend_veterinaria.model.Boleta;
import com.example.backend_veterinaria.model.DetalleBoleta;
import com.example.backend_veterinaria.model.Producto;
import com.example.backend_veterinaria.repository.BoletaRepository;
import com.example.backend_veterinaria.repository.ProductoRepository;
import com.example.backend_veterinaria.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public BoletaService(BoletaRepository boletaRepository,
                         ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.boletaRepository = boletaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Boleta> listarTodas() {
        return boletaRepository.findAll();
    }

    public Boleta obtenerPorId(Long id) {
        return boletaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boleta no encontrada con ID: " + id));
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Boleta emitirBoleta(Map<String, Object> payload) {
        Boleta boleta = new Boleta();
        boleta.setCodigoOrden("ORD-" + System.currentTimeMillis());
        boleta.setFecha(LocalDateTime.now());
        boleta.setEstado("Pagado");

        Map<String, String> cliente = (Map<String, String>) payload.get("cliente");
        if (cliente != null) {
            boleta.setNombreCliente(cliente.get("nombre"));
            boleta.setApellidosCliente(cliente.get("apellidos"));
            boleta.setCorreoCliente(cliente.get("correo"));
            boleta.setCalle(cliente.get("calle"));
            boleta.setDepto(cliente.get("depto"));
            boleta.setRegion(cliente.get("region"));
            boleta.setComuna(cliente.get("comuna"));
            boleta.setIndicaciones(cliente.get("indicaciones"));

            if (cliente.get("correo") != null) {
                usuarioRepository.findByCorreo(cliente.get("correo")).ifPresent(boleta::setUsuario);
            }
        }

        List<?> items = (List<?>) payload.get("items");
        int sumaTotal = 0;

        if (items != null) {
            for (Object obj : items) {
                Map<?, ?> item = (Map<?, ?>) obj;
                Long prodId = Long.valueOf(item.get("id").toString());
                Integer cantidad = Integer.valueOf(item.get("cantidad").toString());

                Producto producto = productoRepository.findById(prodId)
                        .orElseThrow(() -> new RuntimeException("Producto no existe: " + prodId));

                if (producto.getStock() < cantidad) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
                }

                // Logica de negocio: descontar stock del inventario
                producto.setStock(producto.getStock() - cantidad);
                productoRepository.save(producto);

                DetalleBoleta detalle = new DetalleBoleta(cantidad, producto.getPrecio(), boleta, producto);
                boleta.getDetalles().add(detalle);
                sumaTotal += detalle.getSubtotal();
            }
        }

        boleta.setTotal(sumaTotal);
        return boletaRepository.save(boleta);
    }
}