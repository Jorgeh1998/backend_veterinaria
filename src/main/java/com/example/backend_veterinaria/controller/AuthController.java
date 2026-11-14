package com.example.backend_veterinaria.controller;

import com.example.backend_veterinaria.model.Usuario;
import com.example.backend_veterinaria.repository.UsuarioRepository;
import com.example.backend_veterinaria.security.JwtUtil;
import com.example.backend_veterinaria.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "1. Autenticación y Sesiones (JWT)", description = "Endpoints para login con generación de token JWT y registro de clientes")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository,
                          UsuarioService usuarioService,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario y obtener Token JWT", description = "Valida correo y contraseña, retornando el token JWT y el rol asignado (ADMIN, VENDEDOR o CLIENTE)")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String password = credenciales.get("password");

        Optional<Usuario> userOpt = usuarioRepository.findByCorreo(correo);
        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol(), usuario.getNombre());
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "id", usuario.getId(),
                        "nombre", usuario.getNombre(),
                        "correo", usuario.getCorreo(),
                        "rol", usuario.getRol()
                ));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Credenciales inválidas. Verifica tu correo y contraseña."));
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo cliente", description = "Crea una cuenta con rol CLIENTE y retorna su token JWT de sesión")
    public ResponseEntity<?> registrar(@RequestBody Usuario nuevoUsuario) {
        if (usuarioRepository.existsByCorreo(nuevoUsuario.getCorreo())) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo ya se encuentra registrado."));
        }
        nuevoUsuario.setRol("CLIENTE");
        Usuario guardado = usuarioService.crearUsuario(nuevoUsuario);
        String token = jwtUtil.generarToken(guardado.getCorreo(), guardado.getRol(), guardado.getNombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "token", token,
                "id", guardado.getId(),
                "nombre", guardado.getNombre(),
                "correo", guardado.getCorreo(),
                "rol", guardado.getRol()
        ));
    }
}