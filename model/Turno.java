package com.dentalflow.dentalflow.model;

import com.dentalflow.dentalflow.enums.EstadoTurno;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Turno extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Paciente paciente;

    @ManyToOne
    @NotNull
    private Odontologo odontologo;

    @ManyToOne
    @NotNull
    private Tratamiento tratamiento;

    @Future
    @NotNull
    private LocalDateTime fechaHoraInicio;
    @NotNull
    private LocalDateTime fechaHoraFin;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EstadoTurno estado;
}