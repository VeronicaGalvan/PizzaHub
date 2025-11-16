# 🧪 GUÍA COMPLETA DE PRUEBAS MANUALES - PizzaHub API

## 📋 ÍNDICE
1. [Preparación del Entorno](#preparación)
2. [Pruebas de Autenticación](#autenticación)
3. [Pruebas de Gestión de Caja](#caja)
4. [Pruebas de Pedidos y Ventas](#pedidos-ventas)
5. [Pruebas del Módulo Repartidor](#repartidor)
6. [Pruebas de Inventario e Insumos](#inventario)
7. [Checklist de Validación](#checklist)

---

## 🛠️ PREPARACIÓN DEL ENTORNO {#preparación}

### 1. **Verificar que el servidor esté corriendo**
```bash
cd c:\UTL\PizzaHub\PizzaHubAPI\PizzaHubAPI
dotnet run
```

✅ Deberías ver: `Now listening on: https://localhost:7188`

### 2. **Abrir Swagger**
Abre tu navegador en: `https://localhost:7188/swagger`

### 3. **Herramientas recomendadas**
- 🔧 **Swagger UI** (incluida en tu proyecto)
- 📮 **Postman** (opcional, para pruebas más avanzadas)
- 📝 **Bloc de notas** (para guardar tokens y IDs)

---

## 🔐 PRUEBAS DE AUTENTICACIÓN {#autenticación}

### ✅ **TEST 1: Registrar Usuarios de Diferentes Roles**

#### **1.1 Registrar Administrador**
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "admin@pizzahub.com",
  "password": "Admin123!",
  "nombreUsuario": "AdminPizza",
  "telefonoContacto": "4771234567"
}
```

**Resultado esperado:** 
- ✅ Status: 201 Created
- ✅ Devuelve tokens (accessToken, refreshToken)
- ✅ Rol: Cliente (por defecto)

> ⚠️ **NOTA:** Los usuarios se registran como "Cliente" por defecto. Para crear Administrador/Empleado/Repartidor, debes:
> 1. Cambiar el rol en la base de datos manualmente, O
> 2. Crear un endpoint de administración para cambiar roles

#### **1.2 Registrar Cliente**
```http
POST /api/v1/auth/register

{
  "email": "cliente1@gmail.com",
  "password": "Cliente123!",
  "nombreUsuario": "ClienteJuan",
  "telefonoContacto": "4779876543"
}
```

#### **1.3 Registrar Repartidor**
```http
POST /api/v1/auth/register

{
  "email": "repartidor1@pizzahub.com",
  "password": "Repartidor123!",
  "nombreUsuario": "RepartidorCarlos",
  "telefonoContacto": "4775554321"
}
```

**📝 GUARDAR:**
- Los `accessToken` de cada usuario
- Los `id` de cada usuario

---

### ✅ **TEST 2: Login**

```http
POST /api/v1/auth/login

{
  "email": "admin@pizzahub.com",
  "password": "Admin123!"
}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Devuelve accessToken y refreshToken
- ✅ Muestra el rol del usuario

**📝 COPIAR el accessToken** para usarlo en las siguientes pruebas

---

### ✅ **TEST 3: Refresh Token**

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "{tu_refresh_token}"
}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Devuelve nuevo accessToken

---

### ✅ **TEST 4: Logout**

```http
POST /api/v1/auth/logout
Authorization: Bearer {tu_token}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Mensaje: "Sesión cerrada correctamente"

---

## 💰 PRUEBAS DE GESTIÓN DE CAJA {#caja}

> ⚠️ **IMPORTANTE:** Usa el token de un usuario con rol Empleado o Administrador

### ✅ **TEST 5: Abrir Caja del Día**

```http
POST /api/Caja/abrir
Authorization: Bearer {token_empleado}
Content-Type: application/json

{
  "saldoInicial": 500.00,
  "empleadoId": 1
}
```

**Resultado esperado:**
- ✅ Status: 201 Created
- ✅ Se crea caja con estado "Abierta"
- ✅ Fecha de hoy
- ✅ Saldo inicial: 500.00

**📝 GUARDAR el `id` de la caja**

**Validaciones a probar:**
- ❌ Intentar abrir otra caja (debería fallar: "Ya existe una caja abierta")
- ❌ Intentar abrir caja sin empleadoId

---

### ✅ **TEST 6: Ver Caja Abierta**

```http
GET /api/Caja/abierta
Authorization: Bearer {token_empleado}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Muestra la caja actualmente abierta

---

## 🍕 PRUEBAS DE PEDIDOS Y VENTAS {#pedidos-ventas}

### 📦 **ESCENARIO 1: Pedido en Mostrador (Cliente sin cuenta)**

#### **TEST 7: Crear Pedido en Mostrador**

```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token}
Content-Type: application/json

{
  "clienteId": null,
  "tipo": "Mostrador",
  "metodoPago": "Efectivo",
  "origen": "Mostrador",
  "direccionEntrega": null,
  "observaciones": "Cliente sin cuenta",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2
    },
    {
      "productoId": 2,
      "cantidad": 1
    }
  ]
}
```

**Resultado esperado:**
- ✅ Status: 201 Created
- ✅ Estado: Pendiente
- ✅ Calcula el total automáticamente
- ✅ clienteId: null

**📝 GUARDAR el `id` del pedido**

---

#### **TEST 8: Registrar Venta del Pedido**

```http
POST /api/Ventas
Authorization: Bearer {token_empleado}
Content-Type: application/json

{
  "cajaId": 1,
  "pedidoId": 1,
  "empleadoId": 1,
  "metodoPago": "Efectivo",
  "total": 350.00
}
```

**Resultado esperado:**
- ✅ Status: 201 Created
- ✅ La venta se vincula al pedido
- ✅ Se registra en la caja abierta

**Validaciones a probar:**
- ❌ Intentar registrar venta sin caja abierta
- ❌ Intentar registrar venta con cajaId inexistente

---

### 📦 **ESCENARIO 2: Pedido por Teléfono con Envío (Cliente con cuenta)**

#### **TEST 9: Crear Perfil de Cliente**

```http
PUT /api/Clientes/mi-perfil
Authorization: Bearer {token_cliente}
Content-Type: application/json

{
  "nombre": "María",
  "apellidos": "González López",
  "telefono": "4779876543",
  "colonia": "Centro",
  "calle": "Juárez",
  "numeroCasa": "123",
  "observaciones": "Casa azul con portón blanco"
}
```

**Resultado esperado:**
- ✅ Status: 204 No Content
- ✅ Se crea o actualiza el perfil

---

#### **TEST 10: Crear Pedido con Envío**

```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token_empleado}
Content-Type: application/json

{
  "clienteId": 1,
  "tipo": "LlamadaEnvio",
  "metodoPago": "Efectivo",
  "origen": "Llamada",
  "direccionEntrega": "Calle Juárez 123, Centro",
  "observaciones": "Sin cebolla",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 1
    },
    {
      "productoId": 3,
      "cantidad": 2
    }
  ]
}
```

**Resultado esperado:**
- ✅ Status: 201 Created
- ✅ Estado: Pendiente
- ✅ Tiene clienteId
- ✅ Tiene dirección de entrega

**📝 GUARDAR el `id` del pedido**

---

#### **TEST 11: Asignar Repartidor al Pedido**

> ⚠️ Primero debes crear un repartidor

**11.1 Crear Repartidor**
```http
POST /api/Repartidores
Authorization: Bearer {token_admin}
Content-Type: application/json

{
  "nombre": "Carlos",
  "apellidos": "Ramírez",
  "telefono": "4775554321",
  "usuarioId": 3
}
```

**11.2 Asignar Repartidor**
```http
PUT /api/PedidosNew/2/asignar-repartidor
Authorization: Bearer {token_empleado}
Content-Type: application/json

{
  "repartidorId": 1
}
```

**Resultado esperado:**
- ✅ Status: 204 No Content
- ✅ El pedido cambia a "EnPreparacion"
- ✅ El repartidor cambia a "Ocupado"

**Validaciones a probar:**
- ❌ Asignar repartidor no disponible (debería fallar)
- ❌ Asignar repartidor inexistente

---

### 📦 **ESCENARIO 3: Pedido desde App Móvil**

#### **TEST 12: Pedido desde App (Pagado con Tarjeta)**

```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token_cliente}
Content-Type: application/json

{
  "clienteId": 1,
  "tipo": "App",
  "metodoPago": "Tarjeta",
  "origen": "App",
  "direccionEntrega": "Calle Hidalgo 456, Col. Centro",
  "observaciones": "Dejar en portería",
  "detalles": [
    {
      "productoId": 2,
      "cantidad": 3
    }
  ]
}
```

**Resultado esperado:**
- ✅ Status: 201 Created
- ✅ Tipo: App
- ✅ Método de pago: Tarjeta
- ✅ Estado: Pendiente

**📝 Nota:** En producción, aquí se integraría con pasarela de pagos

---

### 📦 **ESCENARIO 4: Pedido de Plataforma (Uber Eats, Rappi)**

#### **TEST 13: Pedido desde Plataforma**

```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token_empleado}
Content-Type: application/json

{
  "clienteId": null,
  "tipo": "Plataforma",
  "metodoPago": "Plataforma",
  "origen": "Plataforma",
  "direccionEntrega": "Dirección proporcionada por plataforma",
  "observaciones": "Pedido de Uber Eats #UE123456",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2
    }
  ]
}
```

**Resultado esperado:**
- ✅ Status: 201 Created
- ✅ Tipo: Plataforma
- ✅ Cliente: null (el cliente es de la plataforma)

---

## 🏍️ PRUEBAS DEL MÓDULO REPARTIDOR {#repartidor}

> ⚠️ **IMPORTANTE:** Usa el token del usuario con rol Repartidor

### ✅ **TEST 14: Ver Mi Perfil de Repartidor**

```http
GET /api/MisEnvios/mi-perfil
Authorization: Bearer {token_repartidor}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Muestra datos del repartidor autenticado

---

### ✅ **TEST 15: Ver Mis Pedidos Pendientes**

```http
GET /api/MisEnvios/pendientes
Authorization: Bearer {token_repartidor}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Muestra solo pedidos asignados a este repartidor
- ✅ Estados: Pendiente o EnPreparacion

---

### ✅ **TEST 16: Ver Detalles de un Pedido**

```http
GET /api/MisEnvios/pedido/2
Authorization: Bearer {token_repartidor}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Muestra todos los datos del cliente
- ✅ Muestra productos del pedido
- ✅ Indica si debe cobrar o no

**Validaciones a probar:**
- ❌ Intentar ver pedido no asignado (debería fallar)

---

### ✅ **TEST 17: Recoger Pedido**

```http
PUT /api/MisEnvios/pedido/2/recoger
Authorization: Bearer {token_repartidor}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Pedido cambia a "EnCamino"
- ✅ Repartidor cambia a "Ocupado"
- ✅ Mensaje: "Pedido recogido, en camino al cliente"

**Validaciones a probar:**
- ❌ Recoger pedido que no está en preparación

---

### ✅ **TEST 18: Marcar como Entregado**

```http
PUT /api/MisEnvios/pedido/2/entregar
Authorization: Bearer {token_repartidor}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Pedido cambia a "Entregado"
- ✅ Repartidor cambia a "Disponible" (si no tiene más pedidos)
- ✅ Mensaje: "Pedido entregado exitosamente"

---

### ✅ **TEST 19: Ver Historial de Entregas**

```http
GET /api/MisEnvios/historial
Authorization: Bearer {token_repartidor}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Muestra solo pedidos entregados

---

### ✅ **TEST 20: Ver Estadísticas**

```http
GET /api/MisEnvios/estadisticas
Authorization: Bearer {token_repartidor}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Muestra estadísticas del día
- ✅ Muestra estadísticas generales

---

### ✅ **TEST 21: Cambiar Disponibilidad**

```http
PATCH /api/MisEnvios/disponibilidad
Authorization: Bearer {token_repartidor}
Content-Type: application/json

{
  "disponible": false
}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Repartidor cambia a "Inactivo"

**Validaciones a probar:**
- ❌ Cambiar a disponible mientras tenga pedidos en camino

---

## 📦 PRUEBAS DE INVENTARIO E INSUMOS {#inventario}

### ✅ **TEST 22: Registrar Insumo con Stock Inicial**

```http
POST /api/Insumos
Authorization: Bearer {token_empleado}
Content-Type: application/json

{
  "nombre": "Harina 00",
  "unidadMedida": "Kg",
  "stockInicial": 50.0,
  "stockMinimo": 20.0
}
```

**Resultado esperado:**
- ✅ Status: 201 Created
- ✅ StockActual: 50.0
- ✅ Se crea log de inventario automáticamente

**📝 GUARDAR el `id` del insumo**

---

### ✅ **TEST 23: Registrar Compra de Múltiples Insumos**

```http
POST /api/ComprasInsumos
Authorization: Bearer {token_empleado}
Content-Type: application/json

{
  "proveedor": "Distribuidora La Mejor",
  "numeroFactura": "FAC-001234",
  "observaciones": "Entrega urgente",
  "detalles": [
    {
      "insumoId": 1,
      "cantidad": 25.0,
      "precioUnitario": 35.50
    },
    {
      "insumoId": 2,
      "cantidad": 10.0,
      "precioUnitario": 85.00
    }
  ]
}
```

**Resultado esperado:**
- ✅ Status: 201 Created
- ✅ Calcula el total automáticamente
- ✅ Actualiza stock de cada insumo
- ✅ Crea logs de inventario

**Validaciones a probar:**
- ❌ Compra sin detalles
- ❌ Compra con insumoId inexistente

---

### ✅ **TEST 24: Ver Insumos con Bajo Stock**

```http
GET /api/Insumos/bajo-stock
Authorization: Bearer {token_empleado}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Muestra insumos donde StockActual <= StockMinimo

---

### ✅ **TEST 25: Ver Historial de Compras**

```http
GET /api/ComprasInsumos
Authorization: Bearer {token_empleado}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Muestra todas las compras registradas

---

## 💰 PRUEBAS DE CIERRE DE CAJA

### ✅ **TEST 26: Ver Resumen de Caja**

```http
GET /api/Caja/1/resumen
Authorization: Bearer {token_empleado}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Muestra total de ventas
- ✅ Muestra ventas por método de pago
- ✅ Muestra cantidad de ventas

---

### ✅ **TEST 27: Cerrar Caja**

```http
POST /api/Caja/1/cerrar
Authorization: Bearer {token_empleado}
Content-Type: application/json

{
  "saldoFinal": 3850.00
}
```

**Resultado esperado:**
- ✅ Status: 200 OK
- ✅ Caja cambia a "Cerrada"
- ✅ Devuelve resumen completo
- ✅ Muestra diferencia si hay

**Validaciones a probar:**
- ❌ Cerrar caja ya cerrada
- ❌ Intentar registrar ventas en caja cerrada

---

## ✅ CHECKLIST DE VALIDACIÓN FINAL {#checklist}

### **Flujo Completo de Venta**
- [ ] Se puede abrir caja
- [ ] Se pueden crear pedidos de diferentes tipos
- [ ] Se pueden asignar repartidores
- [ ] El repartidor puede gestionar sus entregas
- [ ] Se registran ventas correctamente
- [ ] Se puede cerrar caja con resumen

### **Seguridad**
- [ ] Los endpoints protegidos requieren autenticación
- [ ] Los roles se respetan (Cliente no puede acceder a admin)
- [ ] El logout funciona correctamente
- [ ] El refresh token funciona

### **Repartidor**
- [ ] Solo ve sus pedidos
- [ ] Puede actualizar estados
- [ ] Ve toda la información del cliente
- [ ] No importa si es App o Llamada (ve los mismos datos)

### **Inventario**
- [ ] Se puede registrar insumo con stock inicial
- [ ] Se puede registrar compra de múltiples insumos
- [ ] El stock se actualiza automáticamente
- [ ] Se crean logs de inventario

### **Validaciones**
- [ ] No se puede abrir 2 cajas a la vez
- [ ] No se puede asignar repartidor ocupado
- [ ] No se puede registrar venta sin caja abierta
- [ ] No se puede recoger pedido que no está en preparación

---

## 📊 TABLA DE RESULTADOS

Usa esta tabla para registrar tus pruebas:

| Test # | Descripción | Status | Notas |
|--------|-------------|--------|-------|
| 1 | Registro de usuarios | ⬜ | |
| 2 | Login | ⬜ | |
| 3 | Refresh token | ⬜ | |
| 4 | Logout | ⬜ | |
| 5 | Abrir caja | ⬜ | |
| 6 | Ver caja abierta | ⬜ | |
| 7 | Pedido mostrador | ⬜ | |
| 8 | Registrar venta | ⬜ | |
| 9 | Crear perfil cliente | ⬜ | |
| 10 | Pedido con envío | ⬜ | |
| 11 | Asignar repartidor | ⬜ | |
| 12 | Pedido desde app | ⬜ | |
| 13 | Pedido de plataforma | ⬜ | |
| 14-21 | Módulo repartidor | ⬜ | |
| 22-25 | Inventario e insumos | ⬜ | |
| 26-27 | Cierre de caja | ⬜ | |

**Leyenda:**
- ⬜ Pendiente
- ✅ Pasó
- ❌ Falló

---

## 🐛 REPORTE DE BUGS

Si encuentras algún error, anótalo aquí:

```
Test #: ___
Endpoint: ___________
Request enviado:
___________

Response recibido:
___________

Error esperado vs real:
___________
```

---

## 💡 CONSEJOS FINALES

1. **Usa Swagger** para las pruebas básicas
2. **Guarda los tokens** en un archivo de texto
3. **Anota los IDs** generados (usuarios, cajas, pedidos, etc.)
4. **Prueba en orden** (crea datos antes de usarlos)
5. **Verifica las validaciones** intentando casos inválidos
6. **Revisa la base de datos** después de cada operación importante

---

¡Buena suerte con las pruebas! 🚀
