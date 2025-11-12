using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]
public class ClientesController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public ClientesController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/Clientes
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Cliente>>> GetClientes()
    {
        return await _context.Clientes
            .Include(c => c.Usuario)
            .ToListAsync();
    }

    // GET: api/Clientes/5
    [HttpGet("{id}")]
    public async Task<ActionResult<Cliente>> GetCliente(int id)
    {
        var cliente = await _context.Clientes
            .Include(c => c.Usuario)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (cliente == null)
            return NotFound(new { message = "Cliente no encontrado" });

        return cliente;
    }

    // GET: api/Clientes/usuario/5
    [HttpGet("usuario/{usuarioId}")]
    public async Task<ActionResult<Cliente>> GetClientePorUsuario(int usuarioId)
    {
        var cliente = await _context.Clientes
            .Include(c => c.Usuario)
            .FirstOrDefaultAsync(c => c.UsuarioId == usuarioId);

        if (cliente == null)
            return NotFound(new { message = "Cliente no encontrado para el usuario" });

        return cliente;
    }

    // POST: api/Clientes
    [HttpPost]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Cliente>> CreateCliente(CrearClienteDto dto)
    {
        if (dto.UsuarioId.HasValue)
        {
            var usuario = await _context.Usuarios.FindAsync(dto.UsuarioId.Value);
            if (usuario == null)
                return BadRequest(new { message = "Usuario no encontrado" });
        }

        var cliente = new Cliente
        {
            Nombre = dto.Nombre,
            Apellidos = dto.Apellidos,
            Telefono = dto.Telefono,
            Colonia = dto.Colonia,
            Calle = dto.Calle,
            NumeroCasa = dto.NumeroCasa,
            Observaciones = dto.Observaciones,
            UsuarioId = dto.UsuarioId
        };

        _context.Clientes.Add(cliente);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetCliente), new { id = cliente.Id }, cliente);
    }

    // PUT: api/Clientes/5
    [HttpPut("{id}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<IActionResult> UpdateCliente(int id, CrearClienteDto dto)
    {
        var cliente = await _context.Clientes.FindAsync(id);
        if (cliente == null)
            return NotFound(new { message = "Cliente no encontrado" });

        cliente.Nombre = dto.Nombre;
        cliente.Apellidos = dto.Apellidos;
        cliente.Telefono = dto.Telefono;
        cliente.Colonia = dto.Colonia;
        cliente.Calle = dto.Calle;
        cliente.NumeroCasa = dto.NumeroCasa;
        cliente.Observaciones = dto.Observaciones;
        cliente.UsuarioId = dto.UsuarioId;

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!ClienteExists(id))
                return NotFound();
            throw;
        }

        return NoContent();
    }

    // DELETE: api/Clientes/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteCliente(int id)
    {
        var cliente = await _context.Clientes.FindAsync(id);
        if (cliente == null)
            return NotFound(new { message = "Cliente no encontrado" });

        _context.Clientes.Remove(cliente);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool ClienteExists(int id)
    {
        return _context.Clientes.Any(c => c.Id == id);
    }
}
