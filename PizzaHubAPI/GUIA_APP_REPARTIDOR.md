# 🏍️ MÓDULO DE REPARTIDOR - App del Repartidor

## 📋 RESUMEN EJECUTIVO

**SÍ, el repartidor está bien representado** en tu sistema. Tiene su propio módulo completo con endpoints específicos donde puede:

✅ Ver solo sus pedidos asignados  
✅ Ver datos completos del cliente (nombre, dirección, teléfono)  
✅ Actualizar estados de entrega  
✅ Ver historial de entregas  
✅ Cambiar su disponibilidad  
✅ Ver estadísticas de desempeño  

---

## ❓ ¿QUÉ CAMBIA ENTRE PEDIDO DE APP vs LLAMADA?

### **Para el repartidor: NADA**

El repartidor ve EXACTAMENTE la misma información independientemente del origen:

| Dato | App Móvil | Llamada |
|------|-----------|---------|
| Nombre del cliente | ✅ | ✅ |
| Dirección completa | ✅ | ✅ |
| Teléfono | ✅ | ✅ |
| Productos | ✅ | ✅ |
| Total | ✅ | ✅ |
| Observaciones | ✅ | ✅ |

**La ÚNICA diferencia operativa:**
- **App/Tarjeta**: Cliente ya pagó → Solo entregar
- **Llamada/Efectivo**: Repartidor cobra al entregar

---

## 🔐 AUTENTICACIÓN

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "repartidor@pizzahub.com",
  "password": "12345678"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "...",
  "roles": ["Repartidor"],
  "usuario": {
    "id": 5,
    "email": "repartidor@pizzahub.com",
    "nombreUsuario": "Repartidor123"
  }
}
```

---

## 📱 ENDPOINTS DEL REPARTIDOR

**Base URL:** `/api/MisEnvios`  
**Rol requerido:** `Repartidor`  
**Autenticación:** Bearer Token

---

### 1. **Ver Mi Perfil**
```http
GET /api/MisEnvios/mi-perfil
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": 3,
  "nombre": "Juan",
  "apellidos": "Pérez",
  "telefono": "4771234567",
  "estado": "Disponible",
  "email": "juan@pizzahub.com"
}
```

---

### 2. **Ver Mis Pedidos Pendientes** ⭐ PRINCIPAL
```http
GET /api/MisEnvios/pendientes
Authorization: Bearer {token}
```

Muestra pedidos que aún no has recogido.

**Response:**
```json
[
  {
    "id": 123,
    "estado": "EnPreparacion",
    "total": 350.00,
    "direccionEntrega": "Calle Juárez 123, Centro",
    "observaciones": "Casa azul con portón blanco",
    "fechaPedido": "2025-11-15T14:30:00",
    "metodoPago": "Efectivo",
    "tipo": "LlamadaEnvio",
    "cliente": {
      "nombre": "María González",
      "telefono": "4779876543",
      "direccion": "Calle Juárez 123, Centro"
    },
    "cantidadProductos": 3
  }
]
```

---

### 3. **Ver Pedidos En Camino**
```http
GET /api/MisEnvios/en-camino
Authorization: Bearer {token}
```

Muestra pedidos que ya recogiste y estás llevando.

---

### 4. **Ver Detalles de un Pedido** ⭐
```http
GET /api/MisEnvios/pedido/123
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": 123,
  "estado": "EnCamino",
  "total": 350.00,
  "direccionEntrega": "Calle Juárez 123, Centro",
  "observaciones": "Sin cebolla",
  "fechaPedido": "2025-11-15T14:30:00",
  "metodoPago": "Efectivo",
  "tipo": "LlamadaEnvio",
  "origen": "Llamada",
  "cliente": {
    "id": 5,
    "nombre": "María González",
    "telefono": "4779876543",
    "calle": "Juárez",
    "numeroCasa": "123",
    "colonia": "Centro",
    "direccionCompleta": "Juárez 123, Centro",
    "observaciones": "Casa con portón blanco"
  },
  "productos": [
    {
      "nombre": "Pizza Hawaiana Grande",
      "cantidad": 2,
      "precioUnitario": 150.00,
      "subtotal": 300.00
    },
    {
      "nombre": "Refresco 600ml",
      "cantidad": 1,
      "precioUnitario": 50.00,
      "subtotal": 50.00
    }
  ],
  "totalProductos": 3
}
```

---

### 5. **Recoger Pedido** ⭐
```http
PUT /api/MisEnvios/pedido/123/recoger
Authorization: Bearer {token}
```

**Qué hace:**
- Cambia el pedido a "EnCamino"
- Te marca como "Ocupado"

**Response:**
```json
{
  "message": "Pedido recogido, en camino al cliente"
}
```

---

### 6. **Marcar como Entregado** ⭐
```http
PUT /api/MisEnvios/pedido/123/entregar
Authorization: Bearer {token}
```

**Qué hace:**
- Marca el pedido como "Entregado"
- Te marca como "Disponible" (si no tienes más pedidos)

**Response:**
```json
{
  "message": "Pedido entregado exitosamente"
}
```

---

### 7. **Ver Estadísticas**
```http
GET /api/MisEnvios/estadisticas
Authorization: Bearer {token}
```

**Response:**
```json
{
  "hoy": {
    "total": 8,
    "entregados": 6,
    "enCamino": 1,
    "pendientes": 1,
    "totalGanancias": 2100.00
  },
  "general": {
    "totalEntregas": 156,
    "totalPedidos": 162,
    "totalGanancias": 54300.00
  },
  "estadoActual": "Ocupado"
}
```

---

### 8. **Cambiar Disponibilidad**
```http
PATCH /api/MisEnvios/disponibilidad
Authorization: Bearer {token}
Content-Type: application/json

