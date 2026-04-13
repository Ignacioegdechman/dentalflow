package com.dentalflow.dentalflow.controller;

import com.dentalflow.dentalflow.dto.AuthResponse;
import com.dentalflow.dentalflow.dto.LoginRequest;
import com.dentalflow.dentalflow.dto.RegisterRequest;
import com.dentalflow.dentalflow.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService service;

    public AuthController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public AuthResponse registrar(@Valid @RequestBody RegisterRequest request){
        return service.registrar(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){
        return service.login(request.getUsername(), request.getPassword());
    }

}