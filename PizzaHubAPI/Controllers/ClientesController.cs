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
public class ClientesController : ControllerBase
{
    private readonly PizzaHubContext _context;
    private readonly AuthService _authService;

    public ClientesController(PizzaHubContext context, AuthService authService)
    {
        _context = context;
        _authService = authService;
    }

    [HttpPost("registro")]
    public async Task<ActionResult<LoginResponseDTO>> RegistrarCliente(RegistroClienteDTO registro)
    {
        // Register through auth service with complete name
        var registerRequest = new RegisterRequestDTO
        {
            Email = registro.Email,
            Password = registro.Password,
            NombreCompleto = $"{registro.Nombre} {registro.Apellido}",
            TelefonoContacto = registro.NumeroCelular
        };

        var authResponse = await _authService.RegisterAsync(registerRequest);
        if (authResponse == null)
        {
            return BadRequest("Error al registrar el usuario. El email ya podría estar en uso.");
        }

        // Update additional client-specific information
        var usuario = await _context.Usuarios
            .Include(u => u.Persona)
            .FirstOrDefaultAsync(u => u.Email == registro.Email);

        if (usuario?.Persona == null)
        {
            return BadRequest("Error al crear el usuario.");
        }

        // Update persona with additional address information
        usuario.Persona.Colonia = registro.Colonia;
        usuario.Persona.Calle = registro.Calle;
        usuario.Persona.Numero = registro.Numero;

        // Update cliente distance estimation
        var cliente = await _context.Clientes
            .FirstOrDefaultAsync(c => c.PersonaId == usuario.Persona.Id);
        
        if (cliente != null)
        {
            cliente.DistanciaEstimacion = registro.DistanciaAproximada;
        }

        await _context.SaveChangesAsync();
        return Ok(authResponse);
    }

    [Authorize]
    [HttpGet("perfil")]
    public async Task<ActionResult<ClienteDTO>> GetPerfil()
    {
    var usuarioId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var cliente = await _context.Clientes
            .Include(c => c.Persona)
            .FirstOrDefaultAsync(c => c.Persona.UsuarioId == usuarioId);

        if (cliente == null)
        {
            return NotFound("Cliente no encontrado");
        }

        return new ClienteDTO
        {
            Id = cliente.Id,
            NumeroCelular = cliente.Persona.Telefono ?? string.Empty,
            Nombre = cliente.Persona.Nombre ?? string.Empty,
            Apellido = cliente.Persona.Apellido ?? string.Empty,
            Colonia = cliente.Persona.Colonia ?? string.Empty,
            Calle = cliente.Persona.Calle ?? string.Empty,
            Numero = cliente.Persona.Numero ?? string.Empty,
            DistanciaAproximada = cliente.DistanciaEstimacion,
            FechaRegistro = cliente.Persona.FechaRegistro
        };
    }

    [Authorize(Roles = "Administrador")]
    [HttpGet]
    public async Task<ActionResult<IEnumerable<ClienteDTO>>> GetClientes()
    {
        return await _context.Clientes
            .Where(c => c.Activo)
            .Include(c => c.Persona)
            .Select(c => new ClienteDTO
            {
                Id = c.Id,
                NumeroCelular = c.Persona.Telefono ?? string.Empty,
                Nombre = c.Persona.Nombre ?? string.Empty,
                Apellido = c.Persona.Apellido ?? string.Empty,
                Colonia = c.Persona.Colonia ?? string.Empty,
                Calle = c.Persona.Calle ?? string.Empty,
                Numero = c.Persona.Numero ?? string.Empty,
                DistanciaAproximada = c.DistanciaEstimacion,
                FechaRegistro = c.Persona.FechaRegistro
            })
            .ToListAsync();
    }
}