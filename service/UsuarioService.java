package com.dentalflow.dentalflow.service;

import com.dentalflow.dentalflow.dto.AuthResponse;
import com.dentalflow.dentalflow.dto.RegisterRequest;
import com.dentalflow.dentalflow.enums.RolUsuario;
import com.dentalflow.dentalflow.model.Usuario;
import com.dentalflow.dentalflow.repository.UsuarioRepository;
import com.dentalflow.dentalflow.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse registrar(RegisterRequest request) {
        String username = request.getUsername().trim();

        if (repository.existsByUsername(username)) {
            throw new IllegalStateException("El usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(RolUsuario.RECEPCIONISTA);

        Usuario guardado = repository.save(usuario);
        String token = jwtService.generateToken(guardado);

        return new AuthResponse(token, guardado.getUsername(), guardado.getRol());
    }

    public AuthResponse login(String username, String password) {

        Usuario usuario = repository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return new AuthResponse(jwtService.generateToken(usuario), usuario.getUsername(), usuario.getRol());
    }
}