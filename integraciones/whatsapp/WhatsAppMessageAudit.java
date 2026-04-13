package com.dentalflow.dentalflow.integraciones.whatsapp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	@Column(nullable = false, length = 40)
	private String phone;

	@Column(nullable = false, length = 3000)
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private WhatsAppDirection direction;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private WhatsAppDeliveryStatus status;

	@Column(length = 100)
	private String provider;

	@Column(length = 150)
	private String providerMessageId;

	@Column(length = 1000)
	private String error;

	@Column(length = 1000)
	private String metadata;

	@Column(nullable = false)
	private LocalDateTime createdAt;
}


