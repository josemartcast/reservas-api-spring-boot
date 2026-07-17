package com.mycompany.excepciones.controller;

import com.mycompany.excepciones.ReservaResumen;
import com.mycompany.excepciones.ReservaService;
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

}
