package com.mycompany.excepciones.controller;

import com.mycompany.excepciones.ReservaResumen;
import com.mycompany.excepciones.ReservaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController (ReservaService reservaService){
        this.reservaService=reservaService;
    }
        @GetMapping
        public List<ReservaResumen> obtenerReservasResumen(){
        return reservaService.obtenerResumenes();
    }

}
