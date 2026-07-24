# Diseño de integración continua con GitHub Actions

## Objetivo

Ejecutar automáticamente la suite completa de tests del proyecto para detectar errores antes de integrar cambios en `main`.

## Activación

El workflow se ejecutará cuando:

- Se realice un `push` directo a `main`.
- Se abra o actualice un pull request dirigido a `main`.

## Entorno

GitHub Actions utilizará:

- Ubuntu como sistema operativo.
- Eclipse Temurin JDK 21.
- Caché local de dependencias Maven.
- Docker disponible en el runner para Testcontainers.

## Ejecución

El workflow ejecutará:

```text
mvn --batch-mode test