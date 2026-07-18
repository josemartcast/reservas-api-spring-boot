package com.mycompany.excepciones.controller.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ReprogramarReservaRequest (
        @Positive(message = "El número de mesa debe ser mayor que 0")
        int nuevaMesa,
        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede estar en el pasado")
        LocalDate nuevaFecha) {
}
