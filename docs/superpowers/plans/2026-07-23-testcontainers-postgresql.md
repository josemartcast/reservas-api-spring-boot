# PostgreSQL Testcontainers Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Verificar automáticamente que las migraciones Flyway, las entidades JPA y los repositorios funcionan contra un PostgreSQL 17 real y temporal.

**Architecture:** La suite rápida existente continuará utilizando H2. Una única clase `@SpringBootTest` levantará PostgreSQL mediante Testcontainers, conectará Spring con `@ServiceConnection`, habilitará Flyway y mantendrá Hibernate en modo `validate`.

**Tech Stack:** Java 21, Spring Boot 4.1.0, JUnit Jupiter 6, Testcontainers 2.0.5, PostgreSQL 17, Flyway, Spring Data JPA, Maven.

## Global Constraints

- Trabajar en la rama `feature/testcontainers-postgresql`.
- No modificar las migraciones `V1__crear_tabla_clientes.sql` ni `V2__crear_tabla_reservas.sql`.
- Los tests existentes deben continuar utilizando H2.
- Flyway solo se habilitará para la nueva prueba de integración con PostgreSQL.
- Hibernate debe utilizar `spring.jpa.hibernate.ddl-auto=validate` en la prueba de Testcontainers.
- La prueba no debe conectarse al PostgreSQL de `compose.yaml` ni depender del puerto 5432.
- Docker debe estar iniciado; no es necesario ejecutar `docker compose up`.
- Las versiones de Testcontainers serán administradas por el parent de Spring Boot, sin versiones explícitas en el POM.

---

## File Structure

- Modify: `pom.xml` — incorpora las dependencias de Testcontainers con alcance de test.
- Create: `src/test/resources/application-testcontainers.properties` — sobrescribe la configuración H2 únicamente cuando se activa el perfil `testcontainers`.
- Create: `src/test/java/com/mycompany/excepciones/PostgreSqlFlywayIntegrationTest.java` — comprueba migraciones y persistencia sobre PostgreSQL real.
- Modify: `README.md` — documenta que la suite completa requiere Docker por la prueba de Testcontainers.

## Preparación local

- [ ] **Step 1: Descargar la rama remota creada para el ticket**

```powershell
git fetch origin
git switch --track origin/feature/testcontainers-postgresql
```

Expected:

```text
branch 'feature/testcontainers-postgresql' set up to track 'origin/feature/testcontainers-postgresql'
Switched to a new branch 'feature/testcontainers-postgresql'
```

- [ ] **Step 2: Comprobar el punto de partida**

```powershell
git status
git log --oneline -3
```

Expected: rama `feature/testcontainers-postgresql`, árbol limpio y los commits de diseño y planificación por encima de `main`.

---

### Task 1: Arranque de PostgreSQL y ejecución de Flyway

**Files:**
- Modify: `pom.xml`
- Create: `src/test/resources/application-testcontainers.properties`
- Create: `src/test/java/com/mycompany/excepciones/PostgreSqlFlywayIntegrationTest.java`

**Interfaces:**
- Consumes: migraciones Flyway existentes en `src/main/resources/db/migration`.
- Produces: perfil de test `testcontainers` y clase `PostgreSqlFlywayIntegrationTest` conectada a un `PostgreSQLContainer`.

- [ ] **Step 1: Escribir primero la prueba de migraciones**

Crear `src/test/java/com/mycompany/excepciones/PostgreSqlFlywayIntegrationTest.java`:

```java
package com.mycompany.excepciones;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
```

- [ ] **Step 2: Ejecutar la prueba para verificar el primer fallo**

```powershell
mvn -Dtest=PostgreSqlFlywayIntegrationTest test
```

Expected: `BUILD FAILURE` durante `testCompile`, porque todavía no existen las dependencias de Testcontainers y no se pueden resolver sus imports.

- [ ] **Step 3: Añadir las dependencias administradas por Spring Boot**

Añadir dentro de `<dependencies>` en `pom.xml`:

```xml
<!-- Integración de Spring Boot con Testcontainers -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>

<!-- Contenedor PostgreSQL para pruebas de integración -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-postgresql</artifactId>
    <scope>test</scope>
</dependency>

<!-- Ciclo de vida de Testcontainers con JUnit Jupiter -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

No añadir elementos `<version>`: Spring Boot 4.1.0 administra Testcontainers 2.0.5.

- [ ] **Step 4: Crear la configuración exclusiva del perfil**

Crear `src/test/resources/application-testcontainers.properties`:

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.flyway.enabled=true
```

`@ServiceConnection` proporcionará dinámicamente la URL, el usuario y la contraseña. Estas propiedades sustituyen el driver H2, habilitan Flyway y obligan a Hibernate a validar el esquema creado por las migraciones.

- [ ] **Step 5: Ejecutar nuevamente la prueba de migraciones**

Asegurarse de que Docker Desktop está iniciado y ejecutar:

