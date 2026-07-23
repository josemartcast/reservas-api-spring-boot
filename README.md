# ReservaCollectionsDia2

API REST de gestión de reservas desarrollada con Java 21, Spring Boot, Spring Data JPA y Maven.

## Base de datos y ejecución local

La aplicación utiliza PostgreSQL 17, ejecutado mediante Docker Compose.

### Levantar PostgreSQL

```powershell
docker compose up -d
```

Comprobar el estado del contenedor:

```powershell
docker compose ps
```

### Arrancar la aplicación

```powershell
mvn spring-boot:run
```

Por defecto, la aplicación se conecta a PostgreSQL mediante:

```text
jdbc:postgresql://localhost:5432/reservas
```

La conexión puede configurarse mediante las variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

### Ejecutar los tests

```powershell
mvn test
```

Los tests utilizan una base de datos H2 en memoria y no modifican los datos de PostgreSQL.

### Migraciones

Flyway administra el esquema de PostgreSQL.

Las migraciones se encuentran en:

```text
src/main/resources/db/migration
```

Una migración que ya se ha aplicado no debe modificarse. Cualquier cambio posterior del esquema debe añadirse en una
nueva migración, por ejemplo `V3__descripcion.sql`.

La mayor parte de la suite utiliza H2 como base de datos en memoria.

La prueba `PostgreSqlFlywayIntegrationTest` utiliza Testcontainers para
verificar las migraciones de Flyway y la persistencia JPA contra una instancia
real de PostgreSQL 17.

Para ejecutar la suite completa, Docker debe estar iniciado. No es necesario
ejecutar `docker compose up`, ya que Testcontainers crea automáticamente una
base de datos temporal con un puerto aleatorio y la elimina al finalizar.
