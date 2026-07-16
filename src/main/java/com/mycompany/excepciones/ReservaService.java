package com.mycompany.excepciones;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
@Service
public class ReservaService {

    private final List<Reserva> reservas = new ArrayList<>();

    public void crearReserva(Reserva nuevaReserva) {
        validarReserva(nuevaReserva);

        boolean mesaOcupada = reservas.stream()
                .anyMatch(reserva
                        -> reserva.getNumeroMesa() == nuevaReserva.getNumeroMesa()
                && reserva.getFecha().equals(nuevaReserva.getFecha())
                );

        if (mesaOcupada) {
            throw new MesaOcupadaException(
                    "La mesa " + nuevaReserva.getNumeroMesa()
                    + " ya está ocupada para la fecha "
                    + nuevaReserva.getFecha()
            );
        }

        reservas.add(nuevaReserva);
    }

    public void cancelarReserva(int numeroMesa, LocalDate fecha) {
        Reserva reserva = buscarReserva(numeroMesa, fecha);
        reservas.remove(reserva);
    }

    public List<Reserva> buscarReservasPorFecha(LocalDate fecha) {
        validarFecha(fecha);

        return reservas.stream()
                .filter(reserva -> reserva.getFecha().equals(fecha))
                .toList();
    }

    public int calcularTotalPersonasPorFecha(LocalDate fecha) {
        validarFecha(fecha);

        return reservas.stream()
                .filter(reserva -> reserva.getFecha().equals(fecha))
                .mapToInt(Reserva::getNumeroPersonas)
                .sum();
    }

    public long contarReservasPorFecha(LocalDate fecha) {
        validarFecha(fecha);

        return reservas.stream()
                .filter(reserva -> reserva.getFecha().equals(fecha))
                .count();
    }

    public Optional<Reserva> buscarReservaOpcional(int numeroMesa, LocalDate fecha) {
        validarNumeroMesa(numeroMesa);
        validarFecha(fecha);

        return reservas.stream()
                .filter(reserva
                        -> reserva.getNumeroMesa() == numeroMesa
                && reserva.getFecha().equals(fecha)
                )
                .findFirst();
    }

    public Reserva buscarReserva(int numeroMesa, LocalDate fecha) {
        return buscarReservaOpcional(numeroMesa, fecha)
                .orElseThrow(() -> new ReservaNoEncontradaException(
                "No existe una reserva para la mesa "
                + numeroMesa
                + " en la fecha "
                + fecha
        ));
    }

    public List<Reserva> buscarReservasDeCliente(String dni) {
        validarDni(dni);

        return reservas.stream()
                .filter(reserva -> reserva.getCliente().getDni().equals(dni))
                .toList();
    }

    public int calcularTotalPersonasReservadasPorCliente(String dni) {
        validarDni(dni);

        return reservas.stream()
                .filter(reserva -> reserva.getCliente().getDni().equals(dni))
                .mapToInt(Reserva::getNumeroPersonas)
                .sum();
    }

    public Reserva buscarReservaConMasPersonas() {
        return reservas.stream()
                .max(Comparator.comparingInt(Reserva::getNumeroPersonas))
                .orElseThrow(() -> new ReservaNoEncontradaException(
                "No hay reservas registradas."
        ));
    }

    public boolean existeReservaParaMesaYFecha(int numeroMesa, LocalDate fecha) {
        validarNumeroMesa(numeroMesa);
        validarFecha(fecha);

        return reservas.stream()
                .anyMatch(reserva
                        -> reserva.getNumeroMesa() == numeroMesa
                && reserva.getFecha().equals(fecha)
                );
    }

    public boolean todasLasReservasTienenPersonas() {
        return reservas.stream()
                .allMatch(reserva -> reserva.getNumeroPersonas() > 0);
    }

    public boolean ningunaReservaParaMesa(int numeroMesa) {
        validarNumeroMesa(numeroMesa);

        return reservas.stream()
                .noneMatch(reserva -> reserva.getNumeroMesa() == numeroMesa);
    }

