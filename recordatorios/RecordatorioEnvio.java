package com.dentalflow.dentalflow.recordatorios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recordatorio_envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordatorioEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long turnoId;

    @Column(nullable = false)
    private String canal;

    private String destinatario;

    private LocalDateTime enviadoAt;
}

