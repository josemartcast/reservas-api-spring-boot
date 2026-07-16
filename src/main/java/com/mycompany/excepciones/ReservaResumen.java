
package com.mycompany.excepciones;

import java.time.LocalDate;

public record ReservaResumen (int numeroMesa, LocalDate fecha, String nombreCliente, int numeroPersonas, EstadoReserva estado) {
    public ReservaResumen {
    if (numeroMesa <= 0) {
        throw new IllegalArgumentException(
                "El número de mesa debe ser mayor que 0."
        );
    }

    if (fecha == null) {
        throw new IllegalArgumentException(
                "La fecha no puede ser nula."
        );
    }

    if (nombreCliente == null || nombreCliente.isBlank()) {
        throw new IllegalArgumentException(
                "El nombre del cliente no puede estar vacío."
        );
    }

    if (numeroPersonas <= 0) {
        throw new IllegalArgumentException(
                "El número de personas debe ser mayor que 0."
        );
    }

    if (estado == null) {
        throw new IllegalArgumentException(
                "El estado no puede ser nulo."
        );
    }
}
}
