package com.dentalflow.dentalflow.controller;

import com.dentalflow.dentalflow.dto.OdontologoRequest;
import com.dentalflow.dentalflow.dto.OdontologoResponse;
import com.dentalflow.dentalflow.service.OdontologoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/odontologos")
public class OdontologoController {

    private final OdontologoService service;

    public OdontologoController(OdontologoService service) {
        this.service = service;
    }

    @GetMapping
    public List<OdontologoResponse> obtenerTodos() {
        return service.obtenerTodos();
    }

    @PostMapping
    public OdontologoResponse crear(@Valid @RequestBody OdontologoRequest odontologo) {
        return service.crear(odontologo);
    }

}