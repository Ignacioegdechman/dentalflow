package com.dentalflow.dentalflow.controller;

import com.dentalflow.dentalflow.dto.PacienteRequest;
import com.dentalflow.dentalflow.dto.PacienteResponse;
import com.dentalflow.dentalflow.dto.PacienteUpdateRequest;
import com.dentalflow.dentalflow.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<PacienteResponse> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/pagina")
    public Page<PacienteResponse> obtenerPagina(Pageable pageable) {
        return service.obtenerPagina(pageable);
    }

    @GetMapping("/buscar")
    public List<PacienteResponse> buscar(@RequestParam String nombre) {
        return service.buscar(nombre);
    }

    @PostMapping
    public PacienteResponse crear(@Valid @RequestBody PacienteRequest paciente) {
        return service.crear(paciente);
    }

    @PutMapping("/{id}")
    public PacienteResponse actualizar(@PathVariable Long id, @Valid @RequestBody PacienteUpdateRequest paciente) {
        return service.actualizar(id, paciente);
    }

    @PatchMapping("/{id}/reactivar")
    public PacienteResponse reactivar(@PathVariable Long id) {
        return service.reactivar(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}