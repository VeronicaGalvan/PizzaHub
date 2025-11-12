# 📖 Ejemplos de Uso de la API PizzaHub

## 🔐 Autenticación

Todos los endpoints (excepto login/register) requieren un token JWT en el header:

```
Authorization: Bearer {tu_token_jwt}
```

---

## 1️⃣ **Empleados**

### Crear Empleado
```http
POST /api/Empleados
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Juan",
  "apellidos": "Pérez García",
  "telefono": "4612345678",
  "usuarioId": 5
}
```

### Listar Empleados Activos
```http
GET /api/Empleados/activos
Authorization: Bearer {token}
```

---

## 2️⃣ **Insumos**

### Crear Insumo
```http
POST /api/Insumos
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Harina de trigo",
  "unidadMedida": 0,
  "stockActual": 50.5,
  "stockMinimo": 10.0
}
```

**UnidadMedida:**
- 0 = Kg
- 1 = g
- 2 = L
- 3 = ml
- 4 = Uds

### Obtener Insumos con Stock Bajo
```http
GET /api/Insumos/bajo-stock
Authorization: Bearer {token}
```

---

## 3️⃣ **Inventario Log**

### Registrar Entrada de Inventario
```http
POST /api/InventarioLog
Authorization: Bearer {token}
Content-Type: application/json

{
  "insumoId": 1,
  "cantidad": 25.0,
  "tipoMovimiento": 0,
  "motivo": "Compra a proveedor"
}
```

**TipoMovimiento:**
- 0 = Entrada
- 1 = Salida

### Registrar Salida de Inventario
```http
POST /api/InventarioLog
Authorization: Bearer {token}
Content-Type: application/json

{
  "insumoId": 1,
  "cantidad": 5.0,
  "tipoMovimiento": 1,
  "motivo": "Uso en producción"
}
```

---

## 4️⃣ **Caja**

### Abrir Caja
```http
POST /api/Caja/abrir
Authorization: Bearer {token}
Content-Type: application/json

{
  "saldoInicial": 500.00,
  "empleadoId": 1
}
```

### Obtener Caja Abierta
```http
GET /api/Caja/abierta
Authorization: Bearer {token}
```

### Cerrar Caja
```http
POST /api/Caja/1/cerrar
Authorization: Bearer {token}
Content-Type: application/json

{
  "saldoFinal": 2350.50
}
```

**Respuesta:**
```json
{
  "id": 1,
  "fecha": "2025-11-10T00:00:00",
  "saldoInicial": 500.00,
  "saldoFinal": 2350.50,
  "totalVentas": 1850.50,
  "cantidadVentas": 15,
  "ventasPorMetodoPago": {
    "Efectivo": 1200.00,
    "Tarjeta": 450.50,
    "Transferencia": 200.00
  },
  "empleadoNombre": "Juan Pérez García"
}
```

### Obtener Resumen de Caja
```http
GET /api/Caja/1/resumen
Authorization: Bearer {token}
```

---

## 5️⃣ **Ventas**

### Registrar Venta
```http
POST /api/Ventas
Authorization: Bearer {token}
Content-Type: application/json

{
  "cajaId": 1,
  "pedidoId": 5,
  "empleadoId": 1,
  "metodoPago": 0,
  "total": 125.50
}
```

**MetodoPago:**
- 0 = Efectivo
- 1 = Tarjeta
- 2 = Plataforma
- 3 = Transferencia

### Obtener Ventas por Caja
```http
GET /api/Ventas/caja/1
Authorization: Bearer {token}
```

### Obtener Ventas por Fecha
```http
GET /api/Ventas/fecha/2025-11-10
Authorization: Bearer {token}
```

---

## 6️⃣ **Pedidos**

### Registrar Pedido con Detalles
```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token}
Content-Type: application/json

{
  "clienteId": 3,
  "tipo": 4,
  "metodoPago": 0,
  "origen": 0,
  "direccionEntrega": "Calle Hidalgo 123, Col. Centro",
  "observaciones": "Sin cebolla",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2
    },
    {
      "productoId": 3,
      "cantidad": 1
    }
  ]
}
```

