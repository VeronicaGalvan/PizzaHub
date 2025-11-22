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
    var port = uri.Port > 0 ? uri.Port : 5432; // Puerto por defecto de PostgreSQL
    var userInfo = uri.UserInfo.Split(':');
    var username = userInfo[0];
    var password = userInfo.Length > 1 ? userInfo[1] : "";
    
    connectionString = $"Host={uri.Host};Port={port};Database={uri.AbsolutePath.TrimStart('/')};Username={username};Password={password};SSL Mode=Require;Trust Server Certificate=true";
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
    options.AddDefaultPolicy(policy =>
    {
        var origins = builder.Configuration.GetSection("Cors:AllowedOrigins").Get<string[]>() 
            ?? new[] { "http://localhost:3000", "http://localhost:5173", "capacitor://localhost" };
            
        policy.WithOrigins(origins)
            .AllowAnyMethod()
            .AllowAnyHeader()
            .AllowCredentials();
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

app.UseCors();

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

app.MapControllers();

// Configurar puerto dinámico para Render.com
var port = Environment.GetEnvironmentVariable("PORT") ?? "5000";
app.Urls.Clear();
app.Urls.Add($"http://0.0.0.0:{port}");

app.Run();
