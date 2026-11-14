package com.example.backend_veterinaria.service;

import com.example.backend_veterinaria.model.Usuario;
import com.example.backend_veterinaria.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario crearUsuario(Usuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            usuario.setPassword("123456");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("CLIENTE");
        } else {
            usuario.setRol(usuario.getRol().toUpperCase());
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario datos) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setNombre(datos.getNombre());
        u.setCorreo(datos.getCorreo());
        if (datos.getRol() != null) u.setRol(datos.getRol().toUpperCase());
        if (datos.getRegion() != null) u.setRegion(datos.getRegion());
        if (datos.getComuna() != null) u.setComuna(datos.getComuna());
        return usuarioRepository.save(u);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}