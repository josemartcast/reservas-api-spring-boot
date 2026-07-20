package com.mycompany.excepciones.repository;

import com.mycompany.excepciones.Cliente;
import com.mycompany.excepciones.EstadoReserva;
import com.mycompany.excepciones.Reserva;
import com.mycompany.excepciones.ReservaService;
import com.mycompany.excepciones.controller.ReservaController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void debeGuardarYBuscarReservaPorMesaYFecha() {
        Cliente cliente = new Cliente("77777777A", "Jose", "666666666");
        clienteRepository.save(cliente);
        LocalDate fecha = LocalDate.now().plusDays(1);
        Reserva reserva = new Reserva(cliente, 5, 8, fecha, EstadoReserva.PENDIENTE);
        reservaRepository.save(reserva);
        Optional<Reserva> resultado = reservaRepository.findByNumeroMesaAndFecha(5, fecha);
        assertTrue(resultado.isPresent());
        assertNotNull(resultado.get().getId());
        assertEquals(5, resultado.get().getNumeroMesa());
        assertEquals("Jose", resultado.get().getCliente().getNombre());
        assertEquals(EstadoReserva.PENDIENTE,
                resultado.get().getEstadoReserva());
    }

    @Test
    void noDebePermitirDosReservasParaMismaMesaYFecha() {
        Cliente cliente = new Cliente("77777777A", "Jose", "666666666");
        clienteRepository.save(cliente);
        LocalDate fecha = LocalDate.now().plusDays(1);
        Reserva reserva = new Reserva(cliente, 5, 8, fecha, EstadoReserva.PENDIENTE);
        Reserva reserva2 = new Reserva(cliente, 5, 8, fecha, EstadoReserva.PENDIENTE);
        reservaRepository.saveAndFlush(reserva);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> reservaRepository.saveAndFlush(reserva2)
        );
    }

    @Test
    void debeBuscarTodasLasReservasDeUnaFecha() {
        Cliente cliente = new Cliente("77777777A", "Jose", "666666666");
        clienteRepository.save(cliente);
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalDate fechaDist = LocalDate.now().plusDays(2);
        Reserva reserva1 = new Reserva(cliente, 5, 8, fecha, EstadoReserva.PENDIENTE);
        Reserva reserva2 = new Reserva(cliente, 8, 8, fecha, EstadoReserva.PENDIENTE);
        Reserva reserva3 = new Reserva(cliente, 5, 8, fechaDist, EstadoReserva.PENDIENTE);
        reservaRepository.saveAllAndFlush(List.of(reserva1, reserva2, reserva3));
        List<Reserva> resultado =
                reservaRepository.findAllByFecha(fecha);
        assertEquals(2, resultado.size());

        assertTrue(resultado.stream()
                .allMatch(reserva ->
                        reserva.getFecha().equals(fecha)));

    }

}