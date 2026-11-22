# Cappucare – Microservicios

Microservicios desarrollados en Spring Boot que respaldan la app móvil de Cappucare de control de
existencias de productos e insumos médicos.

Repositorio de la aplicación: https://github.com/ItaloCarmonaM/Cappucare


## Integrantes

Ítalo Carmona, Luciano Sepulveda e Isahac Zapata
## Microservicios

| Microservicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `ms-usuarios` | 8081 | H2 (`data/usuarios_db`) | Inicio de sesión y CRUD de usuarios |
| `ms-inventario` | 8082 | H2 (`data/inventario_db`) | CRUD de productos, movimientos de stock, historial y productos en stock bajo |

Cada microservicio es independiente: tiene su propio proyecto, puerto y base de datos. Como el usuario vive en
`ms-usuarios`, cada movimiento de `ms-inventario` guarda una copia del nombre del responsable.

## Tecnologías

- Java 17 o superior
- Spring Boot 4 (Spring Web, Spring Data JPA, Validation)
- H2 en modo archivo
- Lombok
- BCrypt (`spring-security-crypto`) para encriptar las claves

## Estructura del repositorio

```
backend-cappucare/
├── ms-usuarios/
│   └── src/main/java/com/example/ms_usuarios/
│       ├── controller/   Endpoints REST
│       ├── service/      Lógica de negocio
│       ├── repository/   Acceso a la base de datos (JPA)
│       ├── model/        Entidades
│       ├── dto/          Cuerpos de solicitud y respuesta
│       ├── exception/    Errores con formato { "mensaje": "..." }
│       └── config/       Encriptación de claves y datos iniciales
├── ms-inventario/        (misma estructura)
└── postman/              Colecciones de pruebas manuales
```

## Cómo ejecutar

### Requisitos

- JDK 17 o superior
- Cualquier editor (IntelliJ IDEA, VS Code) o solo una terminal

### Pasos

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/ItaloCarmonaM/backend-cappucare.git
   ```
2. Iniciar `ms-usuarios` desde su carpeta:
   ```bash
   cd ms-usuarios
   ./mvnw spring-boot:run
   ```
3. En otra terminal, iniciar `ms-inventario`:
   ```bash
   cd ms-inventario
   ./mvnw spring-boot:run
   ```

### Consola de la base de datos

Cada microservicio incluye la consola web de H2 para ver las tablas:

| Microservicio | URL | JDBC URL | Usuario | Contraseña |
|---|---|---|---|---|
| `ms-usuarios` | http://localhost:8081/h2-console | `jdbc:h2:file:./data/usuarios_db` | `sa` | (vacía) |
| `ms-inventario` | http://localhost:8082/h2-console | `jdbc:h2:file:./data/inventario_db` | `sa` | (vacía) |

### Conexión con la app

La app necesita llegar al computador donde corren los microservicios. La dirección depende de dónde se ejecute:

| Dónde corre la app | Dirección | Requisitos |
|---|---|---|
| Emulador de Android | `10.0.2.2` | Ninguno: es la dirección del computador vista desde el emulador |

- `http://10.0.2.2:8081/` → `ms-usuarios` (emulador)
- `http://10.0.2.2:8082/` → `ms-inventario` (emulador)


## Datos de prueba

**Usuarios** (`ms-usuarios`; las claves se guardan encriptadas)

| Nombre | Correo | Clave | Perfil |
|---|---|---|---|
| María González | encargado@cappucare.cl | cappu123 | ENCARGADO |
| Pedro Soto | consultor@cappucare.cl | cappu123 | CONSULTOR |

**Inventario** (`ms-inventario`): 8 productos (disponibles, en stock bajo y agotados) y 3 movimientos de ejemplo.

## Endpoints

### Formato de errores

Los errores siempre responden con este cuerpo:

```json
{ "mensaje": "Stock insuficiente: disponible 5" }
```

| Código | Cuándo ocurre |
|---|---|
| 400 | Falla una validación del cuerpo, el JSON no es válido, o se rompe RN3 o RN5 |
| 401 | Credenciales incorrectas en el login |
| 404 | El usuario o el producto no existe |
| 409 | Código de producto o correo ya en uso; producto con movimientos que se intenta eliminar |

