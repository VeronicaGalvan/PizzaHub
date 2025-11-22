# Guía de Despliegue en Render.com - PizzaHub API

## Paso 1: Preparar Base de Datos MySQL

### Opción A: Usar MySQL externo (Recomendado)
1. Crea una base de datos MySQL en un servicio externo:
   - [Railway](https://railway.app)
   - [PlanetScale](https://planetscale.com)
   - [Clever Cloud](https://www.clever-cloud.com)
   - O cualquier otro proveedor

2. Obtén el connection string, ejemplo:
   ```
   server=tu-host.mysql.database.azure.com;port=3306;database=pizzahub;user=admin;password=tupassword;SslMode=Required
   ```

### Opción B: Usar base de datos local (Solo desarrollo)
Si solo es para pruebas, puedes usar tu MySQL local con un túnel como [ngrok](https://ngrok.com) o similar.

## Paso 2: Subir Código a GitHub

1. Asegúrate de tener todos los cambios:
```bash
git status
git add .
git commit -m "Preparar para despliegue en Render"
git push origin back-end-structure
```

## Paso 3: Crear Web Service en Render

1. Ve a [Render.com](https://render.com) y crea una cuenta
2. Click en "New +" → "Web Service"
3. Conecta tu repositorio de GitHub: `VeronicaGalvan/PizzaHub`
4. Configuración del servicio:
   - **Name:** `pizzahub-api` (o el que prefieras)
   - **Region:** Oregon (US West) u otro cercano
   - **Branch:** `back-end-structure`
   - **Root Directory:** `PizzaHubAPI` (IMPORTANTE)
   - **Runtime:** `.NET`
   - **Build Command:** `dotnet restore && dotnet publish -c Release -o out`
   - **Start Command:** `dotnet out/PizzaHubAPI.dll`
   - **Plan:** Free

## Paso 4: Configurar Variables de Entorno

En el dashboard de Render, ve a "Environment" y agrega:

### Variables OBLIGATORIAS:

```
ASPNETCORE_ENVIRONMENT = Production
ASPNETCORE_URLS = http://0.0.0.0:$PORT
```

### Connection String de MySQL:
```
ConnectionStrings__DefaultConnection = server=TU_HOST;port=3306;database=pizzahub;user=TU_USUARIO;password=TU_PASSWORD;SslMode=Required
```
⚠️ **IMPORTANTE:** Reemplaza TU_HOST, TU_USUARIO y TU_PASSWORD con tus datos reales

### JWT Settings:
```
JwtSettings__SecretKey = tu_clave_super_secreta_minimo_32_caracteres_cambiar
JwtSettings__Issuer = PizzaHub.API
JwtSettings__Audience = PizzaHub.Clients
JwtSettings__AccessTokenDurationInMinutes = 15
JwtSettings__RefreshTokenDurationInDays = 7
```

### Firebase (OPCIONAL - Si usas notificaciones):
```
Firebase__ProjectId = tu-proyecto-id
Firebase__ClientEmail = tu-cliente@tu-proyecto.iam.gserviceaccount.com
Firebase__PrivateKey = -----BEGIN PRIVATE KEY-----\nTU_CLAVE_PRIVADA\n-----END PRIVATE KEY-----
```

**Nota:** Si no usas Firebase, puedes omitir estas variables.

### CORS (Opcional):
```
Cors__AllowedOrigins__0 = https://tu-frontend.com
Cors__AllowedOrigins__1 = http://localhost:3000
```

## Paso 5: Desplegar

1. Click en "Create Web Service"
2. Render comenzará a construir y desplegar automáticamente
3. Espera 5-10 minutos para el primer despliegue
4. Verás logs en tiempo real

## Paso 6: Aplicar Migraciones de Base de Datos

### Opción A: Desde tu computadora local (Más fácil)
1. Cambia temporalmente el connection string en tu `appsettings.json` al de producción
2. Ejecuta:
```bash
cd PizzaHubAPI
dotnet ef database update
```

### Opción B: Desde el Shell de Render
1. En el dashboard de Render, ve a "Shell"
2. Ejecuta:
```bash
dotnet ef database update --project PizzaHubAPI.csproj
```

## Paso 7: Probar la API

Una vez desplegada, tu API estará disponible en:
```
https://pizzahub-api.onrender.com
```

Prueba el health check:
```bash
curl https://pizzahub-api.onrender.com/health
```

Deberías ver:
```json
{
  "status": "healthy",
  "timestamp": "2025-11-21T...",
  "environment": "Production"
}
```

## Paso 8: Probar Swagger (Opcional)

Swagger solo funciona en Development por seguridad. Si quieres habilitarlo en producción para la tarea:

En `Program.cs`, cambia:
```csharp
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}
```

Por:
```csharp
app.UseSwagger();
app.UseSwaggerUI();
```

Luego accede a: `https://pizzahub-api.onrender.com/swagger`

## Solución de Problemas Comunes

### Error: "Failed to connect to database"
- Verifica que el connection string sea correcto
- Asegúrate de que la base de datos permita conexiones externas
- Verifica que incluya `SslMode=Required` si tu proveedor lo requiere

### Error: "Application failed to start"
- Revisa los logs en Render
- Verifica que todas las variables de entorno estén configuradas
- Asegúrate de que el Root Directory sea `PizzaHubAPI`

### La aplicación está "Sleeping"
- En el plan Free de Render, el servicio se duerme después de 15 minutos de inactividad
- La primera petición después de dormir puede tardar 30-60 segundos
- Considera usar un plan pagado si necesitas disponibilidad 24/7

### Error de CORS
- Agrega el dominio de tu frontend a las variables de entorno CORS
- O configura temporalmente en `Program.cs`: `.AllowAnyOrigin()`

## Actualizar el Código

Cada vez que hagas push a tu rama `back-end-structure`, Render redesplegiará automáticamente:

```bash
git add .
git commit -m "Actualización de la API"
git push origin back-end-structure
```

## Notas Adicionales

- El plan Free de Render tiene 750 horas gratis al mes (suficiente para una tarea)
- Los servicios Free se duermen después de 15 minutos de inactividad
- Los despliegues pueden tardar 5-10 minutos
- Render maneja automáticamente SSL/HTTPS
- Los logs están disponibles en el dashboard

## Soporte

Si tienes problemas, revisa:
1. Los logs en el dashboard de Render
2. La documentación oficial: https://render.com/docs
3. El canal de Discord de Render

¡Buena suerte con tu tarea! 🍕
