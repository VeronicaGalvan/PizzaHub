# Guía de Pruebas - Sistema de Notificaciones Push

## Prerrequisitos

1. **Dispositivo Android** (físico o emulador) con Google Play Services
2. **Backend PizzaHub** ejecutándose y accesible
3. **Firebase Console** configurado con el proyecto
4. **APK instalado** en el dispositivo

## Instalación

### Compilar e Instalar

```powershell
# Desde c:\PizzaHub\PizzaHub_App\PizzaHub_Mobile

# Compilar APK Debug
.\gradlew.bat assembleDebug

# Instalar en dispositivo conectado (vía USB o emulador)
.\gradlew.bat installDebug

# Ver logs en tiempo real
adb logcat | Select-String "MyFirebaseMsgService|NotificationsViewModel"
```

## Escenarios de Prueba

### 1. Verificar Registro de Token FCM

**Pasos:**

1. Instalar la app con `.\gradlew.bat installDebug`
2. Abrir la app
3. Iniciar sesión con un usuario válido
4. Verificar en logcat:
   ```
   MyFirebaseMsgService: FCM new token: [token]
   MyFirebaseMsgService: FCM token registered with backend
   ```

**Resultado Esperado:**

- Token guardado localmente en DataStore
- Token enviado a `POST /api/Notificaciones/registrar-token`
- Backend asocia token con el usuario autenticado

**Verificar en Backend:**

```sql
-- Si usas SQL Server
SELECT * FROM NotificacionesTokensFCM
WHERE UsuarioId = [tu_usuario_id]
ORDER BY FechaRegistro DESC;
```

---

### 2. Recibir Notificación Push

**Método A: Desde Postman/Backend**

```json
POST {{baseUrl}}/api/Notificaciones/prueba
Authorization: Bearer [tu_token]
Content-Type: application/json

{
  "fcmToken": "[token_del_paso_1]"
}
```

**Método B: Desde Firebase Console**

1. Ir a Firebase Console > Cloud Messaging
2. Seleccionar "Send your first message"
3. Ingresar título y mensaje
4. En "Target" seleccionar "Single device"
5. Pegar el FCM token del paso 1
6. Enviar

**Resultado Esperado:**

- Notificación aparece en barra de notificaciones del dispositivo
- Al tocar la notificación, la app se abre (si está cerrada)
- Log en logcat: `MyFirebaseMsgService: Message received from: [sender]`

---

### 3. Ver Lista de Notificaciones

**Pasos:**

1. Tener al menos 3 notificaciones enviadas desde backend
2. Abrir la app
3. Ir a "Perfil" (icono de usuario en home)
4. Observar el **badge rojo** en el icono de notificaciones (debe mostrar número)
5. Tocar el icono de notificaciones
6. Verificar que se muestra la lista de notificaciones

**Resultado Esperado:**

