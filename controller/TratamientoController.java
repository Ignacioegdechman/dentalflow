package com.dentalflow.dentalflow.controller;

import com.dentalflow.dentalflow.dto.TratamientoRequest;
import com.dentalflow.dentalflow.dto.TratamientoResponse;
import com.dentalflow.dentalflow.dto.TratamientoUpdateRequest;
import com.dentalflow.dentalflow.service.TratamientoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tratamientos")
public class TratamientoController {

    private final TratamientoService service;

    public TratamientoController(TratamientoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TratamientoResponse> obtenerTodos() {
        return service.obtenerTodos();
    }

    @PostMapping
    public TratamientoResponse crear(@Valid @RequestBody TratamientoRequest tratamiento) {
        return service.crear(tratamiento);
    }

    @PutMapping("/{id}")
    public TratamientoResponse actualizar(@PathVariable Long id, @Valid @RequestBody TratamientoUpdateRequest tratamiento) {
        return service.actualizar(id, tratamiento);
    }

    @GetMapping("/buscar")
    public List<TratamientoResponse> buscar(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre);
    }

}