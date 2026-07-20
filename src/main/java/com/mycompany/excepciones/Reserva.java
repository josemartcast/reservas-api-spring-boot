package com.mycompany.excepciones;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(
        name = "reservas",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reserva_mesa_fecha",
                columnNames = {"numero_mesa", "fecha"}
        )
)
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    @Column(name = "numero_mesa", nullable = false)
    private int numeroMesa;
    @Column(name = "numero_personas", nullable = false)
    private int numeroPersonas;
    @Column(nullable = false)
    private  LocalDate fecha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
    protected Reserva() {
    }
    public Long getId() {
        return id;
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
    public void asignarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no puede ser nulo."
            );
        }

        this.cliente = cliente;
    }
}
