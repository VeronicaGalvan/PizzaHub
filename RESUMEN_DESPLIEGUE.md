# RESUMEN RÁPIDO - Despliegue PizzaHub en Render.com

## ✅ Lo que YA está listo:
- ✅ Program.cs configurado con puerto dinámico
- ✅ Health check endpoint en /health
- ✅ render.yaml con configuración automática
- ✅ Firebase configurado para variables de entorno
- ✅ README.md con documentación
- ✅ .gitignore configurado

## 🚀 Pasos para desplegar (5 minutos):

### 1️⃣ Subir a GitHub
```bash
git add .
git commit -m "Listo para Render"
git push origin back-end-structure
```

### 2️⃣ Crear Web Service en Render.com
- Ve a: https://render.com
- New + → Web Service
- Conecta tu repo: VeronicaGalvan/PizzaHub
- Branch: back-end-structure
- **Root Directory:** PizzaHubAPI  ⚠️ IMPORTANTE
- Runtime: .NET
- Plan: Free

### 3️⃣ Variables de Entorno MÍNIMAS en Render:

**OBLIGATORIAS:**
```
ConnectionStrings__DefaultConnection = server=TU_MYSQL_HOST;port=3306;database=pizzahub;user=TU_USER;password=TU_PASS
JwtSettings__SecretKey = cambia_esta_clave_por_una_de_minimo_32_caracteres_seguros
JwtSettings__Issuer = PizzaHub.API
JwtSettings__Audience = PizzaHub.Clients
```

**OPCIONALES (si usas notificaciones):**
```
Firebase__ProjectId = tu-proyecto-id
Firebase__ClientEmail = tu-email@proyecto.iam.gserviceaccount.com
Firebase__PrivateKey = -----BEGIN PRIVATE KEY-----\nTU_CLAVE\n-----END PRIVATE KEY-----
```

### 4️⃣ Aplicar Migraciones

Desde tu computadora, cambia el connection string en appsettings.json temporalmente y ejecuta:
```bash
cd PizzaHubAPI
dotnet ef database update
```

### 5️⃣ Probar
```
https://tu-app.onrender.com/health
https://tu-app.onrender.com/swagger
```

## 🗄️ Opciones para Base de Datos MySQL:

### Opción 1: Railway (Gratis 500 horas/mes)
1. railway.app → New Project → MySQL
2. Copia el connection string
3. Úsalo en Render

### Opción 2: Clever Cloud (Gratis permanente con límites)
1. clever-cloud.com → Create → MySQL
2. Copia el connection string
3. Úsalo en Render

### Opción 3: PlanetScale (Gratis con límites)
1. planetscale.com → Create database
2. Copia el connection string
3. Úsalo en Render

## ⚠️ IMPORTANTE ANTES DE SUBIR:

Verifica que `appsettings.json` NO tenga tus datos reales de producción:
```json
{
  "ConnectionStrings": {
    "DefaultConnection": "server=localhost;port=3306;database=pizzahub;user=root;password=cclab"
  },
  "JwtSettings": {
    "SecretKey": "tu_clave_super_secreta_aqui_min_32_caracteres_pizzahub_2025"
  }
}
```

Estos valores son para desarrollo local. En Render usarás variables de entorno.

## 🐛 Solución Rápida de Problemas:

**Error al conectar DB:**
→ Verifica el connection string en variables de entorno de Render

**App no inicia:**
→ Revisa que Root Directory sea "PizzaHubAPI" (sin barra al inicio)

**CORS Error:**
→ Agrega tu dominio frontend a Cors__AllowedOrigins__0

**Swagger no aparece:**
→ Es normal, solo funciona en Development (puedes cambiarlo si quieres)

## 📝 Archivos Creados:
- ✅ README.md - Documentación del proyecto
- ✅ render.yaml - Configuración automática de Render
- ✅ DESPLIEGUE_RENDER.md - Guía detallada paso a paso
- ✅ RESUMEN_DESPLIEGUE.md - Este archivo (guía rápida)

## 🎯 Checklist Final:
- [ ] Código subido a GitHub
- [ ] Base de datos MySQL creada (Railway/Clever Cloud/etc)
- [ ] Web Service creado en Render
- [ ] Variables de entorno configuradas
- [ ] Migraciones aplicadas
- [ ] /health responde correctamente
- [ ] Endpoints de la API funcionan

¡Listo! Tu API estará en línea en ~10 minutos. 🍕
