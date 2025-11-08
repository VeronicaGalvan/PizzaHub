using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;
using PizzaHubAPI.Services;
using System.Security.Claims;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class RepartidoresController : ControllerBase
{
    private readonly PizzaHubContext _context;
    private readonly AuthService _authService;

    public RepartidoresController(PizzaHubContext context, AuthService authService)
    {
        _context = context;
        _authService = authService;
    }

    [HttpPost("registro")]
    [Authorize(Roles = "Administrador")]
    public async Task<ActionResult<LoginResponseDTO>> RegistrarRepartidor(RegistroRepartidorDTO registro)
    {
        // Registrar usuario base
        var registerRequest = new RegisterRequestDTO
        {
            Email = registro.Email,
            Password = registro.Password
        };

        var authResponse = await _authService.RegisterAsync(registerRequest);
        if (authResponse == null)
        {
            return BadRequest("Error al registrar el usuario. El email ya podría estar en uso.");
        }

        var usuario = await _context.Usuarios
            .FirstOrDefaultAsync(u => u.Email == registro.Email);

        if (usuario == null)
        {
            return BadRequest("Error al crear el usuario.");
        }

        // Crear la persona y el repartidor vinculados al usuario
        var persona = new Persona
        {
            UsuarioId = usuario.Id,
            Nombre = registro.Nombre,
            Apellido = registro.Apellido,
            Telefono = registro.NumeroCelular,
            Colonia = registro.Colonia,
            Calle = registro.Calle,
            Numero = registro.Numero,
            FechaRegistro = DateTime.UtcNow
        };
        _context.Personas.Add(persona);
        await _context.SaveChangesAsync();

        var repartidor = new Repartidor
        {
            PersonaId = persona.Id,
            Sobrenombre = registro.Sobrenombre,
            Disponible = false,
            Activo = true
        };

        _context.Repartidores.Add(repartidor);

        // Assign enum role on the Usuario and persist (no DB-role table used)
        usuario.Rol = UsuarioRolEnum.Repartidor;
        await _context.SaveChangesAsync();

        return Ok(authResponse);
    }

    [Authorize]
    [HttpGet("perfil")]
    public async Task<ActionResult<RepartidorDTO>> GetPerfil()
    {
    var usuarioId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .Include(r => r.Persona)
            .FirstOrDefaultAsync(r => r.Persona.UsuarioId == usuarioId);

        if (repartidor == null)
        {
            return NotFound("Repartidor no encontrado");
        }

        return new RepartidorDTO
        {
            Id = repartidor.Id,
            NumeroCelular = repartidor.Persona.Telefono ?? string.Empty,
            Sobrenombre = repartidor.Sobrenombre ?? string.Empty,
            Nombre = repartidor.Persona.Nombre ?? string.Empty,
            Apellido = repartidor.Persona.Apellido ?? string.Empty,
            Colonia = repartidor.Persona.Colonia ?? string.Empty,
            Calle = repartidor.Persona.Calle ?? string.Empty,
            Numero = repartidor.Persona.Numero ?? string.Empty,
            Disponible = repartidor.Disponible
        };
    }

    [Authorize(Roles = "Administrador")]
    [HttpGet]
    public async Task<ActionResult<IEnumerable<RepartidorDTO>>> GetRepartidores()
    {
        return await _context.Repartidores
            .Where(r => r.Activo)
            .Include(r => r.Persona)
            .Select(r => new RepartidorDTO
            {
                Id = r.Id,
                NumeroCelular = r.Persona.Telefono ?? string.Empty,
                Sobrenombre = r.Sobrenombre ?? string.Empty,
                Nombre = r.Persona.Nombre ?? string.Empty,
                Apellido = r.Persona.Apellido ?? string.Empty,
                Colonia = r.Persona.Colonia ?? string.Empty,
                Calle = r.Persona.Calle ?? string.Empty,
                Numero = r.Persona.Numero ?? string.Empty,
                Disponible = r.Disponible
            })
            .ToListAsync();
    }

    [Authorize(Roles = "Administrador,Repartidor")]
    [HttpPatch("{id}/disponibilidad")]
    public async Task<IActionResult> CambiarDisponibilidad(int id, bool disponible)
    {
        var repartidor = await _context.Repartidores.FindAsync(id);
        if (repartidor == null)
        {
            return NotFound();
        }

    var usuarioId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        if (!User.IsInRole("Administrador") && repartidor.Persona.UsuarioId != usuarioId)
        {
            return Forbid();
        }

        repartidor.Disponible = disponible;
        await _context.SaveChangesAsync();

        return NoContent();
    }
}