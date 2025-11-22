# PizzaHub API

API REST para sistema de gestión de pizzería con pedidos, repartidores y notificaciones.

## Tecnologías
- ASP.NET Core 9.0
- Entity Framework Core
- MySQL
- JWT Authentication
- Firebase Cloud Messaging

## Configuración para Desarrollo Local

### Requisitos
- .NET 9.0 SDK
- MySQL Server

### Variables de Entorno Necesarias

En `appsettings.json` configura:

```json
{
  "ConnectionStrings": {
    "DefaultConnection": "server=localhost;port=3306;database=pizzahub;user=root;password=tupassword"
  },
  "JwtSettings": {
    "SecretKey": "tu_clave_secreta_minimo_32_caracteres",
    "Issuer": "PizzaHub.API",
    "Audience": "PizzaHub.Clients",
    "AccessTokenDurationInMinutes": 15,
    "RefreshTokenDurationInDays": 7
  },
  "Firebase": {
    "CredentialsPath": "path/to/firebase-credentials.json"
  }
}
```

### Instalación

```bash
# Restaurar paquetes
dotnet restore

# Aplicar migraciones
dotnet ef database update

# Ejecutar
dotnet run
```

## Despliegue en Render.com

1. Conecta tu repositorio de GitHub
2. Render detectará automáticamente la configuración desde `render.yaml`
3. Configura las variables de entorno en el dashboard de Render
4. El despliegue se realizará automáticamente

### Variables de Entorno en Render

Configura estas variables en el dashboard de Render:

- `ConnectionStrings__DefaultConnection`: Tu connection string de MySQL
- `JwtSettings__SecretKey`: Clave secreta para JWT
- `JwtSettings__Issuer`: PizzaHub.API
- `JwtSettings__Audience`: PizzaHub.Clients
- `Firebase__ProjectId`: ID de tu proyecto Firebase
- `Firebase__PrivateKey`: Clave privada de Firebase
- `Firebase__ClientEmail`: Email del cliente Firebase

## Endpoints Principales

- `/api/auth` - Autenticación
- `/api/pedidos` - Gestión de pedidos
- `/api/productos` - Productos disponibles
- `/api/repartidores` - Gestión de repartidores
- `/api/clientes` - Gestión de clientes
- `/health` - Health check

## Documentación API

La documentación Swagger está disponible en `/swagger` cuando la app está en modo desarrollo.

## Estructura del Proyecto

```
PizzaHubAPI/
├── Controllers/      # Endpoints de la API
├── Models/          # Modelos de datos y DTOs
├── Services/        # Lógica de negocio
├── Data/            # Contexto de base de datos
├── Migrations/      # Migraciones de EF Core
└── Configurations/  # Configuraciones (JWT, etc)
```

## Autor

Proyecto universitario - UTL
