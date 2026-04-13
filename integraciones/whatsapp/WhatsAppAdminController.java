package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/integraciones/whatsapp")
public class WhatsAppAdminController {

    private final WhatsAppConversationService conversationService;

    public WhatsAppAdminController(WhatsAppConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/auditoria")
    public Page<WhatsAppAuditResponse> auditoria(@RequestParam(required = false) String telefono,
                                                 @RequestParam(required = false) WhatsAppDirection direction,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                                 Pageable pageable) {
        return conversationService.auditoria(telefono, direction, desde, hasta, pageable);
    }
}

