using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;
using PizzaHubAPI.Services;

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
    [Authorize(Roles = "Admin")]
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

        // Crear el repartidor
        var repartidor = new Repartidor
        {
            UsuarioId = usuario.Id,
            NumeroCelular = registro.NumeroCelular,
            Sobrenombre = registro.Sobrenombre,
            Nombre = registro.Nombre,
            Apellido = registro.Apellido,
            Colonia = registro.Colonia,
            Calle = registro.Calle,
            Numero = registro.Numero
        };

        _context.Repartidores.Add(repartidor);

        // Asignar rol de Repartidor
        var rolRepartidor = await _context.Roles.FirstOrDefaultAsync(r => r.Nombre == "Repartidor");
        if (rolRepartidor == null)
        {
            return BadRequest("Error al asignar rol de repartidor.");
        }

        _context.UsuariosRoles.Add(new UsuarioRol
        {
            UsuarioId = usuario.Id,
            RolId = rolRepartidor.Id
        });

        await _context.SaveChangesAsync();

        return Ok(authResponse);
    }

    [Authorize]
    [HttpGet("perfil")]
    public async Task<ActionResult<RepartidorDTO>> GetPerfil()
    {
        var usuarioId = int.Parse(User.FindFirst("UserId")?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == usuarioId);

        if (repartidor == null)
        {
            return NotFound("Repartidor no encontrado");
        }

        return new RepartidorDTO
        {
            Id = repartidor.Id,
            NumeroCelular = repartidor.NumeroCelular,
            Sobrenombre = repartidor.Sobrenombre,
            Nombre = repartidor.Nombre,
            Apellido = repartidor.Apellido,
            Colonia = repartidor.Colonia,
            Calle = repartidor.Calle,
            Numero = repartidor.Numero,
            Disponible = repartidor.Disponible
        };
    }

    [Authorize(Roles = "Admin")]
    [HttpGet]
    public async Task<ActionResult<IEnumerable<RepartidorDTO>>> GetRepartidores()
    {
        return await _context.Repartidores
            .Where(r => r.Activo)
            .Select(r => new RepartidorDTO
            {
                Id = r.Id,
                NumeroCelular = r.NumeroCelular,
                Sobrenombre = r.Sobrenombre,
                Nombre = r.Nombre,
                Apellido = r.Apellido,
                Colonia = r.Colonia,
                Calle = r.Calle,
                Numero = r.Numero,
                Disponible = r.Disponible
            })
            .ToListAsync();
    }

    [Authorize(Roles = "Admin,Repartidor")]
    [HttpPatch("{id}/disponibilidad")]
    public async Task<IActionResult> CambiarDisponibilidad(int id, bool disponible)
    {
        var repartidor = await _context.Repartidores.FindAsync(id);
        if (repartidor == null)
        {
            return NotFound();
        }

        var usuarioId = int.Parse(User.FindFirst("UserId")?.Value ?? "0");
        if (!User.IsInRole("Admin") && repartidor.UsuarioId != usuarioId)
        {
            return Forbid();
        }

        repartidor.Disponible = disponible;
        await _context.SaveChangesAsync();

        return NoContent();
    }
}