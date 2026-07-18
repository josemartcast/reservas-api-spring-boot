package com.mycompany.excepciones.controller;

import com.mycompany.excepciones.ReservaResumen;
import com.mycompany.excepciones.ReservaService;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mycompany.excepciones.Cliente;
import com.mycompany.excepciones.EstadoReserva;
import com.mycompany.excepciones.Reserva;
import com.mycompany.excepciones.controller.dto.CrearReservaRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.mycompany.excepciones.controller.dto.ReprogramarReservaRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<ReservaResumen> obtenerReservasResumen() {
        return reservaService.obtenerResumenes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResumen crearReserva(@Valid @RequestBody CrearReservaRequest request) {
        Cliente cliente = new Cliente(request.dniCliente(), request.nombreCliente(), request.telefonoCliente());
        Reserva reserva = new Reserva(cliente, request.numeroMesa(), request.numeroPersonas(), request.fecha(), EstadoReserva.PENDIENTE);
        reservaService.crearReserva(reserva);
        return reservaService.convertirAResumen(reserva);
    }

    @PatchMapping("/{mesaActual}/{fechaActual}")
    public ReservaResumen reprogramarReserva(
            @PathVariable
            @Positive(message = "El número de mesa actual debe ser mayor que 0")
            int mesaActual,
            @PathVariable LocalDate fechaActual,
            @Valid @RequestBody ReprogramarReservaRequest request
    ) {
        Reserva reserva = reservaService.reprogramarReserva(mesaActual, fechaActual, request.nuevaMesa(), request.nuevaFecha());
        return reservaService.convertirAResumen(reserva);
    }

}
