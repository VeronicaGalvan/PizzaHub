# 🔄 ENDPOINT TEMPORAL PARA CAMBIAR ROLES

## ⚠️ IMPORTANTE
Este endpoint es **TEMPORAL** y está diseñado para facilitar la gestión de roles en **Render.com** donde no tienes acceso directo a un gestor de base de datos.

## 📝 Uso del Endpoint

### Endpoint
```
PUT /api/v1/auth/cambiar-rol
```

### Headers Requeridos
```
Authorization: Bearer {tu_token_de_acceso}
Content-Type: application/json
```

### Body (JSON)
```json
{
  "usuarioId": 1,
  "nuevoRol": "Administrador"
}
```

## 🎭 Roles Disponibles

| Rol | Descripción |
|-----|-------------|
| `Cliente` | Usuario cliente normal (rol por defecto) |
| `Empleado` | Personal que atiende pedidos y gestiona la caja |
| `Repartidor` | Personal de entregas |
| `Administrador` | Acceso completo al sistema |

## 📋 Ejemplos de Uso

### 1. Cambiar a Administrador
```http
PUT /api/v1/auth/cambiar-rol
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "usuarioId": 1,
  "nuevoRol": "Administrador"
}
```

**Respuesta exitosa:**
```json
{
  "message": "Rol cambiado exitosamente a Administrador",
  "usuarioId": 1,
  "nuevoRol": "Administrador"
}
```

### 2. Cambiar a Empleado
```http
PUT /api/v1/auth/cambiar-rol
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "usuarioId": 2,
  "nuevoRol": "Empleado"
}
```

### 3. Cambiar a Repartidor
```http
PUT /api/v1/auth/cambiar-rol
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "usuarioId": 3,
  "nuevoRol": "Repartidor"
}
```

### 4. Cambiar a Cliente
```http
PUT /api/v1/auth/cambiar-rol
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "usuarioId": 4,
  "nuevoRol": "Cliente"
}
```

## 🔍 Posibles Respuestas

### ✅ Éxito (200 OK)
```json
{
  "message": "Rol cambiado exitosamente a Administrador",
  "usuarioId": 1,
  "nuevoRol": "Administrador"
}
```

### ❌ Rol Inválido (400 Bad Request)
```json
{
  "message": "Rol inválido. Roles válidos: Administrador, Empleado, Repartidor, Cliente"
}
```

### ❌ Usuario No Encontrado (404 Not Found)
```json
{
  "message": "Usuario no encontrado"
}
```

### ❌ Sin Autenticación (401 Unauthorized)
```json
{
  "message": "Unauthorized"
}
```

## 🚀 Flujo Recomendado en Render

### 1. Desplegar la API en Render
```bash
git add .
git commit -m "Agregado endpoint temporal para cambiar roles"
git push
```

### 2. Registrar tu primer usuario
```http
POST https://tu-app.onrender.com/api/v1/auth/register

{
  "email": "admin@pizzahub.com",
  "password": "Admin123!",
  "nombreUsuario": "AdminPizza",
  "telefonoContacto": "4771111111"
}
```

**Guarda el token que recibes en la respuesta**

### 3. Cambiar tu rol a Administrador
```http
PUT https://tu-app.onrender.com/api/v1/auth/cambiar-rol
Authorization: Bearer {token_del_paso_2}

{
  "usuarioId": 1,
  "nuevoRol": "Administrador"
}
```

### 4. Hacer login nuevamente
```http
POST https://tu-app.onrender.com/api/v1/auth/login

{
  "email": "admin@pizzahub.com",
  "password": "Admin123!"
}
```

**Ahora tu nuevo token tendrá el rol de Administrador**

### 5. Crear otros usuarios y cambiar sus roles
Repite el proceso para crear empleados, repartidores, etc.

## 📝 Notas Importantes

1. **Token Actualizado**: Después de cambiar el rol, debes hacer **login nuevamente** para obtener un token con el nuevo rol.

2. **Cualquier usuario autenticado puede cambiar roles**: Este endpoint no tiene restricciones de permisos para facilitar la configuración inicial. En producción, deberías limitarlo solo a administradores.

3. **Seguridad**: Este endpoint es temporal. Una vez que tengas acceso a un gestor de base de datos o hayas configurado todos los roles necesarios, considera:
   - Eliminarlo del código
   - O agregar validación de rol de Administrador:
   ```csharp
   [Authorize(Roles = "Administrador")]
   ```

4. **IDs de Usuario**: Puedes ver los IDs de usuario en las respuestas de registro/login en el campo `usuario.id`.

## 🔒 Recomendaciones de Seguridad

Una vez que hayas configurado los roles iniciales:

1. **Opción 1 - Eliminar el endpoint:**
   - Comenta o elimina el método `CambiarRol` del `AuthController.cs`
   - Redespliega la aplicación

2. **Opción 2 - Restringir a Administradores:**
   ```csharp
   [Authorize(Roles = "Administrador")]
   [HttpPut("cambiar-rol")]
   ```

3. **Opción 3 - Usar variable de entorno:**
   ```csharp
   if (!_configuration.GetValue<bool>("AllowRoleChange"))
       return Forbid();
   ```

## 🎯 Ejemplo Completo en Swagger

1. Ve a: `https://tu-app.onrender.com/swagger`
2. Registra un usuario en `/api/v1/auth/register`
3. Copia el `accessToken` de la respuesta
4. Haz clic en el botón "Authorize" (🔒) en la parte superior
5. Pega el token en el campo: `Bearer {tu_token}`
6. Haz clic en "Authorize"
7. Ahora puedes usar `/api/v1/auth/cambiar-rol`

## 📞 Soporte

Si tienes problemas:
- Verifica que el token sea válido
- Verifica que el `usuarioId` exista
- Verifica que el nombre del rol esté escrito correctamente (sensible a mayúsculas/minúsculas)
- Revisa los logs en Render.com

---

**Última actualización:** 22 de Noviembre de 2025  
**Versión:** 1.0 (Endpoint Temporal)
