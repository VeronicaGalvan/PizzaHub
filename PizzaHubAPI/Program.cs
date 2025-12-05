using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using PizzaHubAPI.Configurations;
using PizzaHubAPI.Data;
using PizzaHubAPI.Services;
using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
var jwtSettingsSection = builder.Configuration.GetSection("JwtSettings");
if (!jwtSettingsSection.Exists())
{
    throw new InvalidOperationException("La sección JwtSettings no está configurada en appsettings.json");
}

builder.Services.Configure<JwtSettings>(jwtSettingsSection);
var jwtSettings = jwtSettingsSection.Get<JwtSettings>();
if (jwtSettings == null)
{
    throw new InvalidOperationException("No se pudieron cargar los ajustes JWT");
}

if (string.IsNullOrEmpty(jwtSettings.SecretKey) || jwtSettings.SecretKey.Length < 32)
{
    throw new InvalidOperationException("La clave secreta JWT debe tener al menos 32 caracteres");
}

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        ValidIssuer = jwtSettings.Issuer,
        ValidAudience = jwtSettings.Audience,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtSettings.SecretKey)),
        ClockSkew = TimeSpan.Zero // Hace que los tokens expiren exactamente en el tiempo especificado
    };

    options.Events = new JwtBearerEvents
    {
        OnAuthenticationFailed = context =>
        {
            if (context.Exception.GetType() == typeof(SecurityTokenExpiredException))
            {
                context.Response.Headers.Append("Token-Expired", "true");
            }
            return Task.CompletedTask;
        }
    };
});

// Configurar DbContext con conversión automática del connection string
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");

// Convertir formato postgresql:// a formato compatible con Npgsql si es necesario
if (!string.IsNullOrEmpty(connectionString) && connectionString.StartsWith("postgresql://"))
{
    var uri = new Uri(connectionString);
    var dbPort = uri.Port > 0 ? uri.Port : 5432; // Puerto por defecto de PostgreSQL
    var userInfo = uri.UserInfo.Split(':');
    var username = userInfo[0];
    var password = userInfo.Length > 1 ? userInfo[1] : "";
    
    connectionString = $"Host={uri.Host};Port={dbPort};Database={uri.AbsolutePath.TrimStart('/')};Username={username};Password={password};SSL Mode=Require;Trust Server Certificate=true";
}

builder.Services.AddDbContext<PizzaHubContext>(options =>
    options.UseNpgsql(connectionString)
);

builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles;
        options.JsonSerializerOptions.DefaultIgnoreCondition = System.Text.Json.Serialization.JsonIgnoreCondition.WhenWritingNull;
    });
builder.Services.AddAutoMapper(typeof(Program));

// Registrar servicios
builder.Services.AddScoped<AuthService>();
builder.Services.AddScoped<PedidoService>();
builder.Services.AddScoped<NotificacionService>();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo { 
        Title = "PizzaHub API", 
        Version = "v1",
        Description = "API para el sistema de gestión de pizzería PizzaHub"
    });
    
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Description = "JWT Authorization header using the Bearer scheme.",
        Name = "Authorization",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.Http,
        Scheme = "bearer"
    });

    c.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            Array.Empty<string>()
        }
    });
});

// Registrar servicios
builder.Services.AddScoped<IAuthService, AuthService>();

// CORS
builder.Services.AddCors(options =>
{
    // Política específica para Netlify
    options.AddPolicy("AllowNetlify", policy =>
    {
        policy.WithOrigins("https://pizzahub.netlify.app")
            .AllowAnyHeader()
            .AllowAnyMethod()
            .AllowCredentials();
    });
    
    options.AddDefaultPolicy(policy =>
    {
        var origins = builder.Configuration.GetSection("Cors:AllowedOrigins").Get<string[]>() 
            ?? new[] { "http://localhost:3000", "http://localhost:5173", "http://localhost:5174", "capacitor://localhost" };
            
        policy.WithOrigins(origins)
            .SetIsOriginAllowedToAllowWildcardSubdomains()
            .AllowAnyMethod()
            .AllowAnyHeader()
            .AllowCredentials()
            .WithExposedHeaders("Token-Expired");
    });
    
    // Política permisiva adicional para desarrollo
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin()
            .AllowAnyMethod()
            .AllowAnyHeader();
    });
});

var app = builder.Build();

var firebaseConfig = builder.Configuration.GetSection("Firebase");
var credentialsEnv = firebaseConfig["CredentialJsonEnv"];

