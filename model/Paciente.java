package com.dentalflow.dentalflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Paciente extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false)
    private String nombre;

    @Size(max = 30)
    private String telefono;

    @Email
    @Size(max = 150)
    private String email;

    @Past
    private LocalDate fechaNacimiento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    private boolean activo = true;
}