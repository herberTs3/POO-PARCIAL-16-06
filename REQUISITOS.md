# Requisitos — Sistema de Gestión de Reservas para Espacios Deportivos

## Contexto
Examen POO — UADE — 16-06-2026. Implementación en Java + Swing del modelo UML provisto.
La clase `Sistema` del diagrama es un anti-patrón; se reemplaza por controladores Singleton según lo visto en clase.

---

## A) Inventario de clases

### Enums (`models/enums/`)
| Enum | Valores |
|---|---|
| `TipoEspacio` | `FUTBOL`, `TENIS`, `PADEL`, `SALON_MULTIUSO` |
| `EstadoCliente` | `ACTIVO`, `INACTIVO` |
| `EstadoEspacio` | `DISPONIBLE`, `NO_DISPONIBLE`, `MANTENIMIENTO` |
| `EstadoReserva` | `INGRESADA`, `CONFIRMADA`, `CANCELADA`, `EN_CURSO`, `FINALIZADA` |
| `EstadoPago` | `REGISTRADO`, `CONFIRMADO`, `ANULADO` |
| `EstadoComplejo` | `ACTIVO`, `INACTIVO` |
| `MedioPago` | `EFECTIVO`, `TRANSFERENCIA`, `TARJETA`, `BILLETERA_VIRTUAL` |

### Modelos (`models/`)

#### `ComplejoDeportivo`
- `nombre: String`, `direccion: String`, `telefono: String`, `email: String`, `estado: EstadoComplejo`
- `List<EspacioDeportivo> espacios`
- `agregarEspacio(EspacioDeportivo)`
- `obtenerEspaciosDisponibles(fecha, horaInicio, horaFin, tipoActividad): List<EspacioDeportivo>`
- `coincideCodigo(codigo): boolean`

#### `EspacioDeportivo`
- `codigo: String`, `nombre: String`, `capacidad: int`, `precioBaseHora: double`
- `estado: EstadoEspacio`, `tipoEspacio: TipoEspacio`, `superficie: String`
- `estaDisponible(fecha, horaInicio, horaFin): boolean`
- `calcularPrecioBase(cantidadHoras): double`
- `coincideCodigo(codigo): boolean`
- `coincideTipoActividad(tipoActividad: TipoEspacio): boolean`

#### `Cliente`
- `dni: String`, `nombre: String`, `apellido: String`, `telefono: String`, `email: String`
- `estado: EstadoCliente`, `creditoAFavor: double`
- `List<Descuento> descuentos`
- `estaActivo(): boolean`
- `agregarCredito(importe: double)`
- `obtenerDescuentoVigente(fecha: Date): Descuento` (puede retornar `null`)
- `coincideDni(dni): boolean`

#### `Descuento`
- `porcentaje: double`, `fechaDesde: Date`, `fechaHasta: Date`
- `estaVigente(fecha: Date): boolean`

#### `Reserva` (abstracta)
- `codigo: String`, `fecha: Date`, `horaInicio: Time`, `horaFin: Time`
- `estado: EstadoReserva`, `importeSena: double`
- `cliente: Cliente`, `complejo: ComplejoDeportivo`, `espacio: EspacioDeportivo`
- `List<Pago> pagos`
- `calcularTotal(): double` (precio base + recargo − descuento aplicado)
- `calcularRecargo(): double` ← **abstracto**, implementado por subclases
- `calcularSaldoPendiente(): double`
- `cambiarEstado(nuevoEstado: EstadoReserva, usuario: String)`
- `registrarImporteSena(importe: double)`
- `agregarPago(pago: Pago)`
- `obtenerCliente(): Cliente`
- `obtenerImporteSena(): double`
- `calcularHorasAnticipacion(fechaCancelacion: Date): long`
- `estaConfirmada(): boolean`
- `coincideCodigo(codigo): boolean`

#### `ReservaComun` extends `Reserva`
- `calcularRecargo(): double` → retorna 0 (sin recargo)

