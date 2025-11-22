# 🧪 GUÍA DE PRUEBAS MANUALES - PizzaHub API
## ⚡ VERSIÓN CRONOLÓGICA (ORDEN CORRECTO)

> **🎯 IMPORTANTE:** Esta guía está ordenada **cronológicamente**. 
> Sigue el orden exacto para evitar errores de referencias (foreign keys).

---

## 📋 ORDEN DE PRUEBAS

**FASE 1:** Configuración Inicial (Usuarios y Roles) → Crea las cuentas base  
**FASE 2:** Catálogos (Productos e Insumos) → Registra lo que vendes  
**FASE 3:** Operación Diaria (Caja) → Abre la caja del día  
**FASE 4:** Pedidos y Ventas → Flujos completos de negocio  
**FASE 5:** Módulo Repartidor → Gestión de entregas  
**FASE 6:** Inventario → Compras y stock  
**FASE 7:** Cierre → Cerrar caja y reportes  

---

## 🛠️ PREPARACIÓN

### 1. **Iniciar el servidor**
```powershell
cd c:\UTL\PizzaHub\PizzaHubAPI\PizzaHubAPI
dotnet run
```

✅ Deberías ver: `Now listening on: https://localhost:7188`

### 2. **Abrir Swagger**
`https://localhost:7188/swagger`

### 3. **Actualizar Base de Datos** (si no lo has hecho)
```powershell
# En la Consola del Administrador de Paquetes de Visual Studio
Remove-Migration  # Si hay una migración fallida
Add-Migration CambiarTokenPorHash
Update-Database
```

### 4. **Preparar bloc de notas** 
Vas a guardar muchos IDs y tokens

---

# 🎯 FASE 1: CONFIGURACIÓN INICIAL

## Objetivo: Crear usuarios y asignar roles correctamente

---

### ✅ TEST 1: Registrar Usuario Administrador

```http
POST /api/v1/auth/register

{
  "email": "admin@pizzahub.com",
  "password": "Admin123!",
  "nombreUsuario": "AdminPizza",
  "telefonoContacto": "4771111111"
}
```

**✅ Resultado esperado:**
- Status: 200 OK
- Devuelve `accessToken` y `refreshToken`
- Rol actual: "Cliente" (lo cambiaremos)

**📝 GUARDAR:**
```
ADMIN
- Email: admin@pizzahub.com
- Usuario ID: (observa en la respuesta, ej: 1)
- Token: {copia el accessToken completo}
```

---

### ✅ TEST 2: Cambiar Rol a Administrador

> ✅ **NUEVO:** Ahora puedes cambiar roles usando el endpoint (ideal para Render.com)

**Opción 1: Usando el endpoint (Recomendado para Render):**
```http
PUT /api/v1/auth/cambiar-rol
Authorization: Bearer {token_del_test_1}

{
  "usuarioId": 1,
  "nuevoRol": "Administrador"
}
```

**Opción 2: Usando MySQL Workbench / phpMyAdmin:**
```sql
UPDATE usuarios SET Rol = 'Administrador' WHERE Correo = 'admin@pizzahub.com';
```

**Verificar con nuevo login:**
```http
POST /api/v1/auth/login

{
  "email": "admin@pizzahub.com",
  "password": "Admin123!"
}
```

**✅ Verifica que `roles` en la respuesta sea:**
```json
{
  "roles": ["Administrador"]
}
```

**📝 ACTUALIZAR token guardado** (ahora con rol Administrador)

---

### ✅ TEST 3: Registrar Usuario Empleado

```http
POST /api/v1/auth/register

{
  "email": "empleado1@pizzahub.com",
  "password": "Empleado123!",
  "nombreUsuario": "EmpleadoJuan",
  "telefonoContacto": "4772222222"
}
```

**📝 GUARDAR:**
```
EMPLEADO
- Email: empleado1@pizzahub.com
- Usuario ID: (ej: 2)
```

**Cambiar rol usando el endpoint (usa el token del admin):**
```http
PUT /api/v1/auth/cambiar-rol
Authorization: Bearer {token_administrador}

{
  "usuarioId": 2,
  "nuevoRol": "Empleado"
}
```

**Login y guardar token:**
```http
POST /api/v1/auth/login

{
  "email": "empleado1@pizzahub.com",
  "password": "Empleado123!"
}
```

