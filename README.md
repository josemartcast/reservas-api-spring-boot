# ReservaCollectionsDia2

Proyecto Maven para NetBeans con Java 21.

## Ejercicio pendiente

Implementar en `ReservaService`:

```java
public int calcularTotalPersonasReservadasPorFecha(String fecha)
```

Reglas:

1. Validar que `fecha` no sea `null` ni esté en blanco.
2. Crear un acumulador `int total = 0`.
3. Recorrer todas las reservas.
4. Si la fecha coincide, sumar `numeroPersonas`.
5. Devolver el total.

## Resultado esperado

Con las reservas cargadas en `Main`, para la fecha `2026-06-17` debe devolver:

```text
Total de personas reservadas: 6
```

## Abrir en NetBeans

1. Descomprime el ZIP.
2. NetBeans → File → Open Project.
3. Selecciona la carpeta `ReservaCollectionsDia2`.
4. Implementa el método.
5. Ejecuta `Main`.
