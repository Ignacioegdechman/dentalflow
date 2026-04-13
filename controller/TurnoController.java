package com.dentalflow.dentalflow.controller;

import com.dentalflow.dentalflow.dto.TurnoRequest;
import com.dentalflow.dentalflow.dto.TurnoFilterRequest;
import com.dentalflow.dentalflow.dto.TurnoReprogramarRequest;
import com.dentalflow.dentalflow.dto.TurnoResponse;
import com.dentalflow.dentalflow.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TurnoResponse> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/hoy")
    public List<TurnoResponse> turnosHoy() {
        return service.turnosHoy();
    }

    @GetMapping("/buscar")
    public Page<TurnoResponse> buscar(@ModelAttribute TurnoFilterRequest filter, Pageable pageable) {
        return service.buscar(filter, pageable);
    }

    @PostMapping
    public TurnoResponse crear(@Valid @RequestBody TurnoRequest turno) {
        return service.crear(turno);
    }

    @PatchMapping("/{id}/confirmar")
    public TurnoResponse confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @PatchMapping("/{id}/cancelar")
    public TurnoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PatchMapping("/{id}/reprogramar")
    public TurnoResponse reprogramar(@PathVariable Long id, @Valid @RequestBody TurnoReprogramarRequest request) {
        return service.reprogramar(id, request);
    }
}