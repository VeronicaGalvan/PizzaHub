# 📖 Diferencia entre Pedidos y Ventas

## 🍕 PEDIDO (Pedido / Order)

### **Definición**
Un **Pedido** representa la orden operativa de productos que un cliente solicita. Es el proceso completo desde que el cliente ordena hasta que recibe su comida.

### **Propósito Principal**
- Gestionar la preparación de productos
- Controlar el flujo de trabajo (cocina → entrega)
- Hacer seguimiento del estado
- Asignar repartidores
- Registrar observaciones de preparación

### **Estados del Pedido**
```
Pendiente → En preparación → En camino → Entregado
                                      ↘ Cancelado
```

### **Casos de uso**
1. **Pedido para comer en mostrador**
   - Tipo: Mostrador
   - Estado: Pendiente → En preparación → Entregado
   - No requiere repartidor

2. **Pedido con envío a domicilio**
   - Tipo: Llamada-Envío / App
   - Estado: Pendiente → En preparación → En camino → Entregado
   - Requiere repartidor y dirección

3. **Pedido de plataforma (Uber Eats, Rappi, etc.)**
   - Tipo: Plataforma
   - Origen: Plataforma
   - El repartidor es externo

### **Endpoints principales**
```
POST   /api/PedidosNew/registrar          - Crear nuevo pedido
GET    /api/PedidosNew                    - Listar todos los pedidos
GET    /api/PedidosNew/{id}               - Ver un pedido específico
PUT    /api/PedidosNew/{id}/estado        - Actualizar estado
POST   /api/PedidosNew/{id}/asignar       - Asignar repartidor
GET    /api/PedidosNew/estado/{estado}    - Filtrar por estado
GET    /api/PedidosNew/cliente/{id}       - Pedidos de un cliente
```

### **Ejemplo de Request**
```json
POST /api/PedidosNew/registrar
{
  "clienteId": 5,
  "tipo": "LlamadaEnvio",
  "metodoPago": "Efectivo",
  "origen": "Llamada",
  "direccionEntrega": "Calle Juárez 123, Centro",
  "observaciones": "Sin cebolla",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2
    },
    {
      "productoId": 5,
      "cantidad": 1
    }
  ]
}
```

### **Ejemplo de Response**
```json
{
  "id": 123,
  "clienteId": 5,
  "clienteNombre": "Juan Pérez",
  "estado": "Pendiente",
  "tipo": "LlamadaEnvio",
  "total": 350.00,
  "direccionEntrega": "Calle Juárez 123, Centro",
  "fechaPedido": "2025-11-15T10:30:00",
  "detalles": [
    {
      "productoId": 1,
      "productoNombre": "Pizza Hawaiana",
      "cantidad": 2,
      "subtotal": 300.00
    },
    {
      "productoId": 5,
      "productoNombre": "Refresco 600ml",
      "cantidad": 1,
      "subtotal": 50.00
    }
  ]
}
```

---

## 💰 VENTA (Venta / Sale)

### **Definición**
Una **Venta** representa el registro contable/financiero de dinero que entra a la caja. Es el comprobante de que se recibió un pago.

### **Propósito Principal**
- Control de caja diario
- Registro contable de ingresos
- Corte de caja al final del turno
- Auditoría financiera
- Reportes de ventas

### **Características**
- Se crea cuando se recibe el pago
- Está vinculada a una caja (turno de trabajo)
- Puede o no estar vinculada a un pedido
- Registro inmutable (no cambia de estado)

### **Casos de uso**

1. **Venta de un Pedido (Caso más común)**
   ```
   Pedido #123 → Cliente paga → Venta #456
   ```

2. **Venta sin Pedido (Venta directa)**
   ```
   Cliente compra un postre en mostrador
   No genera pedido, solo venta
   ```

3. **Pedido sin Venta (Pendiente de pago)**
   ```
   Pedido registrado pero se pagará al entregar
   ```

### **Endpoints principales**
```
POST   /api/Ventas                    - Registrar una venta
GET    /api/Ventas                    - Listar todas las ventas
GET    /api/Ventas/{id}               - Ver una venta específica
GET    /api/Ventas/caja/{id}          - Ventas de una caja
GET    /api/Ventas/fecha/{fecha}      - Ventas por fecha
GET    /api/Ventas/empleado/{id}      - Ventas por empleado
DELETE /api/Ventas/{id}               - Eliminar venta (solo caja abierta)
```

### **Ejemplo de Request**
```json
POST /api/Ventas
{
  "cajaId": 5,
  "pedidoId": 123,
  "empleadoId": 2,
  "metodoPago": "Efectivo",
  "total": 350.00
}
```

