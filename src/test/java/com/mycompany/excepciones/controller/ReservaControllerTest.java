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
}
