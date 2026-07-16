package com.mycompany.excepciones;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    @Test
    void debeCrearReservaConDatosValidos() {
        // Given
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        LocalDate fecha = LocalDate.of(2026, 7, 7);

        // When
        Reserva reserva = new Reserva(
                cliente,
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        // Then
        assertEquals(cliente, reserva.getCliente());
        assertEquals(4, reserva.getNumeroMesa());
        assertEquals(6, reserva.getNumeroPersonas());
        assertEquals(fecha, reserva.getFecha());
        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstadoReserva()
        );
    }
    @Test
    void debeLanzarExcepcionSiClienteEsNull() {
        LocalDate fecha = LocalDate.of(2026, 7, 7);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Reserva(
                        null,
                        4,
                        6,
                        fecha,
                        EstadoReserva.PENDIENTE
                )
        );
    }
    @Test
    void debeLanzarExcepcionSiMesaEsInvalida() {
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        LocalDate fecha = LocalDate.of(2026, 7, 7);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Reserva(
                        cliente,
                        0,
                        6,
                        fecha,
                        EstadoReserva.PENDIENTE
                )
        );
    }
    @Test
    void debeLanzarExcepcionSiNumeroPersonasEsInvalido() {
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        LocalDate fecha = LocalDate.of(2026, 7, 7);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Reserva(
                        cliente,
                        4,
                        0,
                        fecha,
                        EstadoReserva.PENDIENTE
                )
        );
    }
    @Test
    void debeLanzarExcepcionSiFechaEsNull() {
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Reserva(
                        cliente,
                        4,
                        6,
                        null,
                        EstadoReserva.PENDIENTE
                )
        );
    }
    @Test
    void debeLanzarExcepcionSiEstadoEsNull() {
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        LocalDate fecha = LocalDate.of(2026, 7, 7);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Reserva(
                        cliente,
                        4,
                        6,
                        fecha,
                        null
                )
        );
    }
    @Test
    void debeCambiarMesaSiLaNuevaMesaEsValida() {
        // Given
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Reserva reserva = new Reserva(
                cliente,
                4,
                6,
                LocalDate.of(2026, 7, 7),
                EstadoReserva.PENDIENTE
        );

        // When
        reserva.cambiarMesa(8);

        // Then
        assertEquals(8, reserva.getNumeroMesa());
    }
    @Test
    void debeLanzarExcepcionSiNuevaMesaEsInvalida() {
        // Given
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Reserva reserva = new Reserva(
                cliente,
                4,
                6,
                LocalDate.of(2026, 7, 7),
                EstadoReserva.PENDIENTE
        );

        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> reserva.cambiarMesa(0)
        );
    }
    @Test
    void debeCambiarNumeroPersonasSiEsValido() {
        // Given
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Reserva reserva = new Reserva(
                cliente,
                4,
                6,
                LocalDate.of(2026, 7, 7),
                EstadoReserva.PENDIENTE
        );

        // When
        reserva.cambiarNumeroPersonas(10);

        // Then
        assertEquals(10, reserva.getNumeroPersonas());
    }
    @Test
    void debeLanzarExcepcionSiNuevoNumeroPersonasEsInvalido() {
        // Given
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Reserva reserva = new Reserva(
                cliente,
                4,
                6,
                LocalDate.of(2026, 7, 7),
                EstadoReserva.PENDIENTE
        );

        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> reserva.cambiarNumeroPersonas(0)
        );
    }
    @Test
    void debeConfirmarReserva() {
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Reserva reserva = new Reserva(
                cliente,
                4,
                6,
                LocalDate.of(2026, 7, 7),
                EstadoReserva.PENDIENTE
        );

        reserva.confirmar();

        assertEquals(
                EstadoReserva.CONFIRMADA,
                reserva.getEstadoReserva()
        );
    }
    @Test
    void debeCancelarReserva() {
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Reserva reserva = new Reserva(
                cliente,
                4,
                6,
                LocalDate.of(2026, 7, 7),
                EstadoReserva.PENDIENTE
        );

        reserva.cancelar();

        assertEquals(
                EstadoReserva.CANCELADA,
                reserva.getEstadoReserva()
        );
    }
    @Test
    void debePonerReservaPendiente() {
        Cliente cliente = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Reserva reserva = new Reserva(
                cliente,
                4,
                6,
                LocalDate.of(2026, 7, 7),
                EstadoReserva.CONFIRMADA
        );

        reserva.pendiente();

        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstadoReserva()
        );
    }
    @Test
    void reprogramarDebeLanzarExcepcionCuandoNuevaFechaEsNula(){
        LocalDate fecha = LocalDate.of(2027,03,26);
        Reserva reserva = new Reserva (new Cliente("7777777","jose","66666"),5,6,fecha,EstadoReserva.CONFIRMADA);
    assertThrows(IllegalArgumentException.class,()->reserva.reprogramar(7,null));
    assertEquals(5,reserva.getNumeroMesa());
    assertEquals(fecha, reserva.getFecha());

    }
}