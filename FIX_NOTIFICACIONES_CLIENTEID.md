# Fix: Error "No se pudo identificar al cliente" en Notificaciones

## Problema Detectado

Los endpoints de notificaciones retornaban **400 Bad Request** con el mensaje:

```json
{ "message": "No se pudo identificar al cliente" }
```

### Causa Raíz

El backend de notificaciones requiere el `clienteId` para identificar al usuario, pero:

- El JWT solo contiene `usuarioId` (nameidentifier: "4")
- El backend no puede resolver automáticamente el `clienteId` desde el `usuarioId`
- Los endpoints GET/PUT de notificaciones no recibían el `clienteId` como parámetro

## Solución Implementada

### 1. Actualización de AuthApi.kt

**Agregado `clienteId` como query parameter** a todos los endpoints de notificaciones que lo necesitan:

```kotlin
@GET("api/Notificaciones")
suspend fun getNotificaciones(
    @Query("clienteId") clienteId: Int
): Response<List<NotificacionDto>>

@GET("api/Notificaciones/no-leidas/conteo")
suspend fun getNotificacionesNoLeidasConteo(
    @Query("clienteId") clienteId: Int
): Response<Int>

@PUT("api/Notificaciones/{id}/marcar-leida")
suspend fun marcarNotificacionLeida(
    @Path("id") id: Int,
    @Query("clienteId") clienteId: Int
): Response<Any>

@PUT("api/Notificaciones/marcar-todas-leidas")
suspend fun marcarTodasNotificacionesLeidas(
    @Query("clienteId") clienteId: Int
): Response<Any>
```

**Import agregado:**

```kotlin
import retrofit2.http.Query
```

### 2. Actualización de AuthRepository.kt

**Modificados los métodos** para aceptar y pasar `clienteId`:

```kotlin
suspend fun getNotificaciones(clienteId: Int): Result<List<NotificacionDto>>
suspend fun getNotificacionesNoLeidasConteo(clienteId: Int): Result<Int>
suspend fun marcarNotificacionLeida(id: Int, clienteId: Int): Result<Unit>
suspend fun marcarTodasNotificacionesLeidas(clienteId: Int): Result<Unit>
```

### 3. Actualización de NotificationsViewModel.kt

**Agregada lógica** para obtener el `clienteId` del perfil del usuario:

```kotlin
private var clienteId: Int? = null

init {
    loadClienteId()
}

private fun loadClienteId() {
    viewModelScope.launch {
        val result = repo.getClientePerfil()
        result.fold(
            onSuccess = { perfil ->
                clienteId = perfil.id
                // Una vez que tenemos el clienteId, cargar notificaciones
                loadNotificaciones()
                loadUnreadCount()
            },
            onFailure = { e ->
                _error.value = "Error al obtener perfil: ${e.message}"
            }
        )
    }
}
```

**Todos los métodos ahora verifican** que `clienteId` esté disponible antes de hacer peticiones:

```kotlin
fun loadNotificaciones() {
    val cId = clienteId
    if (cId == null) {
        _error.value = "ClienteId no disponible"
        return
    }
    // ... resto del código usando cId
}
```

## Flujo Actualizado

1. **Al abrir NotificationsScreen o ProfileScreen:**
   - `NotificationsViewModel.init()` se ejecuta
   - Se llama a `loadClienteId()`
   - GET a `/api/Clientes/mi-perfil` para obtener `clienteId`
2. **Una vez obtenido el clienteId:**

   - Se llama a `loadNotificaciones()` → GET `/api/Notificaciones?clienteId=4`
   - Se llama a `loadUnreadCount()` → GET `/api/Notificaciones/no-leidas/conteo?clienteId=4`

3. **Al marcar como leída:**

   - PUT `/api/Notificaciones/{id}/marcar-leida?clienteId=4`

4. **Al marcar todas como leídas:**
   - PUT `/api/Notificaciones/marcar-todas-leidas?clienteId=4`

## Verificación en Logcat

**Antes (ERROR 400):**

```
--> GET https://10.0.2.2:7188/api/Notificaciones
<-- 400 {"message":"No se pudo identificar al cliente"}
```

**Después (debe ser 200 OK):**

```
--> GET https://10.0.2.2:7188/api/Notificaciones?clienteId=4
<-- 200 [{"id":1,"titulo":"Pedido actualizado",...}]

--> GET https://10.0.2.2:7188/api/Notificaciones/no-leidas/conteo?clienteId=4
<-- 200 3
```

## Cómo Probar

### 1. Instalar la nueva versión

