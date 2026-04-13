package com.dentalflow.dentalflow.controller;

import com.dentalflow.dentalflow.dto.PagoFilterRequest;
import com.dentalflow.dentalflow.dto.PagoRequest;
import com.dentalflow.dentalflow.dto.PagoResponse;
import com.dentalflow.dentalflow.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PagoResponse> obtenerTodos(){
        return service.obtenerTodos();
    }

    @GetMapping("/paciente/{id}")
    public Page<PagoResponse> porPaciente(@PathVariable Long id, Pageable pageable){
        return service.obtenerPorPaciente(id, pageable);
    }

    @GetMapping("/buscar")
    public Page<PagoResponse> buscar(@ModelAttribute PagoFilterRequest filter, Pageable pageable) {
        return service.buscar(filter, pageable);
    }

    @GetMapping("/deuda/{id}")
    public Double deuda(@PathVariable Long id){
        return service.deuda(id);
    }

    @PostMapping
    public PagoResponse crear(@Valid @RequestBody PagoRequest pago){
        return service.crear(pago);
    }
}