### **Ejemplo de Response**
```json
{
  "id": 456,
  "cajaId": 5,
  "pedidoId": 123,
  "empleadoId": 2,
  "metodoPago": "Efectivo",
  "total": 350.00,
  "fechaVenta": "2025-11-15T10:32:00"
}
```

---

## 🔄 RELACIÓN ENTRE PEDIDO Y VENTA

### **Modelo de datos**
```
PEDIDO (1) ←---(0..n)--- VENTA
```
- Un pedido puede tener 0 o más ventas (normal es 1)
- Una venta puede o no tener un pedido asociado

### **Flujos típicos**

#### **Flujo 1: Pedido en Mostrador (Pago inmediato)**
```
1. Cliente ordena en mostrador
2. Se crea PEDIDO (Estado: Pendiente)
3. Cliente paga inmediatamente
4. Se crea VENTA vinculada al PEDIDO
5. PEDIDO pasa a "En preparación"
6. Se entrega comida
7. PEDIDO pasa a "Entregado"
```

#### **Flujo 2: Pedido a Domicilio (Pago al entregar)**
```
1. Cliente llama para pedir
2. Se crea PEDIDO (Estado: Pendiente)
3. Cocina prepara (Estado: En preparación)
4. Repartidor recoge (Estado: En camino)
5. Repartidor entrega y cobra
6. Se crea VENTA vinculada al PEDIDO
7. PEDIDO pasa a "Entregado"
```

#### **Flujo 3: Venta directa sin pedido**
```
1. Cliente compra producto extra (postre, bebida)
2. No se crea PEDIDO
3. Se crea VENTA sin pedidoId
```

---

## 📊 CONTROL DE CAJA

### **Flujo de Caja Diario**

```
1. Empleado abre CAJA
   POST /api/Caja/abrir
   {
     "saldoInicial": 500.00,
     "empleadoId": 2
   }

2. Durante el día se registran VENTAS
   - Venta 1: $350 (Pedido #123)
   - Venta 2: $200 (Pedido #124)
   - Venta 3: $50  (Sin pedido)
   
3. Al final del turno se cierra CAJA
   POST /api/Caja/{id}/cerrar
   {
     "saldoFinal": 1100.00
   }
   
4. Sistema calcula:
   - Saldo Inicial: $500
   - Total Ventas: $600
   - Saldo Esperado: $1100
   - Saldo Real: $1100
   - Diferencia: $0 ✅
```

---

## 🎯 RESUMEN RÁPIDO

| Aspecto | PEDIDO | VENTA |
|---------|--------|-------|
| **Propósito** | Operativo (cocina/entrega) | Financiero (caja) |
| **Estados** | Múltiples (Pendiente → Entregado) | Uno solo (registrado) |
| **Cambios** | Sí (actualiza estado) | No (inmutable) |
| **Vinculación** | Puede tener ventas | Puede tener pedido |
| **Quién lo usa** | Cocina, Repartidores | Cajeros, Contabilidad |
| **Reportes** | Pedidos por estado, repartidor | Corte de caja, ingresos |

---

## ✅ VALIDACIONES IMPORTANTES

### **Al crear un Pedido:**
- ✅ Debe tener al menos un producto
- ✅ Los productos deben estar activos
- ✅ Se calcula el total automáticamente
- ✅ Si es envío, debe tener dirección

### **Al crear una Venta:**
- ✅ La caja debe estar abierta
- ✅ Si hay pedido, debe existir
- ✅ El total debe ser mayor a 0
- ✅ No se puede eliminar si la caja está cerrada

### **Control de Caja:**
- ✅ Solo puede haber una caja abierta por día
- ✅ Todas las ventas deben estar vinculadas a una caja
- ✅ Una vez cerrada, no se pueden agregar/eliminar ventas

---

## 🚀 BUENAS PRÁCTICAS

### **Para Pedidos:**
1. Siempre actualizar el estado conforme avanza el pedido
2. Asignar repartidor cuando el pedido está listo
3. Agregar observaciones relevantes (alergias, preferencias)
4. Registrar la venta cuando se recibe el pago

### **Para Ventas:**
1. Registrar inmediatamente cuando se recibe el pago
2. Vincular siempre a la caja del turno actual
3. Especificar el método de pago correcto
4. Si es posible, vincular al pedido correspondiente

### **Para Control:**
1. Abrir caja al inicio del turno
2. Cerrar caja al final del turno
3. Verificar que saldo real = saldo esperado
4. Generar reportes diarios
