package com.mycompany.excepciones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceTest {

    private ReservaService service;
    private Cliente cliente;
    private LocalDate fecha;

    @BeforeEach
    void setUp() {
        service = new ReservaService();

        cliente = crearCliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        fecha = LocalDate.of(2026, 7, 7);
    }

    @Test
    void debeCrearReserva() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        // When
        service.crearReserva(reserva);

        // Then
        assertEquals(1, service.getReservas().size());
        assertEquals(reserva, service.getReservas().get(0));
    }

    @Test
    void noDebePermitirMesaDuplicadaEnMismaFecha() {
        // Given
        Reserva reserva1 = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        Reserva reserva2 = crearReserva(
                4,
                2,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva1);

        // When / Then
        assertThrows(
                MesaOcupadaException.class,
                () -> service.crearReserva(reserva2)
        );

        assertEquals(1, service.getReservas().size());
    }

    @Test
    void debePermitirMismaMesaEnFechaDistinta() {
        // Given
        LocalDate otraFecha = LocalDate.of(2026, 7, 8);

        Reserva reserva1 = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        Reserva reserva2 = crearReserva(
                4,
                2,
                otraFecha,
                EstadoReserva.PENDIENTE
        );

        // When
        service.crearReserva(reserva1);
        service.crearReserva(reserva2);

        // Then
        assertEquals(2, service.getReservas().size());
    }

    @Test
    void debeBuscarReservaExistente() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When
        Reserva resultado = service.buscarReserva(4, fecha);

        // Then
        assertEquals(reserva, resultado);
    }

    @Test
    void debeLanzarExcepcionSiReservaNoExiste() {
        assertThrows(
                ReservaNoEncontradaException.class,
                () -> service.buscarReserva(4, fecha)
        );
    }

    @Test
    void debeDevolverOptionalConReservaSiExiste() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When
        Optional<Reserva> resultado =
                service.buscarReservaOpcional(4, fecha);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(reserva, resultado.get());
    }

    @Test
    void debeDevolverOptionalVacioSiReservaNoExiste() {
        // When
        Optional<Reserva> resultado =
                service.buscarReservaOpcional(4, fecha);

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeCancelarReservaExistente() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When
        service.cancelarReserva(4, fecha);

        // Then
        assertEquals(0, service.getReservas().size());
    }

    @Test
    void debeLanzarExcepcionAlCancelarReservaInexistente() {
        assertThrows(
                ReservaNoEncontradaException.class,
                () -> service.cancelarReserva(4, fecha)
        );
    }

    @Test
    void debeCambiarEstadoAConfirmada() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When
        service.cambiarEstadoReserva(
                4,
                fecha,
                EstadoReserva.CONFIRMADA
        );

        // Then
        assertEquals(
                EstadoReserva.CONFIRMADA,
                reserva.getEstadoReserva()
        );
    }

    @Test
    void debeCambiarEstadoACancelada() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When
        service.cambiarEstadoReserva(
                4,
                fecha,
                EstadoReserva.CANCELADA
        );

        // Then
        assertEquals(
                EstadoReserva.CANCELADA,
                reserva.getEstadoReserva()
        );
    }

    @Test
    void debeLanzarExcepcionSiNuevoEstadoEsNull() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.cambiarEstadoReserva(
                        4,
                        fecha,
                        null
                )
        );
    }

    @Test
    void debeCambiarMesaReservaSiLaNuevaMesaEstaLibre() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When
        service.cambiarMesaReserva(4, fecha, 8);

        // Then
        assertEquals(8, reserva.getNumeroMesa());
    }

    @Test
    void noDebeCambiarMesaReservaSiLaNuevaMesaEstaOcupada() {
        // Given
        Reserva reserva1 = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        Cliente otroCliente = crearCliente(
                "87654321B",
                "Luis",
                "611222333"
        );

        Reserva reserva2 = crearReserva(
                otroCliente,
                8,
                2,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva1);
        service.crearReserva(reserva2);

        // When / Then
        assertThrows(
                MesaOcupadaException.class,
                () -> service.cambiarMesaReserva(4, fecha, 8)
        );

        assertEquals(4, reserva1.getNumeroMesa());
    }

    @Test
    void debeCambiarNumeroPersonasReserva() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When
        service.cambiarNumeroPersonasReserva(4, fecha, 10);

        // Then
        assertEquals(10, reserva.getNumeroPersonas());
    }

    @Test
    void debeLanzarExcepcionSiNuevoNumeroPersonasEsInvalidoDesdeService() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.cambiarNumeroPersonasReserva(4, fecha, 0)
        );

        assertEquals(6, reserva.getNumeroPersonas());
    }

    @Test
    void debeContarReservasPorFecha() {
        // Given
        LocalDate otraFecha = LocalDate.of(2026, 7, 8);

        Cliente otroCliente = crearCliente(
                "87654321B",
                "Luis",
                "611222333"
        );

        Reserva reserva1 = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        Reserva reserva2 = crearReserva(
                otroCliente,
                8,
                2,
                fecha,
                EstadoReserva.CONFIRMADA
        );

        Reserva reserva3 = crearReserva(
                4,
                3,
                otraFecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva1);
        service.crearReserva(reserva2);
        service.crearReserva(reserva3);

        // When
        long total = service.contarReservasPorFecha(fecha);

        // Then
        assertEquals(2, total);
    }

    @Test
    void debeSumarPersonasPorFecha() {
        // Given
        LocalDate otraFecha = LocalDate.of(2026, 7, 8);

        Cliente otroCliente = crearCliente(
                "87654321B",
                "Luis",
                "611222333"
        );

        Reserva reserva1 = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        Reserva reserva2 = crearReserva(
                otroCliente,
                8,
                2,
                fecha,
                EstadoReserva.CONFIRMADA
        );

        Reserva reserva3 = crearReserva(
                4,
                3,
                otraFecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva1);
        service.crearReserva(reserva2);
        service.crearReserva(reserva3);

        // When
        int totalPersonas =
                service.calcularTotalPersonasPorFecha(fecha);

        // Then
        assertEquals(8, totalPersonas);
    }

    @Test
    void debeObtenerResumenesPorFechaYEstadoOrdenadosPorPersonasDescendente() {
        // Given
        Cliente otroCliente = crearCliente(
                "87654321B",
                "Luis",
                "611222333"
        );

        Reserva reserva1 = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.CONFIRMADA
        );

        Reserva reserva2 = crearReserva(
                otroCliente,
                8,
                10,
                fecha,
                EstadoReserva.CONFIRMADA
        );

        Reserva reserva3 = crearReserva(
                2,
                3,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva1);
        service.crearReserva(reserva2);
        service.crearReserva(reserva3);

        // When
        List<ReservaResumen> resultado =
                service.obtenerResumenesPorFechaYEstado(
                        fecha,
                        EstadoReserva.CONFIRMADA
                );

        // Then
        assertEquals(2, resultado.size());

        assertEquals(8, resultado.get(0).numeroMesa());
        assertEquals(10, resultado.get(0).numeroPersonas());

        assertEquals(4, resultado.get(1).numeroMesa());
        assertEquals(6, resultado.get(1).numeroPersonas());
    }

    @Test
    void getReservasDebeDevolverListaNoModificable() {
        // Given
        Reserva reserva = crearReserva(
                4,
                6,
                fecha,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // When / Then
        assertThrows(
                UnsupportedOperationException.class,
                () -> service.getReservas().clear()
        );

        assertEquals(1, service.getReservas().size());
    }
    @Test
    void debeReprogramarReservaCorrectamente(){
    //given
        Reserva reserva = crearReserva(4, 6, fecha, EstadoReserva.PENDIENTE);
        LocalDate nuevaFecha = fecha.plusDays(1);
        service.crearReserva(reserva);
        Reserva resultado= service.reprogramarReserva(4,fecha,8,nuevaFecha);
        //when/then
        assertFalse(service.existeReservaParaMesaYFecha(4,fecha));
        assertTrue(service.existeReservaParaMesaYFecha(8,nuevaFecha));
        assertEquals(6,reserva.getNumeroPersonas());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstadoReserva());
        assertEquals(nuevaFecha, reserva.getFecha());
        assertSame(reserva,resultado);

    }
    @Test
     void debePermitirReprogramarMismaMesaEnFechaDistinta(){
        //given
        Reserva reserva = crearReserva(4,6,fecha,EstadoReserva.PENDIENTE);
        LocalDate nuevaFecha = fecha.plusDays(1);
        service.crearReserva(reserva);

        //when
        service.reprogramarReserva(4,fecha,4,nuevaFecha);
        //then
        assertFalse(service.existeReservaParaMesaYFecha(4,fecha));
        assertTrue(service.existeReservaParaMesaYFecha(4,nuevaFecha));
        assertTrue(reserva.getFecha().equals(nuevaFecha));
        assertTrue(reserva.getNumeroMesa()==4);

    }
