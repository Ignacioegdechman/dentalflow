package com.dentalflow.dentalflow.recordatorios;

import com.dentalflow.dentalflow.dto.RecordatorioResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recordatorios")
public class RecordatorioController {

    private final RecordatorioService service;

    public RecordatorioController(RecordatorioService service) {
        this.service = service;
    }

    @GetMapping("/proximos")
    public List<RecordatorioResponse> proximos(@RequestParam(defaultValue = "24") int horas) {
        return service.proximos(horas);
    }

    @PostMapping("/turnos/{id}/email")
    public RecordatorioResponse enviarEmail(@PathVariable Long id) {
        return service.enviarEmail(id);
    }

    @PostMapping("/turnos/{id}/completo")
    public java.util.List<RecordatorioResponse> enviarCompleto(@PathVariable Long id) {
        return service.enviarRecordatoriosCompleto(id);
    }

    @PostMapping("/turnos/{id}/odontologo")
    public RecordatorioResponse enviarOdontologo(@PathVariable Long id) {
        return service.enviarEmailOdontologo(id);
    }

    @PostMapping("/turnos/{id}/whatsapp")
    public RecordatorioResponse prepararWhatsApp(@PathVariable Long id) {
        return service.prepararWhatsApp(id);
    }

    @PostMapping("/turnos/{id}/whatsapp-odontologo")
    public RecordatorioResponse enviarWhatsAppOdontologo(@PathVariable Long id) {
        return service.enviarWhatsAppOdontologo(id);
    }
}

