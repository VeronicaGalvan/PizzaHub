using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;
using PizzaHubAPI.Services;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/v1/auth")]
public class AuthController : ControllerBase
{
    private readonly IAuthService _authService;

    public AuthController(IAuthService authService)
    {
        _authService = authService;
    }

    /// <summary>
    /// Autentica un usuario y retorna los tokens de acceso
    /// </summary>
    [HttpPost("login")]
    [ProducesResponseType(typeof(LoginResponseDTO), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> Login([FromBody] LoginRequestDTO request)
    {
        var response = await _authService.LoginAsync(request);
        if (response == null)
            return Unauthorized(new { message = "Credenciales inválidas" });

        return Ok(response);
    }

    /// <summary>
    /// Registra un nuevo usuario y retorna tokens (login automático)
    /// </summary>
    [HttpPost("register")]
    [ProducesResponseType(typeof(LoginResponseDTO), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> Register([FromBody] RegisterRequestDTO request)
    {
        var response = await _authService.RegisterAsync(request);
        if (response == null)
            return Conflict(new { message = "El email ya está en uso" });

        // Return 201 Created with location header pointing to the newly created resource
        return Created($"/api/v1/users/{response.Usuario.Id}", response);
    }

    /// <summary>
    /// Refresca el token de acceso usando un refresh token válido
    /// </summary>
    [HttpPost("refresh")]
    [ProducesResponseType(typeof(LoginResponseDTO), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> RefreshToken([FromBody] RefreshTokenRequestDTO request)
    {
        var response = await _authService.RefreshTokenAsync(request.RefreshToken);
        if (response == null)
            return Unauthorized(new { message = "Token inválido o expirado" });

        return Ok(response);
    }

    /// <summary>
    /// Cierra la sesión del usuario revocando sus tokens
    /// </summary>
    [Authorize]
    [HttpPost("logout")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> Logout()
    {
        var token = HttpContext.Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        await _authService.RevocarTokenAsync(token, userId);
        return Ok(new { message = "Sesión cerrada correctamente" });
    }

    /// <summary>
    /// Cambia el rol de un usuario (TEMPORAL - Para desarrollo en Render sin acceso a BD)
    /// Roles disponibles: Administrador, Empleado, Repartidor, Cliente
    /// </summary>
    [Authorize]
    [HttpPut("cambiar-rol")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> CambiarRol([FromBody] CambiarRolRequestDTO request)
    {
        // Validar que el rol sea válido
        if (!Enum.TryParse<UsuarioRolEnum>(request.NuevoRol, true, out var nuevoRol))
        {
            return BadRequest(new { 
                message = "Rol inválido. Roles válidos: Administrador, Empleado, Repartidor, Cliente" 
            });
        }

        var exito = await _authService.CambiarRolAsync(request.UsuarioId, nuevoRol);
        
        if (!exito)
            return NotFound(new { message = "Usuario no encontrado" });

        return Ok(new { 
            message = $"Rol cambiado exitosamente a {nuevoRol}",
            usuarioId = request.UsuarioId,
            nuevoRol = nuevoRol.ToString()
        });
    }
}