package com.mycompany.excepciones.controller.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CrearReservaRequest(
        @NotBlank(message = "El DNI del cliente es obligatorio")
        String dniCliente,
        @NotBlank(message = "El nombre del cliente es obligatorio")
        String nombreCliente,
        @NotBlank(message = "El teléfono del cliente es obligatorio")
        String telefonoCliente,
        @Positive(message = "El número de mesa debe ser mayor que 0")
        int numeroMesa,
        @Positive(message = "El número de personas debe ser mayor que 0")
        int numeroPersonas,
        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede estar en el pasado")
        LocalDate fecha) {

}