#### `ReservaTorneo` extends `Reserva`
- `porcentajeRecargo: double` (parametrizable, default 20%)
- `calcularRecargo(): double` → `precioBase * porcentajeRecargo / 100`

#### `ReservaClaseGrupal` extends `Reserva`
- `porcentajeRecargo: double` (parametrizable, default 10%)
- `calcularRecargo(): double` → `precioBase * porcentajeRecargo / 100`

#### `Pago`
- `fecha: Date`, `importe: double`, `medioPago: MedioPago`
- `estado: EstadoPago`, `usuarioRegistro: String`
- `confirmar()`
- `anular()`

#### `HistorialCambioEstado`
- `fechaCambio: Date`, `estadoAnterior: String`, `estadoNuevo: String`
- `tipoEntidad: String`, `referencia: String`, `usuarioResponsable: String`

---

## B) Relaciones

| Relación | Tipo | Detalle |
|---|---|---|
| `ComplejoDeportivo` → `EspacioDeportivo` | Composición 1..* | El complejo posee sus espacios |
| `Reserva` → `Cliente` | Asociación 1 | La reserva referencia al cliente |
| `Reserva` → `ComplejoDeportivo` | Asociación 1 | La reserva referencia al complejo |
| `Reserva` → `EspacioDeportivo` | Asociación 1 | La reserva referencia al espacio |
| `Reserva` → `Pago` | Composición 0..* | La reserva posee sus pagos |
| `Cliente` → `Descuento` | Composición 0..* | El cliente posee sus descuentos |
| `Reserva` (abstracta) → `ReservaComun`, `ReservaTorneo`, `ReservaClaseGrupal` | Herencia |

---

## C) Casos de uso (Diagramas de secuencia)

### CU1 — Solicitar reserva
**Orquestador:** `ReservaController`
**Flujo:**
1. Buscar `Cliente` por DNI en `ClienteController`.
2. Buscar `ComplejoDeportivo` por código en `ComplejoDeportivoController`.
3. Buscar `EspacioDeportivo` por código dentro del complejo.
4. Verificar disponibilidad: `espacio.estaDisponible(fecha, horaInicio, horaFin)`.
5. Crear instancia de `Reserva` (tipo según `tipoReserva`), inicializarla con estado `INGRESADA`.
6. Crear `HistorialCambioEstado(null, INGRESADA, "Reserva", codigoReserva, usuario)`.
7. Retornar reserva registrada.

### CU2 — Confirmar reserva con seña
**Orquestador:** `ReservaController`
**Flujo:**
1. Buscar `Reserva` por código.
2. Crear `Pago(fechaActual, importeSena, medioPago, REGISTRADO, usuario)`.
3. `pago.confirmar()` → estado pasa a `CONFIRMADO`.
4. `reserva.agregarPago(pago)`.
5. `reserva.registrarImporteSena(importeSena)`.
6. `reserva.cambiarEstado(CONFIRMADA, usuario)`.
7. Crear `HistorialCambioEstado(INGRESADA, CONFIRMADA, "Reserva", codigoReserva, usuario)`.

### CU3 — Cancelar reserva
**Orquestador:** `ReservaController`
**Flujo:**
1. Buscar `Reserva` por código.
2. `reserva.obtenerCliente()`.
3. `reserva.calcularHorasAnticipacion(fechaCancelacion)`.
4. `reserva.obtenerImporteSena()`.
5. Si `horasAnticipacion > 24`: `cliente.agregarCredito(importeSena)`.
6. `reserva.cambiarEstado(CANCELADA, usuario)`.
7. Crear `HistorialCambioEstado(CONFIRMADA, CANCELADA, "Reserva", codigoReserva, usuario)`.

### CU4 — Finalizar reserva y calcular saldo
**Orquestador:** `ReservaController`
**Flujo:**
1. Buscar `Reserva` por código.
2. `reserva.cambiarEstado(FINALIZADA, usuario)`.
3. `reserva.obtenerCliente()`.
4. `cliente.obtenerDescuentoVigente(fechaReserva)`.
5. `reserva.calcularPrecioBase()`.
6. `reserva.calcularRecargo()`.
7. Si existe descuento vigente: `descuento.obtenerPorcentaje()` → `reserva.aplicarDescuento(porcentaje)`.
8. `reserva.calcularSaldoPendiente()`.
9. Crear `HistorialCambioEstado(EN_CURSO, FINALIZADA, "Reserva", codigoReserva, usuario)`.