    public List<Reserva> ordenarReservasPorNumeroPersonasDescendente() {
        return reservas.stream()
                .sorted(Comparator.comparingInt(Reserva::getNumeroPersonas).reversed())
                .toList();
    }

    public List<Reserva> ordenarReservasPorFechaYMesa() {
        return reservas.stream()
                .sorted(Comparator.comparing(Reserva::getFecha)
                        .thenComparingInt(Reserva::getNumeroMesa))
                .toList();
    }

    public List<Reserva> ordenarPorFechaYPersonasDescendente() {
        return reservas.stream()
                .sorted(Comparator.comparing(Reserva::getFecha)
                        .thenComparing(
                                Comparator.comparingInt(Reserva::getNumeroPersonas)
                                        .reversed()
                        ))
                .toList();
    }

    public List<Reserva> ordenarPorPersonasDescendenteYMesaAscendente() {
        return reservas.stream()
                .sorted(Comparator.comparingInt(Reserva::getNumeroPersonas)
                        .reversed()
                        .thenComparingInt(Reserva::getNumeroMesa))
                .toList();
    }

    public List<Integer> obtenerNumerosDeMesa() {
        return reservas.stream()
                .map(Reserva::getNumeroMesa)
                .toList();
    }

    public List<String> obtenerNombresClientesConReservasGrandes() {
        return reservas.stream()
                .filter(reserva -> reserva.getNumeroPersonas() > 4)
                .map(reserva -> reserva.getCliente().getNombre())
                .toList();
    }

    public List<String> obtenerNombresClientesSinRepetir() {
        return reservas.stream()
                .map(reserva -> reserva.getCliente().getNombre())
                .distinct()
                .toList();
    }

    public List<String> obtenerNombresClientesDeReservasGrandesOrdenados() {
        return reservas.stream()
                .filter(reserva -> reserva.getNumeroPersonas() > 4)
                .map(reserva -> reserva.getCliente().getNombre())
                .distinct()
                .sorted()
                .toList();
    }

    public List<Reserva> obtenerTresReservasConMasPersonas() {
        return reservas.stream()
                .sorted(Comparator.comparingInt(Reserva::getNumeroPersonas).reversed())
                .limit(3)
                .toList();
    }

    public List<Reserva> obtenerReservasSaltandoLasDosPrimeras() {
        return reservas.stream()
                .sorted(Comparator.comparingInt(Reserva::getNumeroPersonas).reversed())
                .skip(2)
                .toList();
    }

    public List<Reserva> obtenerSegundaPaginaDeTresReservas() {
        return reservas.stream()
                .sorted(Comparator.comparingInt(Reserva::getNumeroPersonas).reversed())
                .skip(3)
                .limit(3)
                .toList();
    }

    public Map<LocalDate, List<Reserva>> agruparReservasPorFecha() {
        return reservas.stream()
                .collect(Collectors.groupingBy(Reserva::getFecha));
    }

    public Map<LocalDate, Long> contarReservasAgrupadasPorFecha() {
        return reservas.stream()
                .collect(Collectors.groupingBy(
                        Reserva::getFecha,
                        Collectors.counting()
                ));
    }

    public Map<LocalDate, Integer> sumarPersonasPorFecha() {
        return reservas.stream()
                .collect(Collectors.groupingBy(
                        Reserva::getFecha,
                        Collectors.summingInt(Reserva::getNumeroPersonas)
                ));
    }

    public Map<String, List<Reserva>> agruparReservasPorDniCliente() {
        return reservas.stream()
                .collect(Collectors.groupingBy(
                        reserva -> reserva.getCliente().getDni()
                ));
    }

    public Map<String, Long> contarReservasPorDniCliente() {
        return reservas.stream()
                .collect(Collectors.groupingBy(
                        reserva -> reserva.getCliente().getDni(),
                        Collectors.counting()
                ));
    }