- Badge muestra número correcto de notificaciones no leídas
- Lista muestra notificaciones en orden (más recientes primero)
- Notificaciones no leídas tienen fondo color `#FFE4CC` (naranja claro)
- Notificaciones leídas tienen fondo `softBeige` (#FFEEDD)
- Cada notificación muestra: título, mensaje, tipo (si existe)

---

### 4. Marcar Notificación como Leída

**Pasos:**

1. Estar en la pantalla de Notificaciones
2. Identificar una notificación **no leída** (fondo naranja con punto rojo)
3. Tocar esa notificación

**Resultado Esperado:**

- PUT enviado a `/api/Notificaciones/{id}/marcar-leida`
- Notificación cambia de color (fondo pasa a beige claro)
- El punto rojo desaparece
- Contador en ProfileScreen se reduce en 1
- Si la notificación tiene `pedidoId`, navega a OrderDetailScreen

---

### 5. Marcar Todas como Leídas

**Pasos:**

1. Tener al menos 2 notificaciones no leídas
2. Ir a pantalla de Notificaciones
3. Tocar el icono de **check circular** (esquina superior derecha)

**Resultado Esperado:**

- PUT enviado a `/api/Notificaciones/marcar-todas-leidas`
- Todas las notificaciones cambian a color beige claro
- Todos los puntos rojos desaparecen
- Badge en ProfileScreen se oculta (contador = 0)

---

### 6. Navegación desde Notificación de Pedido

**Preparación:**

- Crear una notificación desde backend con `pedidoId` válido

**Ejemplo desde backend (C# controller):**

```csharp
await _notificationService.CreateNotification(
    usuarioId: userId,
    titulo: "Pedido actualizado",
    mensaje: "Tu pedido #1234 está en camino",
    tipo: "pedido_estado",
    pedidoId: 1234
);
```

**Pasos:**

1. Recibir notificación con `pedidoId`
2. Abrir lista de notificaciones
3. Tocar la notificación del pedido

**Resultado Esperado:**

- Navega a `order_detail/1234`
- Se muestra la pantalla de detalles del pedido
- Notificación se marca como leída automáticamente

---

### 7. Contador de No Leídas

**Pasos:**

1. Crear 5 notificaciones desde backend (todas no leídas)
2. Abrir la app
3. Ir a ProfileScreen
4. Observar badge en icono de notificaciones

**Resultado Esperado:**

- Badge muestra "5"
- Fondo rojo circular sobre el icono
- Texto blanco y bold

**Variante - Más de 9 notificaciones:**

- Crear 12 notificaciones
- Badge muestra "9+" (no el número exacto)

---

### 8. Cerrar Sesión y Eliminar Token

**Pasos:**

1. Estar autenticado en la app
2. Anotar el FCM token actual (desde logcat o DataStore)
3. Ir a ProfileScreen
4. Tocar "Cerrar sesión"
5. Verificar en logcat la llamada a eliminar token

**Resultado Esperado:**

- DELETE enviado a `/api/Notificaciones/eliminar-token` con el token
- Token eliminado del DataStore local
- Usuario redirigido a LoginScreen

**Verificar que Token Eliminado Funciona:**

1. Desde Postman/Backend, enviar notificación al token eliminado
2. Notificación **NO** debe recibirse en el dispositivo
3. Backend debe retornar error o indicar que el token no existe

---

### 9. Re-Login y Re-Registro de Token

**Pasos:**

1. Después del paso 8 (logout)
2. Volver a iniciar sesión
3. Verificar logcat

**Resultado Esperado:**

- Firebase genera **nuevo token** (puede ser el mismo u otro)
- `onNewToken()` se ejecuta
- POST a `/api/Notificaciones/registrar-token`
- Token asociado nuevamente al usuario
- Notificaciones vuelven a funcionar

---

### 10. Notificación con App Cerrada

**Pasos:**

1. Cerrar completamente la app (swipe en recientes)
2. Desde Firebase Console o Postman, enviar notificación
3. Observar barra de notificaciones del dispositivo

**Resultado Esperado:**

- Notificación aparece incluso con app cerrada
- Tocar notificación abre la app
- `onMessageReceived()` se ejecuta en background
- Notificación se agrega a la base de datos (backend)
- Al abrir lista de notificaciones, aparece la nueva

---

## Comandos Útiles

### Ver logs en tiempo real

```powershell
# Filtrar por notificaciones
adb logcat | Select-String "Notification|FCM|MyFirebaseMsgService"

# Ver solo errores
adb logcat *:E | Select-String "pizzahub"

# Limpiar logs
adb logcat -c
```

### Verificar Token FCM desde DataStore

```powershell
# Entrar al shell del dispositivo
adb shell

# Navegar a la app data
cd /data/data/com.example.pizzahub_mobile/files/datastore

# Ver archivos
ls -la

# Salir
exit
```

### Desinstalar y reinstalar

```powershell
.\gradlew.bat uninstallDebug
.\gradlew.bat installDebug
```

### Compilar y ejecutar en un solo comando

```powershell
.\gradlew.bat installDebug; adb shell am start -n com.example.pizzahub_mobile/.MainActivity
```

---

## Troubleshooting

### Token no se registra

**Síntoma:** Logs muestran "Failed to register FCM token"

**Soluciones:**

1. Verificar que el backend esté corriendo
2. Verificar que el usuario esté autenticado (token JWT válido)
3. Revisar logs del backend para ver el error exacto
4. Verificar que la URL del backend sea correcta en `RetrofitInstance.kt`

### Notificaciones no llegan

**Síntoma:** Notificación enviada desde backend pero no aparece en dispositivo

**Soluciones:**

1. Verificar que Google Play Services esté instalado
2. Verificar conexión a internet
3. Verificar que el token FCM sea válido (no haya expirado)
4. Desde Firebase Console, enviar mensaje de prueba directo al token
5. Revisar que el backend use las credenciales correctas de Firebase Admin SDK

### Badge no se actualiza

**Síntoma:** Contador de no leídas muestra número incorrecto

**Soluciones:**

1. Forzar recarga: salir y volver a entrar a ProfileScreen
2. Verificar que `GET /api/Notificaciones/no-leidas/conteo` retorne el valor correcto
3. Limpiar data y reinstalar:
   ```powershell
   adb uninstall com.example.pizzahub_mobile
   .\gradlew.bat installDebug
   ```

### Marcar como leída no funciona

**Síntoma:** Al tocar notificación, no cambia de color

**Soluciones:**

1. Verificar que `PUT /api/Notificaciones/{id}/marcar-leida` retorne 200 OK
2. Verificar que el ID de la notificación sea correcto
3. Revisar logs del backend para ver si hay error de autorización
4. Verificar que el ViewModel esté actualizando el estado local

---

## Endpoints del Backend

Asegúrate que estos endpoints estén implementados y funcionando:

```
✅ POST   /api/Notificaciones/registrar-token
✅ DELETE /api/Notificaciones/eliminar-token
✅ GET    /api/Notificaciones
✅ GET    /api/Notificaciones/no-leidas/conteo
✅ PUT    /api/Notificaciones/{id}/marcar-leida
✅ PUT    /api/Notificaciones/marcar-todas-leidas
✅ POST   /api/Notificaciones/prueba
```

## Checklist Final

Antes de dar por terminada la integración, verificar:

- [ ] Token FCM se registra al iniciar sesión
- [ ] Token FCM se elimina al cerrar sesión
- [ ] Notificaciones se reciben con app abierta
- [ ] Notificaciones se reciben con app cerrada
- [ ] Lista de notificaciones carga datos del backend
- [ ] Badge muestra contador correcto
- [ ] Marcar como leída funciona (individual)
- [ ] Marcar todas como leídas funciona
- [ ] Navegación a pedido desde notificación funciona
- [ ] UI diferencia notificaciones leídas/no leídas
- [ ] Estados de loading/error/vacío se muestran correctamente
- [ ] No hay crashes ni errores en logcat
- [ ] Build exitoso sin errores de compilación

---

**¡Sistema de notificaciones completamente funcional! 🎉**
