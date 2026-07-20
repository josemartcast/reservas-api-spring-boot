package com.mycompany.excepciones.repository;

import com.mycompany.excepciones.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository <Reserva, Long>{
    boolean existsByNumeroMesaAndFecha(int numeroMesa, LocalDate fecha);
    @EntityGraph(attributePaths = "cliente")
    Optional <Reserva> findByNumeroMesaAndFecha(int numeroMesa,LocalDate fecha);
    List<Reserva>findAllByFecha(LocalDate fecha);
}