    public Map<String, Integer> sumarPersonasPorDniCliente() {
        return reservas.stream()
                .collect(Collectors.groupingBy(
                        reserva -> reserva.getCliente().getDni(),
                        Collectors.summingInt(Reserva::getNumeroPersonas)
                ));
    }

    public List<Reserva> buscarReservasPorEstado(EstadoReserva estadoReserva) {
        validarEstado(estadoReserva);

        return reservas.stream()
                .filter(reserva -> reserva.getEstadoReserva() == estadoReserva)
                .toList();
    }

    public long contarReservasPorEstado(EstadoReserva estadoReserva) {
        validarEstado(estadoReserva);

        return reservas.stream()
                .filter(reserva -> reserva.getEstadoReserva() == estadoReserva)
                .count();
    }

    public void cambiarEstadoReserva(
            int numeroMesa,
            LocalDate fecha,
            EstadoReserva nuevoEstado
    ) {
        validarEstado(nuevoEstado);

        Reserva reserva = buscarReserva(numeroMesa, fecha);

        switch (nuevoEstado) {
            case PENDIENTE ->
                reserva.pendiente();
            case CONFIRMADA ->
                reserva.confirmar();
            case CANCELADA ->
                reserva.cancelar();
        }
    }

    public void marcarReservaComoCancelada(int numeroMesa, LocalDate fecha) {
        cambiarEstadoReserva(numeroMesa, fecha, EstadoReserva.CANCELADA);
    }

    public Map<EstadoReserva, List<Reserva>> agruparReservasPorEstado() {
        return reservas.stream()
                .collect(Collectors.groupingBy(Reserva::getEstadoReserva));
    }

    public Map<EstadoReserva, Long> contarReservasAgrupadasPorEstado() {
        return reservas.stream()
                .collect(Collectors.groupingBy(
                        Reserva::getEstadoReserva,
                        Collectors.counting()
                ));
    }

    public List<Reserva> obtenerReservasConfirmadasPorFecha(LocalDate fecha) {
        validarFecha(fecha);

        return reservas.stream()
                .filter(reserva -> reserva.getFecha().equals(fecha))
                .filter(reserva -> reserva.getEstadoReserva() == EstadoReserva.CONFIRMADA)
                .sorted(Comparator.comparingInt(Reserva::getNumeroPersonas).reversed())
                .toList();
    }

    public Optional<Reserva> buscarPrimeraReservaConfirmadaPorFecha(LocalDate fecha) {
        validarFecha(fecha);

        return reservas.stream()
                .filter(reserva
                        -> reserva.getFecha().equals(fecha)
                && reserva.getEstadoReserva() == EstadoReserva.CONFIRMADA
                )
                .findFirst();
    }

    public Optional<String> buscarNombreClienteDeReserva(
            int numeroMesa,
            LocalDate fecha
    ) {
        return buscarReservaOpcional(numeroMesa, fecha)
                .map(reserva -> reserva.getCliente().getNombre());
    }

    public String buscarNombreClienteOTexto(int numeroMesa, LocalDate fecha) {
        return buscarNombreClienteDeReserva(numeroMesa, fecha)
                .orElse("Cliente no encontrado");
    }

    public Optional<String> buscarNombrePrimerClienteConfirmadoPorFecha(LocalDate fecha) {
        return buscarPrimeraReservaConfirmadaPorFecha(fecha)
                .map(reserva -> reserva.getCliente().getNombre());
    }

    public ReservaResumen convertirAResumen(Reserva reserva) {
        validarReserva(reserva);

        return new ReservaResumen(
                reserva.getNumeroMesa(),
                reserva.getFecha(),
                reserva.getCliente().getNombre(),
                reserva.getNumeroPersonas(),
                reserva.getEstadoReserva()
        );
    }

    public List<ReservaResumen> obtenerTodosLosResumenes() {
        return reservas.stream()
                .map(this::convertirAResumen)
                .toList();
    }