**Tipo:**
- 0 = Mostrador
- 1 = Llamada-Recoge
- 2 = Llamada-Envio
- 3 = Plataforma
- 4 = App

**Origen:**
- 0 = App
- 1 = Llamada
- 2 = Plataforma
- 3 = Mostrador

**Estado (automático):**
- 0 = Pendiente
- 1 = En preparación
- 2 = En camino
- 3 = Entregado
- 4 = Cancelado

### Asignar Repartidor
```http
PUT /api/PedidosNew/5/asignar-repartidor
Authorization: Bearer {token}
Content-Type: application/json

{
  "repartidorId": 2
}
```

### Cambiar Estado del Pedido
```http
PUT /api/PedidosNew/5/estado
Authorization: Bearer {token}
Content-Type: application/json

1
```

Valores posibles: 0 (Pendiente), 1 (En preparación), 2 (En camino), 3 (Entregado), 4 (Cancelado)

### Obtener Pedidos por Estado
```http
GET /api/PedidosNew/estado/1
Authorization: Bearer {token}
```

### Obtener Pedidos de un Cliente
```http
GET /api/PedidosNew/cliente/3
Authorization: Bearer {token}
```

### Obtener Pedidos de un Repartidor
```http
GET /api/PedidosNew/repartidor/2
Authorization: Bearer {token}
```

---

## 7️⃣ **Calificaciones**

### Registrar Calificación
```http
POST /api/Calificaciones/pedido/5
Authorization: Bearer {token}
Content-Type: application/json

{
  "estrellas": 5,
  "comentario": "Excelente servicio, pizza deliciosa"
}
```

**Estrellas:** Valor entre 1 y 5

### Obtener Calificación de un Pedido
```http
GET /api/Calificaciones/pedido/5
Authorization: Bearer {token}
```

### Obtener Promedio de Calificaciones
```http
GET /api/Calificaciones/promedio
```

**Respuesta:**
```json
{
  "promedio": 4.75,
  "total": 48
}
```

### Obtener Estadísticas de Calificaciones
```http
GET /api/Calificaciones/estadisticas
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "promedio": 4.75,
  "total": 48,
  "porEstrellas": {
    "5": 35,
    "4": 10,
    "3": 2,
    "2": 1,
    "1": 0
  }
}
```

---

## 📊 **Flujo Completo de Ejemplo**

### 1. Abrir Caja (inicio del día)
```http
POST /api/Caja/abrir
{
  "saldoInicial": 500.00,
  "empleadoId": 1
}
```

### 2. Cliente hace un pedido
```http
POST /api/PedidosNew/registrar
{
  "clienteId": 3,
  "tipo": 4,
  "metodoPago": 0,
  "origen": 0,
  "direccionEntrega": "Calle Hidalgo 123",
  "detalles": [
    { "productoId": 1, "cantidad": 1 },
    { "productoId": 5, "cantidad": 2 }
  ]
}
```

### 3. Asignar repartidor al pedido
```http
PUT /api/PedidosNew/15/asignar-repartidor
{
  "repartidorId": 2
}
```

### 4. Actualizar estado a "En camino"
```http
PUT /api/PedidosNew/15/estado
2
```

### 5. Registrar venta en la caja
```http
POST /api/Ventas
{
  "cajaId": 1,
  "pedidoId": 15,
  "empleadoId": 1,
  "metodoPago": 0,
  "total": 185.50
}
```

### 6. Actualizar estado a "Entregado"
```http
PUT /api/PedidosNew/15/estado
3
```

### 7. Cliente califica el pedido
```http
POST /api/Calificaciones/pedido/15
{
  "estrellas": 5,
  "comentario": "Todo perfecto!"
}
```

### 8. Cerrar caja (fin del día)
```http
POST /api/Caja/1/cerrar
{
  "saldoFinal": 2350.50
}
```

---

## 8️⃣ **Productos**

### Listar Productos Activos
```http
GET /api/Productos/activos
```

