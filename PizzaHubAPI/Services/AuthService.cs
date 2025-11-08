using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using PizzaHubAPI.Configurations;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Services;

public interface IAuthService
{
    Task<LoginResponseDTO?> LoginAsync(LoginRequestDTO request);
    Task<bool> RevocarTokenAsync(string token, int userId);
    Task<LoginResponseDTO?> RefreshTokenAsync(string refreshToken);
    Task<LoginResponseDTO?> RegisterAsync(RegisterRequestDTO request);
}

public class AuthService : IAuthService
{
    private readonly PizzaHubContext _context;
    private readonly JwtSettings _jwtSettings;

    public AuthService(PizzaHubContext context, IOptions<JwtSettings> jwtSettings)
    {
        _context = context;
        _jwtSettings = jwtSettings.Value;
    }

    public async Task<LoginResponseDTO?> LoginAsync(LoginRequestDTO request)
    {
        var usuario = await _context.Usuarios
            .FirstOrDefaultAsync(u => u.Email == request.Email);

        if (usuario == null || !VerifyPasswordHash(request.Password, usuario.ContraseñaHash))
            return null;

        return await GenerateAuthResponseAsync(usuario);
    }

    public async Task<bool> RevocarTokenAsync(string token, int userId)
    {
        var tokenRevocado = new TokenRevocado
        {
            Token = token,
            FechaRevocacion = DateTime.UtcNow,
            UsuarioId = userId
        };

        _context.TokensRevocados.Add(tokenRevocado);
        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<LoginResponseDTO?> RefreshTokenAsync(string refreshToken)
    {
        var principal = GetPrincipalFromToken(refreshToken);
        if (principal == null)
            return null;

        var userId = int.Parse(principal.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        var usuario = await _context.Usuarios
            .FirstOrDefaultAsync(u => u.Id == userId);

        if (usuario == null)
            return null;

        return await GenerateAuthResponseAsync(usuario);
    }

    public async Task<LoginResponseDTO?> RegisterAsync(RegisterRequestDTO request)
    {
        // Check if email already exists
        if (await _context.Usuarios.AnyAsync(u => u.Email == request.Email))
            return null;

        // Split full name into first and last name
        var nombres = request.NombreCompleto?.Split(' ', 2) ?? new string[2];
        var nombre = nombres.FirstOrDefault() ?? "";
        var apellido = nombres.Length > 1 ? nombres[1] : "";

        // Create user with persona
        var usuario = new Usuario
        {
            Email = request.Email,
            ContraseñaHash = BCrypt.Net.BCrypt.HashPassword(request.Password),
            FechaRegistro = DateTime.UtcNow,
            Rol = UsuarioRolEnum.Cliente, // default role
            Persona = new Persona
            {
                Nombre = nombre,
                Apellido = apellido,
                Telefono = request.TelefonoContacto ?? "",
                FechaRegistro = DateTime.UtcNow
            }
        };

        _context.Usuarios.Add(usuario);
        
        try
        {
            await _context.SaveChangesAsync();

            // If registering as cliente, create cliente record
            if (usuario.Rol == UsuarioRolEnum.Cliente)
            {
                var cliente = new Cliente
                {
                    PersonaId = usuario.Persona.Id,
                    Activo = true
                };
                _context.Clientes.Add(cliente);
                await _context.SaveChangesAsync();
            }
        }
        catch (Microsoft.EntityFrameworkCore.DbUpdateException)
        {
            // Likely a unique constraint violation (email) created by a race condition — treat as conflict
            return null;
        }

        // Return authentication response using the enum role on Usuario
        return await GenerateAuthResponseAsync(usuario);
    }

    private Task<LoginResponseDTO> GenerateAuthResponseAsync(Usuario usuario)
    {
        if (usuario == null) throw new ArgumentNullException(nameof(usuario));
        // Roles come solely from the Usuario enum
        var roles = new[] { usuario.Rol.ToString() };

        var claims = new[]
        {
            new Claim(ClaimTypes.NameIdentifier, usuario!.Id.ToString()),
            new Claim(ClaimTypes.Email, usuario!.Email),
            new Claim(ClaimTypes.Name, usuario!.Email),
            new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
        }.Concat(roles.Select(role => new Claim(ClaimTypes.Role, role)));

        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtSettings.SecretKey));
        var credentials = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
        var expires = DateTime.UtcNow.AddMinutes(_jwtSettings.AccessTokenDurationInMinutes);

        var token = new JwtSecurityToken(
            issuer: _jwtSettings.Issuer,
            audience: _jwtSettings.Audience,
            claims: claims,
            expires: expires,
            signingCredentials: credentials
        );

        var refreshToken = GenerateRefreshToken();

        return Task.FromResult(new LoginResponseDTO
        {
            AccessToken = new JwtSecurityTokenHandler().WriteToken(token),
            RefreshToken = refreshToken,
            ExpiresIn = (int)(expires - DateTime.UtcNow).TotalSeconds,
            Roles = roles,
            Usuario = new UserInfoDTO
            {
                Id = usuario.Id,
                Email = usuario.Email
            }
        });
    }

    private string GenerateRefreshToken()
    {
        var randomNumber = new byte[32];
        using var rng = RandomNumberGenerator.Create();
        rng.GetBytes(randomNumber);
        return Convert.ToBase64String(randomNumber);
    }

    private ClaimsPrincipal? GetPrincipalFromToken(string token)
    {
        var tokenValidationParameters = new TokenValidationParameters
        {
            ValidateAudience = false,
            ValidateIssuer = false,
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtSettings.SecretKey)),
            ValidateLifetime = false
        };

        try
        {
            var tokenHandler = new JwtSecurityTokenHandler();
            var principal = tokenHandler.ValidateToken(token, tokenValidationParameters, out _);
            return principal;
        }
        catch
        {
            return null;
        }
    }

    private bool VerifyPasswordHash(string password, string storedHash)
    {
        // TODO: Implement proper password hashing and verification
        return BCrypt.Net.BCrypt.Verify(password, storedHash);
    }
}