    public List<ReservaResumen> obtenerResumenesConfirmadosOrdenados() {
        return reservas.stream()
                .filter(reserva -> reserva.getEstadoReserva() == EstadoReserva.CONFIRMADA)
                .sorted(Comparator.comparingInt(Reserva::getNumeroPersonas).reversed())
                .map(this::convertirAResumen)
                .toList();
    }

    public List<Reserva> getReservas() {
        return List.copyOf(reservas);
    }

    private void validarReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula.");
        }
    }

    private void validarNumeroMesa(int numeroMesa) {
        if (numeroMesa <= 0) {
            throw new IllegalArgumentException("El número de mesa debe ser mayor que 0.");
        }
    }

    private void validarFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }
    }

    private void validarDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío.");
        }
    }

    private void validarEstado(EstadoReserva estadoReserva) {
        if (estadoReserva == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
    }

    private void validarNumeroPersonas(int numeroPersonas) {
        if (numeroPersonas <= 0) {
            throw new IllegalArgumentException("El numero de personas tiene que ser superior a 0");
        }
    }

    public void cambiarMesaReserva(
            int numeroMesaActual,
            LocalDate fecha,
            int nuevaMesa
    ) {
        validarNumeroMesa(numeroMesaActual);
        validarFecha(fecha);
        validarNumeroMesa(nuevaMesa);

        if (existeReservaParaMesaYFecha(nuevaMesa, fecha)) {
            throw new MesaOcupadaException(
                    "La mesa " + nuevaMesa
                    + " ya está ocupada para la fecha "
                    + fecha
            );
        }

        Reserva reserva = buscarReserva(numeroMesaActual, fecha);
        reserva.cambiarMesa(nuevaMesa);
    }

    public void cambiarNumeroPersonasReserva(
            int numeroMesa,
            LocalDate fecha,
            int nuevoNumeroPersonas
    ) {
        validarNumeroMesa(numeroMesa);
        validarFecha(fecha);
        validarNumeroPersonas(nuevoNumeroPersonas);
        buscarReserva(numeroMesa, fecha).cambiarNumeroPersonas(nuevoNumeroPersonas);
    }

    public List<ReservaResumen> obtenerResumenesPorFechaYEstado(
            LocalDate fecha,
            EstadoReserva estadoReserva
    ) {
        validarFecha(fecha);
        validarEstado(estadoReserva);

        return reservas.stream()
                .filter(reserva
                        -> reserva.getFecha().equals(fecha)
                && reserva.getEstadoReserva() == estadoReserva
                )
                .sorted(
                        Comparator.comparingInt(
                                Reserva::getNumeroPersonas
                        ).reversed()
                )
                .map(this::convertirAResumen)
                .toList();
    }

    public void reprogramarReserva(int mesaActual, LocalDate fechaActual, int nuevaMesa, LocalDate nuevaFecha){

        //validaciones
        validarFecha(fechaActual);
        validarFecha(nuevaFecha);
        validarNumeroMesa(nuevaMesa);
        validarNumeroMesa(mesaActual);
        //comprobaciones de mesas para fecha acutal y nueva
        if(!existeReservaParaMesaYFecha(mesaActual,fechaActual)){
            throw new ReservaNoEncontradaException("Reserva no encontrada.");
        }
        if(existeReservaParaMesaYFecha(nuevaMesa,nuevaFecha)){
            throw new MesaOcupadaException("Mesa ocupada en esa fecha.");
        }
        //cogemos la reserva y le cambiamos los valores.
        Reserva reserva = buscarReserva(mesaActual,fechaActual);
        reserva.reprogramar(nuevaMesa,nuevaFecha);
    }
    public List<ReservaResumen> obtenerResumenes(){
        List<ReservaResumen> reservasResumen = new ArrayList<>();
        for(Reserva reserva : reservas){
           reservasResumen.add(convertirAResumen(reserva));
        }
        return List.copyOf(reservasResumen);
    }
}