### Crear Producto
```http
POST /api/Productos
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Pizza Hawaiana Grande",
  "descripcion": "Pizza de jamón y piña con queso mozzarella",
  "tipo": "pizza",
  "precio": 149.00,
  "almacenable": false,
  "imagenUrl": "https://example.com/hawaiana.jpg"
}
```

### Actualizar Producto
```http
PUT /api/Productos/5
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Pizza Hawaiana Grande",
  "descripcion": "Pizza de jamón y piña con queso mozzarella",
  "tipo": "pizza",
  "precio": 159.00,
  "almacenable": false,
  "imagenUrl": "https://example.com/hawaiana.jpg"
}
```

### Desactivar Producto
```http
PATCH /api/Productos/5/desactivar
Authorization: Bearer {token}
```

### Activar Producto
```http
PATCH /api/Productos/5/activar
Authorization: Bearer {token}
```

### Obtener Productos por Tipo
```http
GET /api/Productos/tipo/pizza
```

---

## 9️⃣ **Clientes**

### Listar Todos los Clientes
```http
GET /api/Clientes
Authorization: Bearer {token}
```

### Obtener Cliente por ID
```http
GET /api/Clientes/3
Authorization: Bearer {token}
```

### Obtener Cliente por Usuario
```http
GET /api/Clientes/usuario/5
Authorization: Bearer {token}
```

### Crear Cliente
```http
POST /api/Clientes
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "María",
  "apellidos": "González López",
  "telefono": "4619876543",
  "colonia": "Centro",
  "calle": "Hidalgo",
  "numeroCasa": "123",
  "observaciones": "Casa amarilla con portón blanco",
  "usuarioId": 5
}
```

### Actualizar Cliente
```http
PUT /api/Clientes/3
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "María",
  "apellidos": "González López",
  "telefono": "4619876543",
  "colonia": "Centro",
  "calle": "Hidalgo",
  "numeroCasa": "123",
  "observaciones": "Casa amarilla con portón blanco",
  "usuarioId": 5
}
```

---

## � **Repartidores**

### Listar Repartidores Disponibles
```http
GET /api/Repartidores/disponibles
Authorization: Bearer {token}
```

### Obtener Repartidor por Usuario
```http
GET /api/Repartidores/usuario/7
Authorization: Bearer {token}
```

### Crear Repartidor
```http
POST /api/Repartidores
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Carlos",
  "apellidos": "Ramírez Soto",
  "telefono": "4615554321",
  "usuarioId": 7
}
```

### Cambiar Estado del Repartidor
```http
PATCH /api/Repartidores/2/estado
Authorization: Bearer {token}
Content-Type: application/json

{
  "estado": 1
}
```

**Estado:**
- 0 = Disponible
- 1 = Ocupado
- 2 = Inactivo

---

## �🔍 **Filtros y Consultas**

### Productos
```http
GET /api/Productos
GET /api/Productos/5
GET /api/Productos/activos
GET /api/Productos/tipo/pizza
```

### Clientes
```http
GET /api/Clientes
GET /api/Clientes/3
GET /api/Clientes/usuario/5
```

### Repartidores
```http
GET /api/Repartidores
GET /api/Repartidores/2
GET /api/Repartidores/disponibles
GET /api/Repartidores/usuario/7
```

---

## ⚠️ **Códigos de Error Comunes**

- **400 Bad Request**: Datos inválidos o falta información requerida
- **401 Unauthorized**: Token JWT inválido o expirado
- **403 Forbidden**: No tienes permisos para esta acción
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

---

## 💡 **Tips**

1. **Usa Swagger** para probar los endpoints: `https://localhost:7xxx/swagger`
2. **Token JWT**: Guarda el token después del login y úsalo en todas las peticiones
3. **Roles**: Algunos endpoints requieren roles específicos (Administrador, Empleado)
4. **Fechas**: Usa formato ISO 8601: `2025-11-10T15:30:00`
5. **Decimales**: Usa punto (.) como separador: `125.50`

---

**¡Listo para usar! 🚀**
