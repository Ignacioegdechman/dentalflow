package com.dentalflow.dentalflow.controller;

import com.dentalflow.dentalflow.dashboard.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String,Object> obtenerDashboard(){
        return service.obtenerDashboard();
    }

}