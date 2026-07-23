package com.mycompany.excepciones;

import com.mycompany.excepciones.repository.ClienteRepository;
import com.mycompany.excepciones.repository.ReservaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ActiveProfiles("testcontainers")
class PostgreSqlFlywayIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRESQL =
            new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void debeAplicarLasMigracionesFlywayEnPostgreSql() {
        Long migracionesAplicadas = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM flyway_schema_history
                WHERE success = true
                """,
                Long.class
        );

        assertEquals(2L, migracionesAplicadas);
    }
    @Test
    void debeGuardarYRecuperarUnaReservaEnPostgreSql(){
        LocalDate fechaFutura = LocalDate.now().plusDays(2);
        Cliente cliente = new Cliente("77777777b","Jose","666666666");
        clienteRepository.saveAndFlush(cliente);
        Reserva reserva = new Reserva(cliente,5,4,fechaFutura,EstadoReserva.PENDIENTE);
        reservaRepository.saveAndFlush(reserva);
        entityManager.clear();
        Optional<Reserva> resultado =
                reservaRepository.findByNumeroMesaAndFecha(5, fechaFutura);
        assertTrue(resultado.isPresent());
        Reserva reservaRecuperada = resultado.get();
        assertEquals("Jose",reservaRecuperada.getCliente().getNombre());
        assertEquals(4,reservaRecuperada.getNumeroPersonas());
        assertEquals(EstadoReserva.PENDIENTE,reservaRecuperada.getEstadoReserva());
    }
}