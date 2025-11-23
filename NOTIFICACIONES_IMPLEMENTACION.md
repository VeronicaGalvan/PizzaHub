# Implementación de Notificaciones Push - PizzaHub Mobile

## Resumen de Implementación

Se implementó la integración completa del sistema de notificaciones push con los endpoints del backend.

### Archivos Creados

1. **NotificationsViewModel.kt** (`ui/viewmodel/NotificationsViewModel.kt`)
   - Maneja el estado de las notificaciones
   - Carga notificaciones desde el backend
   - Cuenta notificaciones no leídas
   - Permite marcar notificaciones como leídas (individual y todas)
   - StateFlows para: `notificaciones`, `unreadCount`, `isLoading`, `error`

### Archivos Modificados

1. **TokenDataStore.kt** (`data/storage/TokenDataStore.kt`)

   - Agregado almacenamiento persistente del FCM token
   - Métodos: `saveFcmToken()`, `getFcmTokenFlow()`, `getFcmTokenBlocking()`
   - El token se limpia al hacer logout

2. **AuthApi.kt** (`data/network/AuthApi.kt`)

   - Agregados 7 endpoints de notificaciones:
     - `POST /api/Notificaciones/registrar-token`
     - `DELETE /api/Notificaciones/eliminar-token` (usando @HTTP con body)
     - `GET /api/Notificaciones`
     - `GET /api/Notificaciones/no-leidas/conteo`
     - `PUT /api/Notificaciones/{id}/marcar-leida`
     - `PUT /api/Notificaciones/marcar-todas-leidas`
     - `POST /api/Notificaciones/prueba`

3. **AuthRepository.kt** (`data/network/AuthRepository.kt`)

   - Wrappers para todos los endpoints de notificaciones
   - Manejo de errores consistente con `Result<T>`
   - Métodos alineados con los endpoints del backend

4. **MyFirebaseMessagingService.kt** (`notifications/MyFirebaseMessagingService.kt`)

   - `onNewToken()` ahora:
     - Guarda el token localmente con `TokenDataStore.saveFcmToken()`
     - Envía el token al backend mediante `repo.registrarTokenFcm()`
     - Log detallado del proceso

5. **AuthViewModel.kt** (`ui/viewmodel/AuthViewModel.kt`)

   - `logout()` ahora:
     - Obtiene el FCM token guardado
     - Llama a `repo.eliminarTokenFcm()` antes de cerrar sesión
     - Asegura que el backend deje de enviar notificaciones

6. **NotificationsScreen.kt** (`ui/screens/NotificationsScreen.kt`)

   - Completamente refactorizada para usar datos reales del backend
   - Integrada con `NotificationsViewModel`
   - Muestra badge en notificaciones no leídas (punto naranja)
   - Permite marcar como leída al hacer clic
   - Botón para marcar todas como leídas
   - Navegación a OrderDetailScreen si la notificación tiene `pedidoId`
   - UI responsive con loading states y mensajes vacíos

7. **ProfileScreen.kt** (`ui/screens/ProfileScreen.kt`)

   - Badge de contador de notificaciones no leídas
   - Muestra número en círculo rojo sobre el icono de notificaciones
   - Limita el display a "9+" para números > 9
   - Se actualiza automáticamente con el estado del ViewModel

8. **AppNavHost.kt** (`ui/navigation/AppNavHost.kt`)
   - Ruta de notificaciones actualizada con callback `onNavigateToOrder`
   - Permite navegación desde notificaciones hacia detalles de pedidos

### Modelos de Datos Existentes (creados previamente)

- **NotificacionDto** (`data/models/NotificacionModels.kt`)

  - Campos: id, titulo, mensaje, tipo, pedidoId, leida, enviada, fechaCreacion, fechaLectura

- **RegistrarTokenFCMDto** (`data/models/NotificacionModels.kt`)
  - Campo: FcmToken (String)

## Flujo Completo de Notificaciones

### 1. Registro del Token FCM

```
1. Firebase genera un token
2. onNewToken() en MyFirebaseMessagingService
3. Token guardado en TokenDataStore
4. POST /api/Notificaciones/registrar-token
5. Backend asocia token con el usuario autenticado
```

### 2. Recepción de Notificaciones

```
1. Backend envía notificación push via Firebase
2. onMessageReceived() en MyFirebaseMessagingService
3. NotificationHelper muestra notificación local
4. Usuario ve notificación en barra del sistema
```

### 3. Visualización en App