### CU5 — Consultar espacios disponibles
**Orquestador:** `ReservaController` / `ComplejoDeportivoController`
**Flujo:**
1. Buscar `ComplejoDeportivo` por código.
2. `complejo.obtenerEspacios()`.
3. Para cada espacio: `espacio.coincideTipoActividad(tipoActividad)`.
4. Si coincide: `espacio.estaDisponible(fecha, horaInicio, horaFin)`.
5. Si disponible: agregar a lista resultado.
6. Retornar lista.

---

## D) Controladores (Singleton)

| Controller | Responsabilidad | Almacena |
|---|---|---|
| `ClienteController` | CRUD clientes + descuentos | `HashMap<String, Cliente>` (clave: DNI) |
| `ComplejoDeportivoController` | CRUD complejos + espacios | `HashMap<String, ComplejoDeportivo>` (clave: código) |
| `ReservaController` | Orquesta los 5 CU + CRUD reservas | `HashMap<String, Reserva>` (clave: código) |
| `PagoController` | CRUD pagos (opcional, los pagos viven en Reserva) | — |
| `HistorialController` | Registro de cambios de estado | `ArrayList<HistorialCambioEstado>` |

---

## E) Vistas Swing (mínimo 2 funcionalidades)

Se implementarán las siguientes ventanas:
1. **`MainFrame`** — menú principal con acceso a módulos.
2. **`SolicitarReservaDialog`** — formulario para CU1 (solicitar reserva).
3. **`ConsultarEspaciosFrame`** — búsqueda de espacios disponibles (CU5).

> El examen solicita exactamente 2 funcionalidades con interfaz gráfica. Se implementarán CU1 y CU5 como prioridad.

---

## F) Pruebas unitarias (5 requeridas)

1. `testReservaTorneoAplicaRecargo20()` — verifica que `calcularRecargo()` devuelve 20% del precio base.
2. `testReservaClaseGrupalAplicaRecargo10()` — verifica 10%.
3. `testReservaComunSinRecargo()` — verifica que el recargo es 0.
4. `testCancelacionConMas24HorasDevuelveCredito()` — verifica que `creditoAFavor` aumenta.
5. `testCancelacionConMenos24HorasNoDevuelveCredito()` — verifica que `creditoAFavor` no cambia.

---

## Orden de implementación sugerido

```
Fase 1 — Enums y modelos base
  [ ] 1.  Enums (7 enums)
  [ ] 2.  HistorialCambioEstado
  [ ] 3.  Descuento
  [ ] 4.  EspacioDeportivo
  [ ] 5.  ComplejoDeportivo
  [ ] 6.  Cliente
  [ ] 7.  Pago
  [ ] 8.  Reserva (abstracta) + ReservaComun + ReservaTorneo + ReservaClaseGrupal

Fase 2 — Controladores Singleton
  [ ] 9.  HistorialController
  [ ] 10. ClienteController (CRUD + datos de prueba)
  [ ] 11. ComplejoDeportivoController (CRUD + datos de prueba)
  [ ] 12. ReservaController (CU1: solicitarReserva)
  [ ] 13. ReservaController (CU2: confirmarReservaConSena)
  [ ] 14. ReservaController (CU3: cancelarReserva)
  [ ] 15. ReservaController (CU4: finalizarReserva)
  [ ] 16. ReservaController (CU5: consultarEspaciosDisponibles)

Fase 3 — Interfaces Swing
  [ ] 17. MainFrame con menú
  [ ] 18. SolicitarReservaDialog (CU1)
  [ ] 19. ConsultarEspaciosFrame (CU5)

Fase 4 — Pruebas unitarias
  [ ] 20. Cinco pruebas unitarias (sin dependencia de Swing)
```
