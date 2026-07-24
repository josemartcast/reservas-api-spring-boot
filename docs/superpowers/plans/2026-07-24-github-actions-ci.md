# GitHub Actions CI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ejecutar automáticamente la suite completa de Maven en cada pull request hacia `main` y en cada push directo a `main`.

**Architecture:** Un único workflow de GitHub Actions preparará un runner Ubuntu, descargará el repositorio, instalará Eclipse Temurin JDK 21 con caché de Maven y ejecutará `mvn --batch-mode test`. Testcontainers utilizará el Docker ya disponible en el runner para levantar temporalmente PostgreSQL 17.

**Tech Stack:** GitHub Actions, Ubuntu, Eclipse Temurin JDK 21, Maven, JUnit 5, Docker y Testcontainers con PostgreSQL 17.

## Global Constraints

- El workflow debe activarse únicamente en `push` a `main` y `pull_request` hacia `main`.
- La verificación debe ejecutar `mvn --batch-mode test`.
- Debe usarse Java 21 con distribución Eclipse Temurin.
- Debe habilitarse la caché de dependencias Maven.
- PostgreSQL no se configurará como servicio de GitHub Actions; Testcontainers administrará el contenedor.
- No se añadirán matrices, cobertura, análisis estático, despliegue ni publicación del JAR.

---

### Task 1: Crear y verificar el workflow de integración continua

**Files:**
- Create: `.github/workflows/ci.yml`
- Reference: `pom.xml`
- Reference: `src/test/java/com/mycompany/excepciones/PostgreSqlFlywayIntegrationTest.java`

**Interfaces:**
- Consumes: el proyecto Maven definido en `pom.xml` y la suite existente de JUnit/Testcontainers.
- Produces: el trabajo `test` del workflow `CI`, visible como comprobación en GitHub.

- [ ] **Step 1: Confirmar la línea base local**

Run:

```powershell
mvn --batch-mode test
```

Expected: `BUILD SUCCESS` y la suite completa sin fallos ni errores. Docker Desktop debe estar iniciado para la prueba con Testcontainers.

- [ ] **Step 2: Crear el workflow mínimo**

Crear `.github/workflows/ci.yml` con este contenido:

```yaml
name: CI

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

permissions:
  contents: read

jobs:
  test:
    name: Maven tests
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v6

      - name: Set up JDK 21
        uses: actions/setup-java@v5
        with:
          distribution: temurin
          java-version: "21"
          cache: maven

      - name: Run tests
        run: mvn --batch-mode test
```

- [ ] **Step 3: Revisar formato y alcance del cambio**

Run:

```powershell
git diff --check
git diff --stat
git diff -- .github/workflows/ci.yml
```

Expected: `git diff --check` no muestra errores; el diff contiene únicamente el nuevo workflow con los dos eventos y el trabajo `test`.

- [ ] **Step 4: Volver a ejecutar la suite local**

Run:

```powershell
mvn --batch-mode test
```

Expected: `BUILD SUCCESS`, sin fallos ni errores.

- [ ] **Step 5: Registrar el workflow**

Run:

```powershell
git add .github/workflows/ci.yml
git diff --cached --check
git diff --cached --stat
git commit -m "ci: run Maven tests with GitHub Actions"
```

Expected: se crea un commit que añade solamente `.github/workflows/ci.yml`.

### Task 2: Publicar y comprobar el workflow en GitHub

**Files:**
- Verify: `.github/workflows/ci.yml`

**Interfaces:**
- Consumes: el commit del workflow creado en Task 1.
- Produces: una ejecución real del trabajo `CI / Maven tests` asociada al pull request.

- [ ] **Step 1: Publicar los commits de la rama**

Run:

```powershell
git push
```

Expected: la rama `feature/github-actions-ci` queda actualizada en `origin`.

- [ ] **Step 2: Abrir un pull request hacia main**

Crear un pull request con:

```text
Title: ci: run Maven tests with GitHub Actions

Base: main
Compare: feature/github-actions-ci
```

La descripción debe indicar que el workflow usa Java 21, caché Maven y ejecuta la suite completa, incluida la prueba PostgreSQL con Testcontainers.

Expected: GitHub inicia automáticamente el workflow `CI`.

- [ ] **Step 3: Comprobar la ejecución remota**

Abrir la pestaña **Checks** del pull request y revisar el trabajo `Maven tests`.

Expected: aparecen correctamente los pasos `Checkout repository`, `Set up JDK 21` y `Run tests`; el trabajo finaliza en verde.

- [ ] **Step 4: Confirmar el estado local**

Run:

```powershell
git status
git log --oneline -3
```

Expected: árbol de trabajo limpio y rama sincronizada con `origin/feature/github-actions-ci`.
