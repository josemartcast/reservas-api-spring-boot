package com.mycompany.excepciones.repository;

import com.mycompany.excepciones.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ClienteRepositoryTest {
    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void debeGuardarYBuscarClientePorDni() {
        // Given: crear Cliente
        Cliente cliente = new Cliente("77777777A", "Jose", "666666666");
        // When: clienteRepository.save(cliente)
        clienteRepository.save(cliente);
        // y clienteRepository.findByDni(...)
        Optional<Cliente> resultado = clienteRepository.findByDni("77777777A");
        // Then:
        // comprobar que existe
        assertTrue(resultado.isPresent());
        // comprobar nombre
        assertEquals("Jose",resultado.get().getNombre());
        // comprobar que Hibernate le asignó un id
        assertNotNull(resultado.get().getId());
    }
}
