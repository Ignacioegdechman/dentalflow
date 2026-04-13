package com.dentalflow.dentalflow.controller;

import com.dentalflow.dentalflow.dto.HistoriaClinicaRequest;
import com.dentalflow.dentalflow.dto.HistoriaClinicaResponse;
import com.dentalflow.dentalflow.service.HistoriaClinicaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historia-clinica")
public class HistoriaClinicaController {

    private final HistoriaClinicaService service;

    public HistoriaClinicaController(HistoriaClinicaService service) {
        this.service = service;
    }

    @GetMapping
    public List<HistoriaClinicaResponse> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/paciente/{pacienteId}")
    public Page<HistoriaClinicaResponse> obtenerPorPaciente(@PathVariable Long pacienteId, Pageable pageable) {
        return service.obtenerPorPaciente(pacienteId, pageable);
    }

    @PostMapping
    public HistoriaClinicaResponse crear(@Valid @RequestBody HistoriaClinicaRequest historia) {
        return service.crear(historia);
    }

}