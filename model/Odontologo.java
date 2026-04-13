package com.dentalflow.dentalflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Odontologo extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @NotBlank
    @Size(max = 120)
    private String especialidad;

    @NotBlank
    @Size(max = 30)
    private String telefono;

    @Email
    @Size(max = 150)
    private String email;

}