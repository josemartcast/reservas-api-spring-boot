package com.mycompany.excepciones;

import java.time.LocalDate;

public class Reserva {

    private final Cliente cliente;
    private int numeroMesa;
    private int numeroPersonas;
    private  LocalDate fecha;
    private EstadoReserva estadoReserva;

    public Reserva(Cliente cliente, int numeroMesa, int numeroPersonas, LocalDate fecha, EstadoReserva estadoReserva) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no puede ser null");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("Fecha no puede ser null");
        }
        if (numeroMesa <= 0) {
            throw new IllegalArgumentException("El número de mesa debe ser mayor que 0.");
        }

        if (numeroPersonas <= 0) {
            throw new IllegalArgumentException("El número de personas debe ser mayor que 0.");
        }

        if (estadoReserva == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estadoReserva = estadoReserva;
        this.cliente = cliente;
        this.fecha = fecha;
        this.numeroPersonas = numeroPersonas;
        this.numeroMesa = numeroMesa;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    public LocalDate getFecha() {
        return fecha;
    }
    public void cambiarMesa(int nuevaMesa) {
        if (nuevaMesa <= 0) {
            throw new IllegalArgumentException(
                    "El número de mesa debe ser mayor que 0."
            );
        }

        this.numeroMesa = nuevaMesa;
    }
    public void cambiarNumeroPersonas(int nuevoNumeroPersonas) {
        if (nuevoNumeroPersonas <= 0) {
            throw new IllegalArgumentException(
                    "El número de personas debe ser mayor que 0."
            );
        }

        this.numeroPersonas = nuevoNumeroPersonas;
    }
public void confirmar(){
    this.estadoReserva=EstadoReserva.CONFIRMADA;
}
public void cancelar(){
    this.estadoReserva=EstadoReserva.CANCELADA;
}
public void pendiente(){
    this.estadoReserva=EstadoReserva.PENDIENTE;
}
public void cambiarFecha(LocalDate fecha){
        if(fecha == null){
            throw new IllegalArgumentException("Fecha no debe ser nula.");
        }
        this.fecha=fecha;
}
    public void reprogramar(int nuevaMesa, LocalDate nuevaFecha){
        if(nuevaFecha == null){
            throw new IllegalArgumentException("La reserva no puede se nula.");
        }
        if(nuevaMesa <=0){
            throw new IllegalArgumentException("El numero de mesa debe ser superior a 0.");
        }
        this.fecha=nuevaFecha;
        this.numeroMesa=nuevaMesa;
    }
}
