package com.mycompany.excepciones.controller;

import com.mycompany.excepciones.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;


@WebMvcTest(ReservaController.class)
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @Test
    void debeDevolverReservas() throws Exception {
        ReservaResumen resumen = new ReservaResumen(5, LocalDate.of(2026, 3, 3),
                "Jose", 5, EstadoReserva.CONFIRMADA);
        when(reservaService.obtenerResumenes()).thenReturn(List.of(resumen));

        mockMvc.perform(get("/api/reservas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].numeroMesa").value(5))
                .andExpect(jsonPath("$[0].fecha").value("2026-03-03"))
                .andExpect(jsonPath("$[0].nombreCliente").value("Jose"))
                .andExpect(jsonPath("$[0].numeroPersonas").value(5))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADA"));
    }

    @Test
    void debeCrearReserva() throws Exception {
        LocalDate fechaFutura = LocalDate.now().plusDays(1);
        ReservaResumen resumen = new ReservaResumen(5, fechaFutura, "Jose", 4, EstadoReserva.PENDIENTE);
        when(reservaService.convertirAResumen(any(Reserva.class))).thenReturn(resumen);
        String json = """
                {
                  "dniCliente": "77777777A",
                  "nombreCliente": "Jose",
                  "telefonoCliente": "666666666",
                  "numeroMesa": 5,
                  "numeroPersonas": 4,
                  "fecha": "%s"
                }
                """.formatted(fechaFutura);

        mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.fecha").value(fechaFutura.toString()))
                .andExpect(jsonPath("$.numeroMesa").value(5))
                .andExpect(jsonPath("$.nombreCliente").value("Jose"))
                .andExpect(jsonPath("$.numeroPersonas").value(4))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.estado").value("PENDIENTE"));


        ArgumentCaptor<Reserva> captor =
                ArgumentCaptor.forClass(Reserva.class);

        verify(reservaService).crearReserva(captor.capture());

        Reserva reservaCapturada = captor.getValue();
        assertEquals(5, reservaCapturada.getNumeroMesa());
        assertEquals(4, reservaCapturada.getNumeroPersonas());
        assertEquals(fechaFutura, reservaCapturada.getFecha());
        assertEquals(EstadoReserva.PENDIENTE,
                reservaCapturada.getEstadoReserva());

        assertEquals("77777777A",
                reservaCapturada.getCliente().getDni());

        assertEquals("Jose",
                reservaCapturada.getCliente().getNombre());

        assertEquals("666666666",
                reservaCapturada.getCliente().getTelefono());
    }

    @Test
    void debeRechazarReservaConMesaNoValida() throws Exception {
        LocalDate fechaFutura = LocalDate.now().plusDays(1);
        String json = """
                {
                  "dniCliente": "77777777A",
                  "nombreCliente": "Jose",
                  "telefonoCliente": "666666666",
                  "numeroMesa": 0,
                  "numeroPersonas": 4,
                  "fecha": "%s"
                }
                """.formatted(fechaFutura);
        mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors.numeroMesa")
                        .value("El número de mesa debe ser mayor que 0"));
        verifyNoInteractions(reservaService);
    }

    @Test
    void debeResponderConflictCuandoLaMesaEstaOcupada() throws Exception {
        LocalDate fechaFutura = LocalDate.now().plusDays(1);
        String json = """
                {
                  "dniCliente": "77777777A",
                  "nombreCliente": "Jose",
                  "telefonoCliente": "666666666",
                  "numeroMesa": 5,
                  "numeroPersonas": 4,
                  "fecha": "%s"
                }
                """.formatted(fechaFutura);
        doThrow(new MesaOcupadaException("Mesa ocupada en esa fecha."))
                .when(reservaService)
                .crearReserva(any(Reserva.class));
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Mesa ocupada en esa fecha."));
    }

    @Test
    void debeDevolverTodosLosErroresDeValidacion() throws Exception {
        LocalDate fechaFutura = LocalDate.now().plusDays(1);
        String json = """
                {
                  "dniCliente": "77777777A",
                  "nombreCliente": "",
                  "telefonoCliente": "666666666",
                  "numeroMesa": 0,
                  "numeroPersonas": 0,
                  "fecha": "%s"
                }
                """.formatted(fechaFutura);
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors.nombreCliente")
                        .value("El nombre del cliente es obligatorio"))
                .andExpect(jsonPath("$.fieldErrors.numeroMesa")
                        .value("El número de mesa debe ser mayor que 0"))
                .andExpect(jsonPath("$.fieldErrors.numeroPersonas")
                        .value("El número de personas debe ser mayor que 0"));
        verify(reservaService, never())
                .crearReserva(any(Reserva.class));
    }

    @Test
    void debeReprogramarReservaCorrectamente() throws Exception {
        LocalDate fechaActual = LocalDate.now();
        LocalDate nuevaFecha = LocalDate.now().plusDays(1);
        Cliente cliente = new Cliente("77777777A", "Jose", "666666666");
        Reserva reservaReprogramada = new Reserva(cliente, 6, 8, fechaActual, EstadoReserva.PENDIENTE);
        ReservaResumen resumen = new ReservaResumen(
                8,
                nuevaFecha,
                "Jose",
                6,
                EstadoReserva.PENDIENTE
        );
        String json = """
                {
                  "nuevaMesa": 8,
                  "nuevaFecha": "%s"
                }
                """.formatted(nuevaFecha);
        when(reservaService.reprogramarReserva(
                4,
                fechaActual,
                8,
                nuevaFecha
        )).thenReturn(reservaReprogramada);

        when(reservaService.convertirAResumen(reservaReprogramada))
                .thenReturn(resumen);
        mockMvc.perform(patch(
                        "/api/reservas/{mesaActual}/{fechaActual}",
                        4,
                        fechaActual
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroMesa").value(8))
                .andExpect(jsonPath("$.fecha").value(nuevaFecha.toString()))
                .andExpect(jsonPath("$.nombreCliente").value("Jose"))
                .andExpect(jsonPath("$.numeroPersonas").value(6));
    }

    @Test
    void debeRechazarReprogramacionConNuevaMesaNoValida() throws Exception {
        LocalDate fechaActual = LocalDate.now();
        LocalDate nuevaFecha = LocalDate.now().plusDays(1);
        String json = """
                {
                  "nuevaMesa": 0,
                  "nuevaFecha": "%s"
                }
                """.formatted(nuevaFecha);
        mockMvc.perform(patch(
                        "/api/reservas/{mesaActual}/{fechaActual}",
                        4,
                        fechaActual
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors.nuevaMesa").value("El número de mesa debe ser mayor que 0"));
        verifyNoInteractions(reservaService);
    }

    @Test
    void debeResponderNotFoundCuandoLaReservaNoExiste() throws Exception {
        LocalDate fechaActual = LocalDate.now();
        LocalDate nuevaFecha = LocalDate.now().plusDays(1);
        String json = """
                {
                  "nuevaMesa": 8,
                  "nuevaFecha": "%s"
                }
                """.formatted(nuevaFecha);
        when(reservaService.reprogramarReserva(
                4,
                fechaActual,
                8,
                nuevaFecha
        )).thenThrow(
                new ReservaNoEncontradaException("Reserva no encontrada.")
        );
        mockMvc.perform(patch(
                        "/api/reservas/{mesaActual}/{fechaActual}",
                        4,
                        fechaActual
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Reserva no encontrada."));

    }

    @Test
    void debeResponderConflictCuandoLaNuevaMesaEstaOcupada() throws Exception {
        LocalDate fechaActual = LocalDate.now();
        LocalDate nuevaFecha = LocalDate.now().plusDays(1);
        String json = """
                {
                  "nuevaMesa": 8,
                  "nuevaFecha": "%s"
                }
                """.formatted(nuevaFecha);
        when(reservaService.reprogramarReserva(
                4,
                fechaActual,
                8,
                nuevaFecha
        )).thenThrow(
                new MesaOcupadaException("Mesa ocupada en esa fecha.")
        );
        mockMvc.perform(patch(
                        "/api/reservas/{mesaActual}/{fechaActual}",
                        4,
                        fechaActual
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Mesa ocupada en esa fecha."));

    }


}