{
  "disponible": true
}
```

**Response:**
```json
{
  "message": "Ahora estás disponible",
  "estado": "Disponible"
}
```

---

## 🔄 FLUJO COMPLETO

### **Caso 1: Pedido desde App (Ya Pagado)**

```
1. 📱 Cliente ordena desde app móvil
   Tipo: App
   Método de pago: Tarjeta
   Estado: Pagado ✅
   
2. 🏢 Empleado asigna repartidor
   PUT /api/PedidosNew/123/asignar-repartidor
   
3. 🏍️ Repartidor ve el pedido
   GET /api/MisEnvios/pendientes
   
   Pantalla muestra:
   ┌─────────────────────────────────┐
   │ PEDIDO #123                     │
   │ 📍 Juárez 123, Centro           │
   │ 👤 María González               │
   │ 📱 477-987-6543                 │
   │ 💳 $350 - PAGADO CON TARJETA   │
   │ ⚠️ NO COBRAR                    │
   │ [VER DETALLES] [RECOGER]       │
   └─────────────────────────────────┘
   
4. 🏍️ Repartidor recoge pedido
   PUT /api/MisEnvios/pedido/123/recoger
   Estado → "En camino"
   
5. 🏍️ Repartidor entrega
   PUT /api/MisEnvios/pedido/123/entregar
   ✅ Solo entrega, NO cobra
```

---

### **Caso 2: Pedido por Llamada (Pago en Efectivo)**

```
1. ☎️ Cliente llama para pedir
   Tipo: Llamada-Envío
   Método de pago: Efectivo
   Estado: Pendiente de pago ⏳
   
2. 🏢 Empleado asigna repartidor
   PUT /api/PedidosNew/124/asignar-repartidor
   
3. 🏍️ Repartidor ve el pedido
   GET /api/MisEnvios/pendientes
   
   Pantalla muestra:
   ┌─────────────────────────────────┐
   │ PEDIDO #124                     │
   │ 📍 Hidalgo 789, Centro          │
   │ 👤 Juan Ramírez                 │
   │ 📱 477-123-4567                 │
   │ 💵 $350 - COBRAR EN EFECTIVO   │
   │ ⚠️ COBRAR AL ENTREGAR           │
   │ [VER DETALLES] [RECOGER]       │
   └─────────────────────────────────┘
   
