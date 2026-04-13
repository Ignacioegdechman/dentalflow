package com.dentalflow.dentalflow.model;

import com.dentalflow.dentalflow.enums.EstadoPago;
import com.dentalflow.dentalflow.enums.MetodoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Pago extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Paciente paciente;

    @NotNull
    @PositiveOrZero
    private Double monto;

    @Enumerated(EnumType.STRING)
    @NotNull
    private MetodoPago metodo;

    @NotNull
    @FutureOrPresent
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EstadoPago estado;
}