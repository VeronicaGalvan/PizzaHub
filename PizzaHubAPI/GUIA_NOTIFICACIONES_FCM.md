# Sistema de Notificaciones con FCM - PizzaHub

## Resumen de Implementación

Se ha implementado un sistema completo de notificaciones push usando Firebase Cloud Messaging (FCM) para enviar notificaciones a los clientes de la aplicación móvil.

## Archivos Creados/Modificados

### 1. Nuevo Modelo: `Notificacion.cs`
- Tabla para almacenar el historial de notificaciones
- Campos: titulo, mensaje, tipo, pedidoId, leida, enviada, fechas
- Relación con Cliente y Pedido

### 2. Modificación: `Cliente.cs`
- Agregado campo `FcmToken` para almacenar el token del dispositivo móvil
- Relación con la tabla Notificaciones

### 3. Nuevo Servicio: `NotificacionService.cs`
Funcionalidades:
- `EnviarNotificacionPushAsync()`: Envía notificación y la guarda en BD
- `RegistrarTokenFCMAsync()`: Registra/actualiza token FCM del cliente
- `EliminarTokenFCMAsync()`: Elimina token cuando cierra sesión
- `ObtenerNotificacionesClienteAsync()`: Consulta notificaciones
- `MarcarComoLeidaAsync()`: Marca notificación individual como leída
- `MarcarTodasComoLeidasAsync()`: Marca todas como leídas
- `NotificarCambioEstadoPedidoAsync()`: Notifica cambios de estado automáticamente

### 4. Nuevo Controlador: `NotificacionesController.cs`
Endpoints:
- `POST /api/notificaciones/registrar-token`: Registra token FCM del dispositivo
- `DELETE /api/notificaciones/eliminar-token`: Elimina token FCM
- `GET /api/notificaciones`: Obtiene notificaciones del cliente
- `GET /api/notificaciones/no-leidas/conteo`: Cuenta notificaciones no leídas
- `PUT /api/notificaciones/{id}/marcar-leida`: Marca como leída
- `PUT /api/notificaciones/marcar-todas-leidas`: Marca todas como leídas
- `POST /api/notificaciones/prueba`: Envía notificación de prueba

### 5. Modificación: `PedidosNewController.cs`
- Integrado `NotificacionService`
- Envía notificación automática cuando cambia el estado de un pedido

### 6. Modificación: `PizzaHubContext.cs`
- Agregado `DbSet<Notificacion>`
- Configuración de relaciones entre Cliente, Pedido y Notificación

### 7. Modificación: `PizzaHubDTOs.cs`
DTOs agregados:
- `NotificacionDto`: Para respuestas de notificaciones
- `RegistrarTokenFCMDto`: Para registrar token FCM

### 8. Modificación: `Program.cs`
- Registrado `NotificacionService` en el contenedor de dependencias

### 9. Modificación: `appsettings.json`
- Agregada sección `Firebase` con ruta al archivo de credenciales

## Configuración Requerida

### 1. Crear Proyecto en Firebase Console
1. Ve a https://console.firebase.google.com/
2. Crea un nuevo proyecto o usa uno existente
3. Habilita Cloud Messaging

### 2. Obtener Credenciales
1. En Firebase Console, ve a **Project Settings** (ícono engranaje)
2. Ve a la pestaña **Service Accounts**
3. Click en **Generate new private key**
4. Descarga el archivo JSON
5. Coloca el archivo como `firebase-credentials.json` en la raíz del proyecto PizzaHubAPI

### 3. Ejecutar Migración
```powershell
cd PizzaHubAPI
dotnet ef migrations add AgregarNotificacionesYFCM
dotnet ef database update
```

## Uso en la App Móvil

### 1. Obtener Token FCM en el Cliente Móvil
```javascript
// En tu app móvil (React Native, Flutter, etc.)
import messaging from '@react-native-firebase/messaging';

// Obtener token
const fcmToken = await messaging().getToken();

// Registrar token en el backend
await fetch('https://tu-api.com/api/notificaciones/registrar-token', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${jwtToken}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ fcmToken })
});
```

### 2. Escuchar Notificaciones
```javascript
// Notificación cuando la app está en primer plano
messaging().onMessage(async remoteMessage => {
  console.log('Notificación recibida:', remoteMessage);
  // Mostrar notificación local
});

// Notificación cuando se hace click
messaging().onNotificationOpenedApp(remoteMessage => {
  console.log('Notificación clickeada:', remoteMessage);
  // Navegar a pantalla de pedidos
  if (remoteMessage.data?.pedido_id) {
    navigation.navigate('DetallePedido', { 
      pedidoId: remoteMessage.data.pedido_id 
    });
  }
});
```

### 3. Consultar Notificaciones Guardadas
```javascript
// Obtener todas las notificaciones
const response = await fetch('https://tu-api.com/api/notificaciones', {
  headers: {
    'Authorization': `Bearer ${jwtToken}`
  }
});
const notificaciones = await response.json();

// Obtener solo no leídas
const responseNoLeidas = await fetch(
  'https://tu-api.com/api/notificaciones?soloNoLeidas=true',
  { headers: { 'Authorization': `Bearer ${jwtToken}` } }
);

// Obtener conteo
const conteoResponse = await fetch(
  'https://tu-api.com/api/notificaciones/no-leidas/conteo',
  { headers: { 'Authorization': `Bearer ${jwtToken}` } }
);
const { conteo } = await conteoResponse.json();
```

### 4. Marcar como Leída
```javascript
// Marcar una notificación como leída
await fetch(`https://tu-api.com/api/notificaciones/${notifId}/marcar-leida`, {
  method: 'PUT',
  headers: { 'Authorization': `Bearer ${jwtToken}` }
});

// Marcar todas como leídas
await fetch('https://tu-api.com/api/notificaciones/marcar-todas-leidas', {
  method: 'PUT',
  headers: { 'Authorization': `Bearer ${jwtToken}` }
});
```

## Flujo de Notificaciones Automáticas

Cuando se cambia el estado de un pedido, se envía automáticamente una notificación:

- **Pendiente** → "Tu pedido ha sido recibido y está pendiente de confirmación."
- **EnPreparacion** → "Tu pedido está siendo preparado."
- **EnCamino** → "Tu pedido está en camino."
- **Entregado** → "¡Tu pedido ha sido entregado! ¡Buen provecho!"
- **Cancelado** → "Tu pedido ha sido cancelado."

## Seguridad

- Solo los clientes autenticados pueden registrar tokens y consultar sus notificaciones
- El token JWT debe incluir el claim `ClienteId`
- Los tokens FCM inválidos se eliminan automáticamente
- Las notificaciones son privadas por cliente

## Notas Importantes

1. **firebase-credentials.json** debe estar en `.gitignore` (nunca subir a Git)
2. Para producción, usa variables de entorno o Azure Key Vault
3. El servicio maneja errores de FCM automáticamente
4. Las notificaciones se guardan en BD incluso si falla el push
5. Los clientes sin token FCM recibirán notificaciones en BD pero no push

## Testing

Usa el endpoint de prueba para verificar que todo funciona:
```bash
POST /api/notificaciones/prueba
Authorization: Bearer {tu_token_jwt}
```

Esto enviará una notificación de prueba al cliente autenticado.
