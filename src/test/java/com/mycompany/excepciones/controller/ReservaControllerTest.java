package com.mycompany.excepciones.controller;
import com.mycompany.excepciones.EstadoReserva;
import com.mycompany.excepciones.ReservaResumen;
import com.mycompany.excepciones.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @Test
    void debeDevolverReservas() throws Exception {
        ReservaResumen resumen = new ReservaResumen(5, LocalDate.of(2026, 3, 3),
                "Jose",5, EstadoReserva.CONFIRMADA);
        when(reservaService.obtenerResumenes()).thenReturn(List.of(resumen));

        mockMvc.perform(get("/api/reservas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].numeroMesa").value(5))
                .andExpect(jsonPath("$[0].fecha").value("2026-03-03"))
                .andExpect(jsonPath("$[0].nombreCliente").value("Jose"))
                .andExpect(jsonPath("$[0].numeroPersonas").value(5))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADA"));
    }
}
