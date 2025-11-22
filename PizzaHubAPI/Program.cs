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

builder.Services.AddDbContext<PizzaHubContext>(options =>
    options.UseMySql(
        builder.Configuration.GetConnectionString("DefaultConnection"),
        ServerVersion.AutoDetect(builder.Configuration.GetConnectionString("DefaultConnection"))
    )
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

// Inicializar Firebase
try
{
    if (FirebaseApp.DefaultInstance == null)
    {
        var credentialsPath = builder.Configuration["Firebase:CredentialsPath"];
        
        // Opción 1: Desde archivo (desarrollo local)
        if (!string.IsNullOrEmpty(credentialsPath) && File.Exists(credentialsPath))
        {
            FirebaseApp.Create(new AppOptions
            {
                Credential = GoogleCredential.FromFile(credentialsPath)
            });
        }
        // Opción 2: Desde variables de entorno (producción en Render)
        else if (!string.IsNullOrEmpty(builder.Configuration["Firebase:ProjectId"]))
        {
            var projectId = builder.Configuration["Firebase:ProjectId"];
            var privateKey = builder.Configuration["Firebase:PrivateKey"];
            var clientEmail = builder.Configuration["Firebase:ClientEmail"];
            
            if (!string.IsNullOrEmpty(projectId) && !string.IsNullOrEmpty(privateKey) && !string.IsNullOrEmpty(clientEmail))
            {
                var credential = GoogleCredential.FromJson($@"{{
                    ""type"": ""service_account"",
                    ""project_id"": ""{projectId}"",
                    ""private_key"": ""{privateKey}"",
                    ""client_email"": ""{clientEmail}"",
                    ""token_uri"": ""https://oauth2.googleapis.com/token""
                }}");
                
                FirebaseApp.Create(new AppOptions
                {
                    Credential = credential
                });
            }
        }
    }
}
catch (Exception ex)
{
    Console.WriteLine($"Firebase no pudo inicializarse: {ex.Message}");
}

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// Configurar para HTTPS solo en producción (Render maneja SSL)
if (!app.Environment.IsDevelopment())
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

app.MapControllers();

// Configurar puerto dinámico para Render.com
var port = Environment.GetEnvironmentVariable("PORT") ?? "5000";
app.Urls.Clear();
app.Urls.Add($"http://0.0.0.0:{port}");

app.Run();