4. 🏍️ Repartidor recoge pedido
   PUT /api/MisEnvios/pedido/124/recoger
   Estado → "En camino"
   
5. 🏍️ Repartidor entrega y cobra
   PUT /api/MisEnvios/pedido/124/entregar
   💵 Cobra $350 en efectivo
```

---

## 📊 COMPARACIÓN VISUAL

### App Móvil (Pagado)
```
┌────────────────────────────────────┐
│ PEDIDO #123 - App                  │
│ Estado: En preparación             │
├────────────────────────────────────┤
│ 👤 María González                  │
│ 📱 477-987-6543                    │
│ 📍 Juárez 123, Centro              │
├────────────────────────────────────┤
│ 2x Pizza Hawaiana       $300       │
│ 1x Refresco             $50        │
├────────────────────────────────────┤
│ TOTAL: $350                        │
│ 💳 PAGADO CON TARJETA ✅           │
│ ⚠️ NO COBRAR AL ENTREGAR           │
└────────────────────────────────────┘
```

### Llamada (Efectivo)
```
┌────────────────────────────────────┐
│ PEDIDO #124 - Llamada              │
│ Estado: En preparación             │
├────────────────────────────────────┤
│ 👤 Juan Ramírez                    │
│ 📱 477-123-4567                    │
│ 📍 Hidalgo 789, Centro             │
├────────────────────────────────────┤
│ 1x Pizza Pepperoni      $180       │
│ 1x Papas                $70        │
├────────────────────────────────────┤
│ TOTAL: $250                        │
│ 💵 COBRAR EN EFECTIVO ⏳           │
│ ⚠️ COBRAR AL ENTREGAR              │
└────────────────────────────────────┘
```

---

## ✅ CONCLUSIÓN

### **El módulo de repartidor SÍ está bien diseñado**

1. ✅ Tiene su propio controlador: `MisEnviosController`
2. ✅ Ve solo sus pedidos asignados
3. ✅ Ve TODOS los datos del cliente (sin importar el origen)
4. ✅ Puede actualizar estados
5. ✅ Ve estadísticas de desempeño

### **NO hay diferencia para el repartidor entre App y Llamada**

La única diferencia es:
- **App/Tarjeta** → Ya pagado, solo entrega
- **Llamada/Efectivo** → Cobra al entregar

**Ambos casos muestran:**
- ✅ Nombre del cliente
- ✅ Teléfono
- ✅ Dirección completa
- ✅ Productos
- ✅ Total
- ✅ Observaciones

### **El campo `metodoPago` en el pedido indica:**
- `Tarjeta` / `Plataforma` → Ya pagado
- `Efectivo` / `Transferencia` → Cobrar al entregar

---

## 🎯 RECOMENDACIONES PARA LA UI

### Dashboard del Repartidor
```
┌────────────────────────────────────┐
│ 🏍️ MI ESTADO                      │
│ Disponible                         │
│ [CAMBIAR A NO DISPONIBLE]          │
├────────────────────────────────────┤
│ 📊 HOY                             │
│ Entregados: 6                      │
│ En camino: 1                       │
│ Pendientes: 2                      │
│ Total ganado: $2,100               │
├────────────────────────────────────┤
│ 📦 PEDIDOS PENDIENTES              │
│                                     │
│ [CARD PEDIDO #123]                 │
│ [CARD PEDIDO #124]                 │
└────────────────────────────────────┘
```

### Card de Pedido
```
┌────────────────────────────────────┐
│ #123 - María González              │
│ 📍 Juárez 123, Centro              │
│ 💵 $350 - Efectivo                 │
│ 🕐 Hace 15 min                     │
│                                     │
│ [📞 LLAMAR] [🗺️ VER MAPA]         │
│ [👁️ DETALLES] [🏍️ RECOGER]       │
└────────────────────────────────────┘
```

¡Tu sistema está bien diseñado! 🎉