**📝 GUARDAR token del empleado**

---

### ✅ TEST 4: Registrar Usuario Repartidor

```http
POST /api/v1/auth/register

{
  "email": "repartidor1@pizzahub.com",
  "password": "Repartidor123!",
  "nombreUsuario": "RepartidorCarlos",
  "telefonoContacto": "4773333333"
}
```

**📝 GUARDAR:**
```
REPARTIDOR
- Email: repartidor1@pizzahub.com
- Usuario ID: (ej: 3)
```

**Cambiar rol usando el endpoint (usa el token del admin):**
```http
PUT /api/v1/auth/cambiar-rol
Authorization: Bearer {token_administrador}

{
  "usuarioId": 3,
  "nuevoRol": "Repartidor"
}
```

**Login y guardar token:**
```http
POST /api/v1/auth/login

{
  "email": "repartidor1@pizzahub.com",
  "password": "Repartidor123!"
}
```

**📝 GUARDAR token del repartidor**

---

### ✅ TEST 5: Registrar Cliente Normal

```http
POST /api/v1/auth/register

{
  "email": "cliente1@gmail.com",
  "password": "Cliente123!",
  "nombreUsuario": "ClienteMaría",
  "telefonoContacto": "4774444444"
}
```

**✅ Resultado:**
- Rol: Cliente (✅ correcto, no cambiar)
- Se crea automáticamente en tabla `clientes`

**📝 GUARDAR:**
```
CLIENTE
- Email: cliente1@gmail.com
- Usuario ID: (ej: 4)
- Token: {accessToken}
```

---

### ✅ TEST 6: Crear Perfil de Empleado

> ⚠️ IMPORTANTE: Esto vincula al usuario con datos laborales

```http
POST /api/Empleados
Authorization: Bearer {token_administrador}

{
  "nombre": "Juan",
  "apellidos": "Pérez López",
  "telefono": "4772222222",
  "usuarioId": 2
}
```

**✅ Resultado esperado:**
- Status: 201 Created
- Devuelve `id` del empleado

**📝 GUARDAR:**
```
EMPLEADO PERFIL
- Empleado ID: (ej: 1) ⚠️ ESTE ID ES IMPORTANTE
- Vinculado a Usuario ID: 2
```

---

### ✅ TEST 7: Crear Perfil de Repartidor

```http
POST /api/Repartidores
Authorization: Bearer {token_administrador}

{
  "nombre": "Carlos",
  "apellidos": "Ramírez González",
  "telefono": "4773333333",
  "usuarioId": 3
}
```

**✅ Resultado esperado:**
- Status: 201 Created
- Estado: Disponible
- Activo: true

**📝 GUARDAR:**
```
REPARTIDOR PERFIL
- Repartidor ID: (ej: 1) ⚠️ ESTE ID ES IMPORTANTE
- Vinculado a Usuario ID: 3
```

---

### 📊 RESUMEN FASE 1

| Rol | Email | Usuario ID | Empleado/Repartidor ID | Token |
|-----|-------|-----------|------------------------|-------|
| Administrador | admin@pizzahub.com | 1 | - | ✅ |
| Empleado | empleado1@pizzahub.com | 2 | Empleado: 1 | ✅ |
| Repartidor | repartidor1@pizzahub.com | 3 | Repartidor: 1 | ✅ |
| Cliente | cliente1@gmail.com | 4 | Cliente: auto | ✅ |

---

# 📦 FASE 2: CATÁLOGOS (Productos e Insumos)

## Objetivo: Registrar lo que vendes y los insumos necesarios

---

### ✅ TEST 8: Registrar Productos

```http
POST /api/Productos
Authorization: Bearer {token_administrador}

{
  "nombre": "Pizza Pepperoni Grande",
  "descripcion": "Pizza de 40cm con pepperoni",
  "tipo": "Pizza",
  "precio": 180.00,
  "almacenable": false,
  "imagenUrl": null
}
```

**Repetir para más productos:**

```json
{
  "nombre": "Refresco 600ml",
  "descripcion": "Coca-Cola 600ml",
  "tipo": "Bebida",
  "precio": 25.00,
  "almacenable": true,
  "imagenUrl": null
}
```

