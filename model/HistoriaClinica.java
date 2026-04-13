package com.dentalflow.dentalflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinica extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    @NotNull
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "tratamiento_id")
    @NotNull
    private Tratamiento tratamiento;

    @ManyToOne
    @JoinColumn(name = "odontologo_id")
    @NotNull
    private Odontologo odontologo;

    @NotNull
    @PastOrPresent
    private LocalDate fecha;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String procedimiento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

}