```powershell
cd c:\PizzaHub\PizzaHub_App\PizzaHub_Mobile
.\gradlew.bat installDebug
```

### 2. Ver logs en tiempo real

```powershell
adb logcat | Select-String "okhttp.OkHttpClient|NotificationsViewModel"
```

### 3. Reproducir el escenario

1. Abrir la app
2. Iniciar sesión con `uli@gmail.com`
3. Ir a ProfileScreen
4. Tocar el icono de notificaciones

**Logs esperados:**

```
NotificationsViewModel: Loading clienteId...
okhttp.OkHttpClient: --> GET .../api/Clientes/mi-perfil
okhttp.OkHttpClient: <-- 200 {"id":4,"nombre":"Ulises",...}
okhttp.OkHttpClient: --> GET .../api/Notificaciones?clienteId=4
okhttp.OkHttpClient: <-- 200 [...]
okhttp.OkHttpClient: --> GET .../api/Notificaciones/no-leidas/conteo?clienteId=4
okhttp.OkHttpClient: <-- 200 3
```

### 4. Crear notificaciones de prueba desde el backend

Desde el backend, crear algunas notificaciones para el cliente:

```csharp
// En el controlador de Pedidos, cuando cambia el estado
await _notificationService.CreateNotification(
    clienteId: 4,
    titulo: "Pedido en camino",
    mensaje: "Tu pedido #1234 está en camino",
    tipo: "pedido_estado",
    pedidoId: 1234
);
```

### 5. Verificar en la app

- **ProfileScreen:** Badge debe mostrar el número de notificaciones no leídas
- **NotificationsScreen:** Lista debe mostrar las notificaciones del cliente
- **Al tocar una notificación:** Se marca como leída y el contador se reduce

## Requisitos del Backend

**IMPORTANTE:** El backend debe aceptar el query parameter `clienteId` en estos endpoints:

```csharp
[HttpGet]
public async Task<IActionResult> GetNotificaciones([FromQuery] int clienteId)
{
    // Usar clienteId en lugar de obtenerlo del JWT
    var notificaciones = await _context.Notificaciones
        .Where(n => n.ClienteId == clienteId)
        .ToListAsync();

    return Ok(notificaciones);
}

[HttpGet("no-leidas/conteo")]
public async Task<IActionResult> GetNotificacionesNoLeidasConteo([FromQuery] int clienteId)
{
    var count = await _context.Notificaciones
        .CountAsync(n => n.ClienteId == clienteId && !n.Leida);

    return Ok(count);
}

[HttpPut("{id}/marcar-leida")]
public async Task<IActionResult> MarcarNotificacionLeida(int id, [FromQuery] int clienteId)
{
    var notificacion = await _context.Notificaciones
        .FirstOrDefaultAsync(n => n.Id == id && n.ClienteId == clienteId);

    if (notificacion == null)
        return NotFound();

    notificacion.Leida = true;
    notificacion.FechaLectura = DateTime.UtcNow;
    await _context.SaveChangesAsync();

    return Ok();
}

[HttpPut("marcar-todas-leidas")]
public async Task<IActionResult> MarcarTodasNotificacionesLeidas([FromQuery] int clienteId)
{
    var notificaciones = await _context.Notificaciones
        .Where(n => n.ClienteId == clienteId && !n.Leida)
        .ToListAsync();

    foreach (var notif in notificaciones)
    {
        notif.Leida = true;
        notif.FechaLectura = DateTime.UtcNow;
    }

    await _context.SaveChangesAsync();
    return Ok();
}
```

## Compilación

✅ **BUILD SUCCESSFUL** - Todos los cambios compilados correctamente

```
BUILD SUCCESSFUL in 36s
37 actionable tasks: 10 executed, 27 up-to-date
```

## Archivos Modificados

- ✅ `AuthApi.kt` - Agregados query parameters `clienteId`
- ✅ `AuthRepository.kt` - Actualizados métodos para pasar `clienteId`
- ✅ `NotificationsViewModel.kt` - Agregada lógica para obtener y usar `clienteId`

## Próximos Pasos

1. **Instalar nueva versión** en el dispositivo
2. **Verificar logs** para confirmar que ahora se pasa `?clienteId=4`
3. **Confirmar respuestas 200 OK** en lugar de 400 Bad Request
4. **Crear notificaciones de prueba** desde el backend cuando cambie el estado del pedido
5. **Verificar que aparecen** en la app móvil

---

**Fecha:** 19 de noviembre de 2025  
**Estado:** ✅ Compilado y listo para probar
