# 🚴 Guía del Sistema de Repartidores

## 📊 Diagnóstico del Error 404

Si obtienes un error 404 "Pedido no encontrado", significa que el pedido no existe. Verifica primero los datos:

### 1. Verificar Pedidos Existentes
```http
GET /api/PedidosNew
Authorization: Bearer {tu_token}
```

**Esto te mostrará todos los pedidos con sus IDs reales.**

### 2. Verificar Repartidores Disponibles
```http
GET /api/Repartidores/disponibles
Authorization: Bearer {tu_token}
```

**Esto te mostrará los repartidores con Estado = Disponible (0).**

---

## 🔄 Flujo Completo de Pedido con Repartidor

### Paso 1: Crear un Repartidor (si no existe)
```http
POST /api/Repartidores
Authorization: Bearer {token_admin}
Content-Type: application/json

{
  "nombre": "Carlos",
  "apellidos": "Ramírez",
  "telefono": "4615554321",
  "usuarioId": 7
}
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "Carlos",
  "apellidos": "Ramírez",
  "telefono": "4615554321",
  "estado": 0,  // 0 = Disponible
  "usuarioId": 7
}
```

### Paso 2: Crear un Cliente (si no existe)
```http
POST /api/Clientes
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "María",
  "apellidos": "González",
  "telefono": "4619876543",
  "colonia": "Centro",
  "calle": "Hidalgo",
  "numeroCasa": "123",
  "usuarioId": 5
}
```

### Paso 3: Registrar un Pedido
```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token}
Content-Type: application/json

{
  "clienteId": 1,
  "tipo": 4,
  "metodoPago": 0,
  "origen": 0,
  "direccionEntrega": "Calle Hidalgo 123, Col. Centro",
  "observaciones": "Sin cebolla",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2
    }
  ]
}
```

**Respuesta (guarda el ID):**
```json
{
  "id": 5,  // ← Este es el ID que necesitas
  "clienteNombre": "María González",
  "repartidorNombre": null,
  "estado": 0,  // Pendiente
  "total": 150.00,
  "fechaPedido": "2025-11-12T00:33:00",
  "detalles": [...]
}
```

### Paso 4: Asignar Repartidor al Pedido
```http
PUT /api/PedidosNew/5/asignar-repartidor
Authorization: Bearer {token}
Content-Type: application/json

{
  "repartidorId": 1
}
```

**Respuesta exitosa: HTTP 204 No Content**

**Lo que sucede automáticamente:**
- ✅ Pedido cambia de `Pendiente (0)` → `EnPreparacion (1)`
- ✅ Repartidor cambia de `Disponible (0)` → `Ocupado (1)`

### Paso 5: Verificar la Asignación
```http
GET /api/PedidosNew/5
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "id": 5,
  "clienteNombre": "María González",
  "repartidorNombre": "Carlos Ramírez",  // ← Asignado
  "estado": 1,  // EnPreparacion
  "repartidorId": 1,
  "total": 150.00,
  "detalles": [...]
}
```

### Paso 6: Cambiar Estado a "En Camino"
```http
PUT /api/PedidosNew/5/estado
Authorization: Bearer {token}
Content-Type: application/json

2
```

**Estados disponibles:**
- `0` = Pendiente
- `1` = EnPreparacion
- `2` = EnCamino ← Cuando el repartidor sale
- `3` = Entregado ← Libera al repartidor (vuelve a Disponible)
- `4` = Cancelado ← También libera al repartidor

### Paso 7: Marcar como Entregado
```http
PUT /api/PedidosNew/5/estado
Authorization: Bearer {token}
Content-Type: application/json

3
```

**Lo que sucede automáticamente:**
- ✅ Pedido cambia a `Entregado (3)`
- ✅ Repartidor cambia de `Ocupado (1)` → `Disponible (0)`
- 🔄 El repartidor puede recibir nuevos pedidos

---

## 🔍 Verificación de Datos

### Ver Todos los Pedidos
```http
GET /api/PedidosNew
```

### Ver Pedidos Pendientes
```http
GET /api/PedidosNew/estado/0
```

### Ver Pedidos de un Repartidor
```http
GET /api/PedidosNew/repartidor/1
```

### Ver Repartidores Disponibles
```http
GET /api/Repartidores/disponibles
```

