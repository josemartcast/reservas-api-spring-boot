package com.mycompany.excepciones;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void debeCrearClienteConDatosValidos() {
        // Given
        String dni = "12345678A";
        String nombre = "Ana";
        String telefono = "600123123";

        // When
        Cliente cliente = new Cliente(dni, nombre, telefono);

        // Then
        assertEquals(dni, cliente.getDni());
        assertEquals(nombre, cliente.getNombre());
        assertEquals(telefono, cliente.getTelefono());
    }

    @Test
    void debeLanzaExcepcionSiDniEsNull() {
        String dni = null;
        String nombre = "Ana";
        String telefono = "600123123";
        assertThrows(IllegalArgumentException.class, () -> new Cliente(dni, nombre, telefono));
    }

    @Test
    void debeLanzarExcepcionSiDniEstaVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Cliente("", "Ana", "600123123")
        );
    }

    @Test
    void debeLanzarExcepcionSiNombreEstaVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Cliente("12345678A", "", "600123123")
        );
    }

    @Test
    void debeLanzarExcepcionSiTelefonoEstaVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Cliente("12345678A", "Ana", "")
        );
    }
    @Test
    void dosClientesConMismoDniDebenSerIguales() {
        // Given
        Cliente cliente1 = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Cliente cliente2 = new Cliente(
                "12345678A",
                "Luis",
                "611222333"
        );

        // When / Then
        assertEquals(cliente1, cliente2);
    }
    @Test
    void dosClientesConDistintoDniNoDebenSerIguales() {
        // Given
        Cliente cliente1 = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Cliente cliente2 = new Cliente(
                "87654321B",
                "Ana",
                "600123123"
        );

        // When / Then
        assertNotEquals(cliente1, cliente2);
    }
    @Test
    void clientesConMismoDniDebenTenerMismoHashCode() {
        // Given
        Cliente cliente1 = new Cliente(
                "12345678A",
                "Ana",
                "600123123"
        );

        Cliente cliente2 = new Cliente(
                "12345678A",
                "Luis",
                "611222333"
        );

        // When / Then
        assertEquals(cliente1.hashCode(), cliente2.hashCode());
    }

}