var credentialJson = Environment.GetEnvironmentVariable(credentialsEnv);

if (!string.IsNullOrWhiteSpace(credentialJson))
{
    var credential = Google.Apis.Auth.OAuth2.ServiceAccountCredential
        .FromServiceAccountData(new MemoryStream(Encoding.UTF8.GetBytes(credentialJson)));

    FirebaseApp.Create(new AppOptions()
    {
        Credential = GoogleCredential.FromServiceAccountCredential(credential)
    });

    Console.WriteLine("Firebase inicializado con variable de entorno.");
}
else
{
    Console.WriteLine("Advertencia: Firebase NO inicializado. No se encontró la variable de entorno.");
}

// Configure the HTTP request pipeline.
// Swagger habilitado en todos los ambientes para facilitar pruebas
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "PizzaHub API v1");
    c.RoutePrefix = "swagger";
});

// Configurar para HTTPS solo en desarrollo (Render maneja SSL en producción)
if (app.Environment.IsDevelopment())
{
    app.UseHttpsRedirection();
}

app.UseCors("AllowNetlify");

app.UseAuthentication();
app.UseAuthorization();

// Health check endpoint para Render
app.MapGet("/health", () => Results.Ok(new { 
    status = "healthy", 
    timestamp = DateTime.UtcNow,
    environment = app.Environment.EnvironmentName 
})).AllowAnonymous();

// Endpoint de diagnóstico para verificar configuración
app.MapGet("/config-check", (IConfiguration config) =>
{
    var connString = config.GetConnectionString("DefaultConnection");
    return Results.Ok(new { 
        hasConnectionString = !string.IsNullOrEmpty(connString),
        connectionStringLength = connString?.Length ?? 0,
        connectionStringStart = connString?.Substring(0, Math.Min(20, connString?.Length ?? 0)) ?? "VACÍO",
        allEnvVars = new {
            aspnetEnv = Environment.GetEnvironmentVariable("ASPNETCORE_ENVIRONMENT"),
            hasConnString = Environment.GetEnvironmentVariable("ConnectionStrings__DefaultConnection") != null
        }
    });
}).AllowAnonymous();

// Endpoint temporal para aplicar migraciones automáticamente
app.MapGet("/migrate", async (PizzaHubContext db, IConfiguration config) =>
{
    try
    {
        var connString = config.GetConnectionString("DefaultConnection");
        if (string.IsNullOrEmpty(connString))
        {
            return Results.Problem("❌ Connection String no configurado. Verifica las variables de entorno en Render.");
        }
        
        await db.Database.MigrateAsync();
        return Results.Ok(new { 
            message = "✅ Migraciones aplicadas exitosamente",
            timestamp = DateTime.UtcNow 
        });
    }
    catch (Exception ex)
    {
        return Results.Problem($"❌ Error al aplicar migraciones: {ex.Message}\n\nStack: {ex.StackTrace}");
    }
}).AllowAnonymous();

// Endpoint temporal para corregir el tipo de dato de unidad_medida
app.MapGet("/fix-unidad-medida", async (PizzaHubContext db) =>
{
    try
    {
        // Verificar si hay insumos en la tabla
        var countInsumos = await db.Insumos.CountAsync();
        
        if (countInsumos > 0)
        {
            return Results.Problem($"❌ La tabla insumos tiene {countInsumos} registros. Por seguridad, este endpoint no puede ejecutarse con datos existentes. Elimina los registros primero o ejecuta el script SQL manualmente.");
        }
        
        // Ejecutar el ALTER TABLE solo si la tabla está vacía
        var sql = "ALTER TABLE insumos ALTER COLUMN unidad_medida TYPE VARCHAR(10);";
        await db.Database.ExecuteSqlRawAsync(sql);
        
        return Results.Ok(new { 
            message = "✅ Tipo de dato de unidad_medida corregido exitosamente",
            timestamp = DateTime.UtcNow 
        });
    }
    catch (Exception ex)
    {
        return Results.Problem($"❌ Error al corregir unidad_medida: {ex.Message}\n\nStack: {ex.StackTrace}");
    }
}).AllowAnonymous();

app.MapControllers();

// Configurar puerto dinámico para Render.com
var port = Environment.GetEnvironmentVariable("PORT") ?? "5000";
app.Urls.Clear();
app.Urls.Add($"http://0.0.0.0:{port}");

app.Run();