```powershell
mvn -Dtest=PostgreSqlFlywayIntegrationTest test
```

Expected:

- Testcontainers descarga o reutiliza `postgres:17-alpine`.
- Flyway registra las versiones 1 y 2.
- Hibernate valida las tablas `clientes` y `reservas`.
- `Tests run: 1, Failures: 0, Errors: 0`.
- `BUILD SUCCESS`.

- [ ] **Step 6: Revisar y guardar el primer incremento**

```powershell
git diff --check
git diff --stat
git add pom.xml src/test/resources/application-testcontainers.properties src/test/java/com/mycompany/excepciones/PostgreSqlFlywayIntegrationTest.java
git diff --cached --check
git commit -m "test: verify Flyway migrations with PostgreSQL"
```

Expected: un commit que añade las dependencias, el perfil y la primera prueba, sin errores de espacios en blanco.

---

### Task 2: Persistencia real de clientes y reservas

**Files:**
- Modify: `src/test/java/com/mycompany/excepciones/PostgreSqlFlywayIntegrationTest.java`
- Modify: `README.md`

**Interfaces:**
- Consumes: `ClienteRepository.saveAndFlush(Cliente)`, `ReservaRepository.saveAndFlush(Reserva)` y `ReservaRepository.findByNumeroMesaAndFecha(int, LocalDate)`.
- Produces: cobertura de integración de la relación `Reserva -> Cliente` y documentación del requisito de Docker.

- [ ] **Step 1: Ampliar la prueba con repositorios y persistencia**

Reemplazar el contenido de `PostgreSqlFlywayIntegrationTest.java` por:

```java
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
    void debeGuardarYRecuperarUnaReservaEnPostgreSql() {
        LocalDate fecha = LocalDate.now().plusDays(1);

        Cliente cliente = clienteRepository.saveAndFlush(
                new Cliente("77777777A", "Jose", "666666666")
        );

        Reserva reserva = new Reserva(
                cliente,
                5,
                4,
                fecha,
                EstadoReserva.PENDIENTE
        );

        reservaRepository.saveAndFlush(reserva);
        entityManager.clear();

        Optional<Reserva> resultado =
                reservaRepository.findByNumeroMesaAndFecha(5, fecha);

        assertTrue(resultado.isPresent());
        assertEquals("Jose", resultado.get().getCliente().getNombre());
        assertEquals(4, resultado.get().getNumeroPersonas());
        assertEquals(
                EstadoReserva.PENDIENTE,
                resultado.get().getEstadoReserva()
        );
    }
}
```

`entityManager.clear()` elimina las entidades de la caché del contexto. De este modo, la búsqueda final debe leer realmente desde PostgreSQL.

- [ ] **Step 2: Ejecutar solamente la clase de integración**

```powershell
mvn -Dtest=PostgreSqlFlywayIntegrationTest test
```

Expected:

- `Tests run: 2, Failures: 0, Errors: 0`.
- Hibernate ejecuta sentencias `insert` y `select`.
- `BUILD SUCCESS`.

Si falla, conservar la salida completa y diagnosticar antes de cambiar entidades o migraciones.

- [ ] **Step 3: Documentar el requisito de Docker**

En `README.md`, después del bloque que explica `mvn test`, añadir:

```markdown
La mayor parte de la suite utiliza H2 en memoria. La prueba
`PostgreSqlFlywayIntegrationTest` utiliza Testcontainers para comprobar
las migraciones Flyway y la persistencia contra PostgreSQL 17 real.

Para ejecutar la suite completa, Docker debe estar iniciado. No es necesario
ejecutar `docker compose up`: Testcontainers crea una base temporal con un
puerto aleatorio y la elimina al terminar.
```

- [ ] **Step 4: Ejecutar la suite completa**

```powershell
mvn test
```

Expected:

- Los tests existentes continúan ejecutándose con H2.
- `PostgreSqlFlywayIntegrationTest` ejecuta 2 tests con PostgreSQL.
- El total anterior de 71 tests aumenta a 73.
- `Failures: 0`.
- `Errors: 0`.
- `BUILD SUCCESS`.

- [ ] **Step 5: Revisar el alcance y crear el commit final**

```powershell
git diff --check
git diff --stat
git add README.md src/test/java/com/mycompany/excepciones/PostgreSqlFlywayIntegrationTest.java
git diff --cached --check
git diff --cached --stat
git commit -m "test: verify JPA persistence with PostgreSQL"
git status
```

Expected: segundo commit de implementación y `nothing to commit, working tree clean`.

---

## Verificación final del ticket

- [ ] Ejecutar:

```powershell
mvn test
git log --oneline --graph -5
git diff main...HEAD --check
git diff main...HEAD --stat
```

Expected:

- 73 tests, cero fallos y cero errores.
- Rama limpia.
- Un commit de diseño, un commit de planificación y dos commits de implementación por encima de `main`.
- Solo se modifican documentación, `pom.xml`, configuración de test y la nueva clase de integración.
