package com.dentalflow.dentalflow.integraciones.whatsapp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_message_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessageAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private WhatsAppDirection direction;

    @Enumerated(EnumType.STRING)
    private WhatsAppDeliveryStatus status;

    private String action;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String rawPayload;

    private String providerMessageId;

    @Column(columnDefinition = "TEXT")
    private String error;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