### Ver Estado de un Repartidor
```http
GET /api/Repartidores/1
```

---

## ❌ Errores Comunes

### 1. Error 404: "Pedido no encontrado"
**Causa:** El ID del pedido no existe en la base de datos.
**Solución:** 
```http
GET /api/PedidosNew
```
Usa un ID de los pedidos que te devuelva esta consulta.

### 2. Error 400: "Repartidor no encontrado"
**Causa:** El repartidorId no existe.
**Solución:**
```http
GET /api/Repartidores
```
Usa un ID de los repartidores existentes.

### 3. Error 400: "El repartidor no está disponible"
**Causa:** El repartidor tiene Estado = Ocupado (1) o Inactivo (2).
**Solución:**
```http
GET /api/Repartidores/disponibles
```
Elige un repartidor de esta lista, o espera a que termine su pedido actual.

### 4. Error 400: "El pedido debe tener al menos un producto"
**Causa:** El array `detalles` está vacío.
**Solución:** Agrega al menos un producto en el array `detalles`.

---

## 📱 Estados del Sistema

### Estados de Pedido (EstadoPedidoEnum)
| Valor | Nombre | Descripción |
|-------|--------|-------------|
| 0 | Pendiente | Recién creado, sin repartidor |
| 1 | EnPreparacion | Asignado a repartidor, en cocina |
| 2 | EnCamino | Repartidor en ruta |
| 3 | Entregado | Completado (libera repartidor) |
| 4 | Cancelado | Cancelado (libera repartidor) |

### Estados de Repartidor (RepartidorEstadoEnum)
| Valor | Nombre | Descripción |
|-------|--------|-------------|
| 0 | Disponible | Puede recibir pedidos |
| 1 | Ocupado | Tiene un pedido asignado |
| 2 | Inactivo | No está trabajando |

---

## 🎯 Ejemplo Práctico Completo

```powershell
# 1. Ver pedidos existentes
curl -X GET "https://localhost:7188/api/PedidosNew" `
  -H "Authorization: Bearer {token}"

# 2. Ver repartidores disponibles
curl -X GET "https://localhost:7188/api/Repartidores/disponibles" `
  -H "Authorization: Bearer {token}"

# 3. Crear un pedido (guarda el ID de la respuesta)
curl -X POST "https://localhost:7188/api/PedidosNew/registrar" `
  -H "Authorization: Bearer {token}" `
  -H "Content-Type: application/json" `
  -d '{
    "clienteId": 1,
    "tipo": 4,
    "metodoPago": 0,
    "origen": 0,
    "direccionEntrega": "Calle Hidalgo 123",
    "detalles": [{"productoId": 1, "cantidad": 2}]
  }'

# 4. Asignar repartidor (usa el ID del paso 3)
curl -X PUT "https://localhost:7188/api/PedidosNew/5/asignar-repartidor" `
  -H "Authorization: Bearer {token}" `
  -H "Content-Type: application/json" `
  -d '{"repartidorId": 1}'

# 5. Cambiar a "En Camino"
curl -X PUT "https://localhost:7188/api/PedidosNew/5/estado" `
  -H "Authorization: Bearer {token}" `
  -H "Content-Type: application/json" `
  -d '2'

# 6. Marcar como Entregado (libera repartidor)
curl -X PUT "https://localhost:7188/api/PedidosNew/5/estado" `
  -H "Authorization: Bearer {token}" `
  -H "Content-Type: application/json" `
  -d '3'
```

---

## 💡 Tips Importantes

1. **Siempre verifica los IDs primero**: Usa `GET /api/PedidosNew` y `GET /api/Repartidores/disponibles` antes de asignar.

2. **Un repartidor ocupado no puede recibir nuevos pedidos**: Marca los pedidos como Entregado (3) para liberar repartidores.

3. **El cambio de estado del repartidor es automático**: 
   - Se pone `Ocupado` al asignar un pedido
   - Vuelve a `Disponible` al entregar o cancelar

4. **Usa Swagger para probar**: Abre `https://localhost:7188/swagger` y prueba los endpoints interactivamente.

5. **El pedido cambia automáticamente a EnPreparacion**: Cuando asignas un repartidor a un pedido Pendiente.

---

**¡Ahora ya sabes cómo funciona el sistema de repartidores! 🚴📦**
