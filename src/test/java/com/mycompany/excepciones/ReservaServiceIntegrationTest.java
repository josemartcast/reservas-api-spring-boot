package com.mycompany.excepciones;

import com.mycompany.excepciones.repository.ClienteRepository;
import com.mycompany.excepciones.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservaServiceIntegrationTest {

    @Autowired
    private ReservaService service;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void limpiarBaseDeDatos() {
        reservaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void debePersistirLaReprogramacionAlFinalizarLaTransaccion() {
        // given
        LocalDate fechaActual = LocalDate.now().plusDays(1);
        LocalDate nuevaFecha = fechaActual.plusDays(1);

        Cliente cliente = new Cliente(
                "77777777A",
                "Jose",
                "666666666"
        );

        Reserva reserva = new Reserva(
                cliente,
                4,
                6,
                fechaActual,
                EstadoReserva.PENDIENTE
        );

        service.crearReserva(reserva);

        // when
        service.reprogramarReserva(
                4,
                fechaActual,
                8,
                nuevaFecha
        );

        // then
        assertFalse(
                reservaRepository.existsByNumeroMesaAndFecha(
                        4,
                        fechaActual
                )
        );

        Reserva resultado = reservaRepository
                .findByNumeroMesaAndFecha(8, nuevaFecha)
                .orElseThrow();

        assertEquals(8, resultado.getNumeroMesa());
        assertEquals(nuevaFecha, resultado.getFecha());
    }
}