@Test
void debeLanzarExcepcionCuandoReservaOriginalNoExiste(){
        //given
    Reserva reserva = crearReserva(5,6,fecha, EstadoReserva.CONFIRMADA);
    LocalDate nuevaFecha = fecha.plusDays(1);
    service.crearReserva(reserva);
    assertThrows(ReservaNoEncontradaException.class,()->service.reprogramarReserva(4,fecha,8,nuevaFecha));
}
@Test
void debeLanzarExcepcionCuandoNuevaMesaEstaOcupada(){
        //given
        LocalDate nuevaFecha = fecha.plusDays(1);
        Reserva reserva1 = crearReserva(4,6,fecha,EstadoReserva.CONFIRMADA);
        Reserva reserva2 = crearReserva(8,6,nuevaFecha,EstadoReserva.CONFIRMADA);
        service.crearReserva(reserva1);
        service.crearReserva(reserva2);
        //when then
    assertThrows(MesaOcupadaException.class,()-> service.reprogramarReserva(4,fecha,8,nuevaFecha));

}
@Test
void noDebeModificarReservaCuandoLaReprogramacionFalla(){
    //given
    LocalDate nuevaFecha = fecha.plusDays(1);
    Reserva reserva1 = crearReserva(4,6,fecha,EstadoReserva.CONFIRMADA);
    Reserva reserva2 = crearReserva(8,6,nuevaFecha,EstadoReserva.CONFIRMADA);
    service.crearReserva(reserva1);
    service.crearReserva(reserva2);
    //when
    assertThrows(MesaOcupadaException.class,()-> service.reprogramarReserva(4,fecha,8,nuevaFecha));
    //then
    assertTrue(service.existeReservaParaMesaYFecha(4,fecha));
    assertTrue(service.existeReservaParaMesaYFecha(8,nuevaFecha));
    assertEquals(6,reserva1.getNumeroPersonas());
    assertEquals(6,reserva2.getNumeroPersonas());
    assertEquals(EstadoReserva.CONFIRMADA,reserva1.getEstadoReserva());
    assertEquals(EstadoReserva.CONFIRMADA,reserva2.getEstadoReserva());


}
@Test
void debeObtenerResumenesDeTodasLasReservas(){
        //given
   Reserva reserva1 = crearReserva(crearCliente("777777", "jose", "6666666"),5,6,fecha,EstadoReserva.PENDIENTE);
   Reserva reserva2 = crearReserva(crearCliente("88888","Rosa","5656560"),8,9,fecha,EstadoReserva.PENDIENTE);
   service.crearReserva(reserva1);
   service.crearReserva(reserva2);
   //when
   List<ReservaResumen> resultado = service.obtenerResumenes();
   //then
    assertEquals(2,resultado.size());
    assertEquals(5,resultado.get(0).numeroMesa());
    assertEquals(8,resultado.get(1).numeroMesa());
    assertEquals("Rosa",resultado.get(1).nombreCliente());
    assertEquals("jose",resultado.get(0).nombreCliente());
    assertEquals(6, resultado.get(0).numeroPersonas());
    assertEquals(9, resultado.get(1).numeroPersonas());
    assertEquals(EstadoReserva.PENDIENTE, resultado.get(0).estado());
    assertEquals(EstadoReserva.PENDIENTE, resultado.get(1).estado());


}

    private Cliente crearCliente(
            String dni,
            String nombre,
            String telefono
    ) {
        return new Cliente(dni, nombre, telefono);
    }

    private Reserva crearReserva(
            int numeroMesa,
            int numeroPersonas,
            LocalDate fecha,
            EstadoReserva estadoReserva
    ) {
        return new Reserva(
                cliente,
                numeroMesa,
                numeroPersonas,
                fecha,
                estadoReserva
        );
    }

    private Reserva crearReserva(
            Cliente cliente,
            int numeroMesa,
            int numeroPersonas,
            LocalDate fecha,
            EstadoReserva estadoReserva
    ) {
        return new Reserva(
                cliente,
                numeroMesa,
                numeroPersonas,
                fecha,
                estadoReserva
        );
    }
}