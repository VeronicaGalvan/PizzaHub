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

        // Crear el cliente
        var cliente = new Cliente
        {
            UsuarioId = usuario.Id,
            NumeroCelular = registro.NumeroCelular,
            Nombre = registro.Nombre,
            Apellido = registro.Apellido,
            Colonia = registro.Colonia,
            Calle = registro.Calle,
            Numero = registro.Numero,
            DistanciaAproximada = registro.DistanciaAproximada,
            FechaRegistro = DateTime.UtcNow
        };

        _context.Clientes.Add(cliente);

        // Asignar rol de Cliente
        var rolCliente = await _context.Roles.FirstOrDefaultAsync(r => r.Nombre == "Cliente");
        if (rolCliente == null)
        {
            return BadRequest("Error al asignar rol de cliente.");
        }

        _context.UsuariosRoles.Add(new UsuarioRol
        {
            UsuarioId = usuario.Id,
            RolId = rolCliente.Id
        });

        await _context.SaveChangesAsync();

        return Ok(authResponse);
    }

    [Authorize]
    [HttpGet("perfil")]
    public async Task<ActionResult<ClienteDTO>> GetPerfil()
    {
        var usuarioId = int.Parse(User.FindFirst("UserId")?.Value ?? "0");
        
        var cliente = await _context.Clientes
            .FirstOrDefaultAsync(c => c.UsuarioId == usuarioId);

        if (cliente == null)
        {
            return NotFound("Cliente no encontrado");
        }

        return new ClienteDTO
        {
            Id = cliente.Id,
            NumeroCelular = cliente.NumeroCelular,
            Nombre = cliente.Nombre,
            Apellido = cliente.Apellido,
            Colonia = cliente.Colonia,
            Calle = cliente.Calle,
            Numero = cliente.Numero,
            DistanciaAproximada = cliente.DistanciaAproximada,
            FechaRegistro = cliente.FechaRegistro
        };
    }

    [Authorize(Roles = "Admin")]
    [HttpGet]
    public async Task<ActionResult<IEnumerable<ClienteDTO>>> GetClientes()
    {
        return await _context.Clientes
            .Where(c => c.Activo)
            .Select(c => new ClienteDTO
            {
                Id = c.Id,
                NumeroCelular = c.NumeroCelular,
                Nombre = c.Nombre,
                Apellido = c.Apellido,
                Colonia = c.Colonia,
                Calle = c.Calle,
                Numero = c.Numero,
                DistanciaAproximada = c.DistanciaAproximada,
                FechaRegistro = c.FechaRegistro
            })
            .ToListAsync();
    }
}