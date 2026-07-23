# JAB-010 — Integración de PostgreSQL y Flyway con Testcontainers

Fecha: 2026-07-23  
Estado: aprobado  
Rama: `feature/testcontainers-postgresql`

## Objetivo

Verificar que la aplicación, las entidades JPA y las migraciones Flyway funcionan contra PostgreSQL real sin depender de una base de datos instalada, de un puerto fijo ni de datos persistentes del desarrollador.

## Situación actual

- La aplicación utiliza PostgreSQL 17 en ejecución local mediante Docker Compose.
- Flyway contiene las migraciones `V1__crear_tabla_clientes.sql` y `V2__crear_tabla_reservas.sql`.
- Hibernate valida el esquema en ejecución normal.
- La suite automática utiliza H2 y tiene Flyway desactivado.
- Por tanto, la suite actual no demuestra que las migraciones sean compatibles con PostgreSQL.

## Enfoques considerados

### 1. Testcontainers en una prueba de integración dedicada

Se mantiene H2 para la suite rápida y se añade una prueba específica contra un PostgreSQL temporal.

Ventajas:

- Aislamiento completo.
- Comprueba PostgreSQL y Flyway reales.
- Impacto mínimo en el tiempo total de la suite.
- No depende del contenedor de `compose.yaml`.

Este es el enfoque seleccionado.

### 2. Testcontainers para toda la suite

Aumentaría el realismo de todos los tests, pero también su duración y el alcance del cambio. No es necesario para cubrir el riesgo actual.

### 3. PostgreSQL de Docker Compose para los tests

Obligaría a mantener manualmente una base, un puerto y un estado conocidos. Reduce el aislamiento y dificulta ejecutar los tests en otras máquinas o en CI.

## Diseño técnico

Se añadirá una clase de integración llamada `PostgreSqlFlywayIntegrationTest`.

La clase:

- Cargará el contexto completo mediante `@SpringBootTest`.
- Declarará un `PostgreSQLContainer` basado en PostgreSQL 17.
- Usará `@Testcontainers` y `@Container` para gestionar su ciclo de vida.
- Usará `@ServiceConnection` para proporcionar automáticamente a Spring la URL JDBC, el usuario y la contraseña.
- Activará Flyway.
- Mantendrá Hibernate con `ddl-auto=validate`.
- Utilizará los repositorios reales de Spring Data JPA.

El contenedor tendrá un puerto aleatorio y una base temporal. Se iniciará antes de la prueba y se eliminará al finalizar.

## Configuración

Se añadirán como dependencias de test:

- `spring-boot-testcontainers`
- módulo PostgreSQL de Testcontainers
- integración JUnit Jupiter de Testcontainers

La configuración específica de esta prueba debe sobrescribir:

- `spring.flyway.enabled=true`
- `spring.jpa.hibernate.ddl-auto=validate`

Los tests existentes seguirán usando `src/test/resources/application.properties`, H2 y Flyway desactivado.

## Casos de prueba

### Flyway aplica las migraciones

La prueba arrancará el contexto contra el PostgreSQL temporal y comprobará que:

- existe el historial de Flyway;
- se han aplicado las migraciones V1 y V2;
- Hibernate ha podido validar el esquema.

Si una migración contiene SQL incompatible o falta una tabla o columna, el contexto no arrancará y el test fallará.

### Persistencia real de una reserva

La prueba:

1. creará y guardará un cliente;
2. creará y guardará una reserva asociada;
3. forzará la escritura mediante `flush`;
4. buscará la reserva por mesa y fecha;
5. verificará cliente, número de personas y estado.

Esto demuestra conjuntamente que las migraciones, las relaciones JPA y los repositorios son compatibles con PostgreSQL.

## Aislamiento y limpieza

- Cada ejecución utilizará una base temporal nueva.
- Los datos no afectarán al PostgreSQL de desarrollo.
- No será necesario ejecutar `docker compose up`.
- Docker sí deberá estar iniciado para que Testcontainers pueda crear el contenedor.
- La prueba no dependerá del puerto 5432.

## Criterios de aceptación

- `mvn test` inicia automáticamente PostgreSQL cuando Docker está disponible.
- Flyway aplica V1 y V2.
- Hibernate valida el esquema.
- Se persiste y consulta una reserva real.
- Los tests actuales siguen funcionando con H2.
- La suite completa termina con cero fallos y cero errores.
- La prueba no usa el PostgreSQL definido en `compose.yaml`.

## Fuera de alcance

- Sustituir H2 en toda la suite.
- Cambiar las migraciones ya aplicadas.
- Añadir GitHub Actions.
- Reutilizar contenedores entre ejecuciones.
- Probar todos los endpoints HTTP contra PostgreSQL.