```json
{
  "nombre": "Pizza Hawaiana Mediana",
  "descripcion": "Pizza de 30cm con piña y jamón",
  "tipo": "Pizza",
  "precio": 150.00,
  "almacenable": false,
  "imagenUrl": null
}
```

**📝 GUARDAR IDs de productos:**
```
- Producto 1 (Pepperoni): id = 1
- Producto 2 (Refresco): id = 2
- Producto 3 (Hawaiana): id = 3
```

---

### ✅ TEST 9: Registrar Insumos con Stock Inicial

```http
POST /api/Insumos
Authorization: Bearer {token_administrador}

{
  "nombre": "Harina 00",
  "unidadMedida": 0,
  "stockInicial": 50.0,
  "stockMinimo": 20.0
}
```

**Más insumos:**

```json
{
  "nombre": "Queso Mozzarella",
  "unidadMedida": 0,
  "stockInicial": 30.0,
  "stockMinimo": 10.0
}
```

```json
{
  "nombre": "Pepperoni",
  "unidadMedida": 0,
  "stockInicial": 15.0,
  "stockMinimo": 5.0
}
```

**✅ Resultado esperado:**
- `stockActual` = `stockInicial`
- Se crea log de inventario automáticamente

**📝 GUARDAR IDs de insumos:**
```
- Insumo 1 (Harina): id = 1
- Insumo 2 (Queso): id = 2
- Insumo 3 (Pepperoni): id = 3
```

---

### 📊 RESUMEN FASE 2

- ✅ Al menos 3 productos disponibles
- ✅ Al menos 3 insumos con stock
- ✅ Logs de inventario creados

---

# 💰 FASE 3: OPERACIÓN DIARIA (Abrir Caja)

## Objetivo: Abrir la caja para poder registrar ventas

---

### ✅ TEST 10: Abrir Caja del Día

> ⚠️ **MUY IMPORTANTE:** Usa el `empleadoId` (ej: 1), NO el `usuarioId`

```http
POST /api/Caja/abrir
Authorization: Bearer {token_empleado}

{
  "saldoInicial": 500.00,
  "empleadoId": 1
}
```

**✅ Resultado esperado:**
- Status: 201 Created
- Estado: Abierta
- Fecha: Hoy
- Saldo inicial: 500.00

**📝 GUARDAR:**
```
CAJA
- Caja ID: (ej: 1) ⚠️ IMPORTANTE para registrar ventas
```

**Validar:**
- ❌ Intentar abrir otra caja debe fallar

---

### ✅ TEST 11: Verificar Caja Abierta

```http
GET /api/Caja/abierta
Authorization: Bearer {token_empleado}
```

**✅ Resultado:**
- Muestra la caja actual con todos sus datos

---

# 🍕 FASE 4: PEDIDOS Y VENTAS

## Objetivo: Probar todos los flujos de pedidos

---

## 📋 ESCENARIO 1: Pedido en Mostrador (Sin cliente)

### ✅ TEST 12: Crear Pedido en Mostrador

```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token_empleado}

{
  "clienteId": null,
  "tipo": 0,
  "metodoPago": 0,
  "origen": 3,
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

**✅ Resultado esperado:**
- Status: 201 Created
- Estado: Pendiente
- Total calculado automáticamente (180*2 + 25 = 385)
- clienteId: null

**📝 GUARDAR:**
```
PEDIDO MOSTRADOR
- Pedido ID: (ej: 1)
- Total: 385.00
```

---

### ✅ TEST 13: Registrar Venta del Pedido

```http
POST /api/Ventas
Authorization: Bearer {token_empleado}

{
  "cajaId": 1,
  "pedidoId": 1,
  "empleadoId": 1,
  "metodoPago": 0,
  "total": 385.00
}
```

**✅ Resultado esperado:**
- Status: 201 Created
- Venta vinculada a caja y pedido

**Validar:**
- ❌ Registrar venta sin caja abierta debe fallar

---

## 📋 ESCENARIO 2: Pedido con Envío (Cliente registrado)

### ✅ TEST 14: Actualizar Perfil del Cliente

```http
PUT /api/Clientes/mi-perfil
Authorization: Bearer {token_cliente}