```
1. Usuario abre pantalla de Notificaciones
2. NotificationsViewModel carga desde GET /api/Notificaciones
3. Se muestra lista de notificaciones
4. Badge en ProfileScreen muestra contador de no leídas
```

### 4. Marcar como Leída

```
1. Usuario toca una notificación
2. PUT /api/Notificaciones/{id}/marcar-leida
3. UI actualizada localmente
4. Contador de no leídas se reduce
5. Si tiene pedidoId, navega a OrderDetailScreen
```

### 5. Logout

```
1. Usuario cierra sesión
2. AuthViewModel obtiene FCM token
3. DELETE /api/Notificaciones/eliminar-token
4. Backend desasocia token del usuario
5. Token eliminado de TokenDataStore
```

## Características Implementadas

✅ Registro automático de token FCM al iniciar app  
✅ Eliminación de token FCM al cerrar sesión  
✅ Lista de notificaciones con datos del backend  
✅ Contador de notificaciones no leídas en ProfileScreen  
✅ Marcar notificaciones como leídas (individual)  
✅ Marcar todas las notificaciones como leídas  
✅ Navegación a detalles de pedido desde notificación  
✅ UI diferenciada para notificaciones leídas/no leídas  
✅ Indicador visual de notificación no leída (badge)  
✅ Estados de carga (loading, error, vacío)  
✅ Persistencia local del FCM token  
✅ Manejo de errores en todos los endpoints

## Endpoints Integrados

| Método | Endpoint                                  | Propósito                  | Estado |
| ------ | ----------------------------------------- | -------------------------- | ------ |
| POST   | `/api/Notificaciones/registrar-token`     | Registrar FCM token        | ✅     |
| DELETE | `/api/Notificaciones/eliminar-token`      | Eliminar FCM token         | ✅     |
| GET    | `/api/Notificaciones`                     | Listar notificaciones      | ✅     |
| GET    | `/api/Notificaciones/no-leidas/conteo`    | Contador no leídas         | ✅     |
| PUT    | `/api/Notificaciones/{id}/marcar-leida`   | Marcar como leída          | ✅     |
| PUT    | `/api/Notificaciones/marcar-todas-leidas` | Marcar todas               | ✅     |
| POST   | `/api/Notificaciones/prueba`              | Enviar notificación prueba | ✅     |

## Pruebas Recomendadas

1. **Registro de Token**

   - Instalar app en dispositivo
   - Verificar logs: "FCM token registered with backend"
   - Comprobar en backend que el token se asoció al usuario

2. **Recepción de Notificaciones**

   - Desde backend, enviar notificación de prueba usando POST /api/Notificaciones/prueba
   - Verificar que aparece en la barra de notificaciones
   - Comprobar que aparece en la lista dentro de la app

3. **Contador de No Leídas**

   - Crear varias notificaciones desde backend
   - Abrir ProfileScreen
   - Verificar que el badge muestra el número correcto

4. **Marcar como Leída**

   - Tocar una notificación no leída
   - Verificar que cambia de color (de #FFE4CC a softBeige)
   - Verificar que el contador se reduce
   - Si tiene pedidoId, verificar navegación a OrderDetailScreen

5. **Marcar Todas como Leídas**

   - Tener varias notificaciones no leídas
   - Tocar el icono de check en NotificationsScreen
   - Verificar que todas cambian a leídas
   - Verificar que el contador va a 0

6. **Logout**
   - Cerrar sesión
   - Verificar logs: llamada a DELETE /api/Notificaciones/eliminar-token
   - Desde backend, intentar enviar notificación al token eliminado
   - Verificar que no se recibe

## Compilación

✅ **BUILD SUCCESSFUL** - Todos los cambios compilados sin errores

```
BUILD SUCCESSFUL in 45s
37 actionable tasks: 10 executed, 27 up-to-date
```

## Próximos Pasos Recomendados

1. **Implementar "Repetir Pedido"** con productos reales

   - Obtener detalles del pedido
   - Buscar productos por ID en el catálogo
   - Agregar al carrito con cantidades correctas

2. **Mejorar UI de Notificaciones**

   - Agregar pull-to-refresh
   - Mostrar fecha/hora formateada
   - Filtros por tipo de notificación
   - Acción de swipe para eliminar

3. **Optimizaciones**

   - Cache de notificaciones
   - Sincronización en background
   - Retry automático en caso de fallo de red

4. **Testing**
   - Unit tests para NotificationsViewModel
   - Integration tests para flujo completo de notificaciones
   - UI tests para NotificationsScreen

---

**Fecha de implementación**: 19 de noviembre de 2025  
**Estado**: ✅ Funcional y compilado exitosamente