### ms-usuarios (puerto 8081)

| Método | Endpoint | Cuerpo | Respuesta |
|---|---|---|---|
| POST | `/api/auth/login` | `{ "correo", "clave" }` | Usuario |
| GET | `/api/usuarios` | — | Lista de usuarios |
| GET | `/api/usuarios/{id}` | — | Usuario |
| POST | `/api/usuarios` | `{ "nombre", "correo", "clave", "perfil" }` | Usuario (201) |
| PUT | `/api/usuarios/{id}` | `{ "nombre", "correo", "clave", "perfil" }` | Usuario |
| DELETE | `/api/usuarios/{id}` | — | 204 |

```json
{ "id": 1, "nombre": "María González", "correo": "encargado@cappucare.cl", "perfil": "ENCARGADO" }
```

### ms-inventario (puerto 8082)

| Método | Endpoint | Cuerpo | Respuesta |
|---|---|---|---|
| GET | `/api/productos?buscar=` | — | Lista de productos por nombre o código (`buscar` es opcional) |
| GET | `/api/productos/stock-bajo` | — | Productos en stock bajo o agotados |
| GET | `/api/productos/{id}` | — | Producto |
| POST | `/api/productos` | Producto (sin `id`) | Producto (201) |
| PUT | `/api/productos/{id}` | Producto | Producto (no modifica la cantidad) |
| DELETE | `/api/productos/{id}` | — | 204 (409 si tiene movimientos) |
| GET | `/api/movimientos` | — | Historial, del más reciente al más antiguo |
| POST | `/api/movimientos` | Movimiento | Producto actualizado y aviso de nivel mínimo (201) |

```json
// Producto
{
  "id": 1, "codigo": "MED-001", "nombre": "Guantes de nitrilo talla M",
  "descripcion": "Caja de 100 unidades", "categoria": "Protección", "cantidad": 120,
  "unidadMedida": "caja", "nivelMinimo": 30, "ubicacion": "Bodega A - Estante 1"
}

// POST /api/movimientos (el motivo solo aplica a salidas; la fecha la asigna el servidor)
{
  "productoId": 6, "usuarioId": 1, "usuarioNombre": "María González",
  "tipo": "SALIDA", "cantidad": 2, "motivo": "VENTA"
}

// Respuesta de POST /api/movimientos
{ "producto": { "...": "producto actualizado" }, "alcanzoNivelMinimo": true }

// Elemento de GET /api/movimientos (fecha en milisegundos)
{
  "movimiento": { "id": 1, "productoId": 6, "usuarioId": 1, "tipo": "SALIDA", "cantidad": 2, "motivo": "VENTA", "fecha": 1763500000000 },
  "productoNombre": "Termómetro digital",
  "usuarioNombre": "María González"
}
```

Valores permitidos: `perfil` = `ENCARGADO` | `CONSULTOR` · `tipo` = `INGRESO` | `SALIDA` ·
`motivo` = `VENTA` | `PRODUCTO_VENCIDO` | `PRODUCTO_DANADO` | `CORRECCION_STOCK`.

## Reglas de negocio en `ms-inventario`

| N° | Regla | Cómo se aplica |
|---|---|---|
| RN1 | El código del producto es único y obligatorio | Responde `409` si el código ya existe (al crear y al editar) |
| RN2 | La cantidad nunca es negativa | Validación `@Min(0)` al crear un producto |
| RN3 | Una salida no puede superar el stock | Responde `400` con el stock disponible |
| RN4 | La cantidad de un movimiento es mayor a 0 | Validación `@Positive` |
| RN5 | Toda salida lleva un motivo | Responde `400` si falta el motivo |
| RN6 | Todo movimiento actualiza el stock y queda en el historial | Se guarda en una sola transacción; el stock solo cambia por movimientos |
| RN8 | Se avisa una sola vez al llegar al nivel mínimo | `alcanzoNivelMinimo` es `true` solo cuando el stock cruza el mínimo hacia abajo |