{
  "nombre": "María",
  "apellidos": "González López",
  "telefono": "4774444444",
  "colonia": "Centro",
  "calle": "Juárez",
  "numeroCasa": "123",
  "observaciones": "Casa azul con portón blanco"
}
```

**✅ Resultado:**
- Status: 204 No Content
- Perfil actualizado

---

### ✅ TEST 15: Crear Pedido con Envío

> El empleado registra el pedido por teléfono

```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token_empleado}

{
  "clienteId": 4,
  "tipo": 2,
  "metodoPago": 0,
  "origen": 1,
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

**✅ Resultado esperado:**
- Estado: Pendiente
- ClienteId: 4
- Total: 180 + (150*2) = 480

**📝 GUARDAR:**
```
PEDIDO CON ENVÍO
- Pedido ID: (ej: 2)
```

---

### ✅ TEST 16: Asignar Repartidor

> Usa el repartidorId del TEST 7

```http
PUT /api/PedidosNew/2/asignar-repartidor
Authorization: Bearer {token_empleado}

{
  "repartidorId": 1
}
```

**✅ Resultado esperado:**
- Status: 204 No Content
- Pedido cambia a "EnPreparacion"
- Repartidor cambia a "Ocupado"

---

## 📋 ESCENARIO 3: Pedido desde App (Pago con Tarjeta)

### ✅ TEST 17: Pedido desde App Móvil

```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token_cliente}

{
  "clienteId": 4,
  "tipo": 4,
  "metodoPago": 1,
  "origen": 0,
  "direccionEntrega": "Calle Hidalgo 456, Centro",
  "observaciones": "Dejar en portería",
  "detalles": [
    {
      "productoId": 2,
      "cantidad": 3
    }
  ]
}
```

**✅ Resultado:**
- Tipo: App
- Método de pago: Tarjeta (ya pagado)
- Estado: Pendiente

**📝 GUARDAR:** Pedido ID (ej: 3)

---

## 📋 ESCENARIO 4: Pedido de Plataforma (Uber Eats)

### ✅ TEST 18: Pedido desde Plataforma

```http
POST /api/PedidosNew/registrar
Authorization: Bearer {token_empleado}

{
  "clienteId": null,
  "tipo": 3,
  "metodoPago": 2,
  "origen": 2,
  "direccionEntrega": "Dirección de plataforma",
  "observaciones": "Uber Eats #UE123456",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2
    }
  ]
}
```

**✅ Resultado:**
- Cliente: null (el cliente es de la plataforma)
- Tipo: Plataforma

**📝 GUARDAR:** Pedido ID (ej: 4)

---

# 🏍️ FASE 5: MÓDULO REPARTIDOR

## Objetivo: Probar el flujo completo del repartidor

> ⚠️ Usa el token del repartidor para todas estas pruebas

---

### ✅ TEST 19: Ver Mi Perfil de Repartidor

```http
GET /api/MisEnvios/mi-perfil
Authorization: Bearer {token_repartidor}
```

**✅ Resultado:**
- Muestra datos del repartidor autenticado

---

### ✅ TEST 20: Ver Pedidos Pendientes

```http
GET /api/MisEnvios/pendientes
Authorization: Bearer {token_repartidor}
```

**✅ Resultado:**
- Solo muestra pedidos asignados a este repartidor
- Estados: Pendiente o EnPreparacion

---

### ✅ TEST 21: Ver Detalle de un Pedido

```http
GET /api/MisEnvios/pedido/2
Authorization: Bearer {token_repartidor}
```

**✅ Resultado:**
- Muestra TODOS los datos del cliente
- Muestra productos del pedido
- Indica si debe cobrar (método pago: Efectivo)

**Validar:**
- ❌ Ver pedido NO asignado debe fallar

---

### ✅ TEST 22: Recoger Pedido

```http
PUT /api/MisEnvios/pedido/2/recoger
Authorization: Bearer {token_repartidor}
```

**✅ Resultado:**
- Pedido cambia a "EnCamino"
- Repartidor queda "Ocupado"
- Mensaje: "Pedido recogido, en camino al cliente"

---

### ✅ TEST 23: Marcar como Entregado

```http
PUT /api/MisEnvios/pedido/2/entregar
Authorization: Bearer {token_repartidor}
```

**✅ Resultado:**
- Pedido cambia a "Entregado"
- Repartidor cambia a "Disponible" (si no tiene más pedidos)

---

### ✅ TEST 24: Ver Historial de Entregas

```http
GET /api/MisEnvios/historial
Authorization: Bearer {token_repartidor}
```

**✅ Resultado:**
- Solo pedidos con estado "Entregado"

---

### ✅ TEST 25: Ver Estadísticas

```http
GET /api/MisEnvios/estadisticas
Authorization: Bearer {token_repartidor}
```

**✅ Resultado:**
- Estadísticas del día
- Estadísticas generales

---

### ✅ TEST 26: Cambiar Disponibilidad

```http
PATCH /api/MisEnvios/disponibilidad
Authorization: Bearer {token_repartidor}

{
  "disponible": false
}
```

**✅ Resultado:**
- Repartidor cambia a "Inactivo"

**Validar:**
- ❌ Cambiar a disponible con pedidos en camino debe fallar

---

# 📦 FASE 6: INVENTARIO (Compras de Insumos)

## Objetivo: Probar el sistema de compras y actualización de stock

---

### ✅ TEST 27: Registrar Compra de Múltiples Insumos

```http
POST /api/ComprasInsumos
Authorization: Bearer {token_empleado}

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

**✅ Resultado esperado:**
- Total calculado automáticamente
- Stock actualizado en cada insumo
- Logs de inventario creados

---

### ✅ TEST 28: Ver Insumos con Bajo Stock

```http
GET /api/Insumos/bajo-stock
Authorization: Bearer {token_empleado}
```

**✅ Resultado:**
- Insumos donde StockActual <= StockMinimo

---

### ✅ TEST 29: Ver Historial de Compras

```http
GET /api/ComprasInsumos
Authorization: Bearer {token_empleado}
```

**✅ Resultado:**
- Todas las compras registradas

---

# 💰 FASE 7: CIERRE DE CAJA

## Objetivo: Cerrar el día y obtener resumen

---

### ✅ TEST 30: Ver Resumen de Caja

```http
GET /api/Caja/1/resumen
Authorization: Bearer {token_empleado}
```

**✅ Resultado:**
- Total de ventas
- Ventas por método de pago
- Cantidad de ventas

---

### ✅ TEST 31: Cerrar Caja

```http
POST /api/Caja/1/cerrar
Authorization: Bearer {token_empleado}

{
  "saldoFinal": 1365.00
}
```

**✅ Resultado esperado:**
- Caja cambia a "Cerrada"
- Devuelve resumen completo
- Muestra diferencia si hay

**Validar:**
- ❌ Cerrar caja ya cerrada debe fallar
- ❌ Registrar ventas en caja cerrada debe fallar

---

# ✅ CHECKLIST FINAL

## Funcionalidades Probadas

### Autenticación
- [ ] Registro de usuarios
- [ ] Login con diferentes roles
- [ ] Refresh token
- [ ] Logout

### Configuración
- [ ] Crear empleados
- [ ] Crear repartidores
- [ ] Registrar productos
- [ ] Registrar insumos con stock

### Operación
- [ ] Abrir caja
- [ ] Pedido en mostrador
- [ ] Pedido con envío
- [ ] Pedido desde app
- [ ] Pedido de plataforma
- [ ] Registrar ventas
- [ ] Cerrar caja

### Repartidor
- [ ] Ver pedidos asignados
- [ ] Recoger pedido
- [ ] Entregar pedido
- [ ] Ver historial
- [ ] Estadísticas
- [ ] Cambiar disponibilidad

### Inventario
- [ ] Comprar múltiples insumos
- [ ] Stock se actualiza automáticamente
- [ ] Ver bajo stock
- [ ] Logs de inventario

---

# 🎉 ¡FELICIDADES!

Si completaste todas las pruebas sin errores, tu sistema está funcionando correctamente.

## Próximos pasos:

1. **Producción:** Configura variables de entorno
2. **Seguridad:** Cambia las contraseñas por defecto
3. **Backup:** Configura respaldos automáticos
4. **Monitoreo:** Implementa logging de errores

---

## 🐛 REPORTE DE BUGS

Si encuentras errores, anota:

```
Test #: ___
Endpoint: ___________
Request:
___________

Response:
___________

Error:
___________
```

---

**Última actualización:** 16 de Noviembre de 2025  
**Versión:** 2.0 (Cronológica)
