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
public class RepartidoresController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public RepartidoresController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/Repartidores
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Repartidor>>> GetRepartidores()
    {
        return await _context.Repartidores
            .Include(r => r.Usuario)
            .ToListAsync();
    }

    // GET: api/Repartidores/5
    [HttpGet("{id}")]
    public async Task<ActionResult<Repartidor>> GetRepartidor(int id)
    {
        var repartidor = await _context.Repartidores
            .Include(r => r.Usuario)
            .FirstOrDefaultAsync(r => r.Id == id);

        if (repartidor == null)
            return NotFound(new { message = "Repartidor no encontrado" });

        return repartidor;
    }

    // GET: api/Repartidores/disponibles
    [HttpGet("disponibles")]
    public async Task<ActionResult<IEnumerable<Repartidor>>> GetRepartidoresDisponibles()
    {
        return await _context.Repartidores
            .Include(r => r.Usuario)
            .Where(r => r.Estado == RepartidorEstadoEnum.Disponible)
            .ToListAsync();
    }

    // GET: api/Repartidores/usuario/5
    [HttpGet("usuario/{usuarioId}")]
    public async Task<ActionResult<Repartidor>> GetRepartidorPorUsuario(int usuarioId)
    {
        var repartidor = await _context.Repartidores
            .Include(r => r.Usuario)
            .FirstOrDefaultAsync(r => r.UsuarioId == usuarioId);

        if (repartidor == null)
            return NotFound(new { message = "Repartidor no encontrado para el usuario" });

        return repartidor;
    }

    // POST: api/Repartidores
    [HttpPost]
    [Authorize(Roles = "Administrador")]
    public async Task<ActionResult<Repartidor>> CreateRepartidor(CrearRepartidorDto dto)
    {
        var usuario = await _context.Usuarios.FindAsync(dto.UsuarioId);
        if (usuario == null)
            return BadRequest(new { message = "Usuario no encontrado" });

        // Verificar que no exista ya un repartidor para este usuario
        if (await _context.Repartidores.AnyAsync(r => r.UsuarioId == dto.UsuarioId))
            return BadRequest(new { message = "Ya existe un repartidor asociado a este usuario" });

        var repartidor = new Repartidor
        {
            Nombre = dto.Nombre,
            Apellidos = dto.Apellidos,
            Telefono = dto.Telefono,
            UsuarioId = dto.UsuarioId,
            Estado = RepartidorEstadoEnum.Disponible
        };

        _context.Repartidores.Add(repartidor);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetRepartidor), new { id = repartidor.Id }, repartidor);
    }

    // PUT: api/Repartidores/5
    [HttpPut("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> UpdateRepartidor(int id, CrearRepartidorDto dto)
    {
        var repartidor = await _context.Repartidores.FindAsync(id);
        if (repartidor == null)
            return NotFound(new { message = "Repartidor no encontrado" });

        repartidor.Nombre = dto.Nombre;
        repartidor.Apellidos = dto.Apellidos;
        repartidor.Telefono = dto.Telefono;

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!RepartidorExists(id))
                return NotFound();
            throw;
        }

        return NoContent();
    }

    // PATCH: api/Repartidores/5/estado
    [HttpPatch("{id}/estado")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<IActionResult> CambiarEstado(int id, [FromBody] CambiarEstadoRepartidorDto dto)
    {
        var repartidor = await _context.Repartidores.FindAsync(id);
        if (repartidor == null)
            return NotFound(new { message = "Repartidor no encontrado" });

        repartidor.Estado = dto.Estado;
        await _context.SaveChangesAsync();

        return NoContent();
    }

    // DELETE: api/Repartidores/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteRepartidor(int id)
    {
        var repartidor = await _context.Repartidores.FindAsync(id);
        if (repartidor == null)
            return NotFound(new { message = "Repartidor no encontrado" });

        // Marcar como inactivo en lugar de eliminar
        repartidor.Estado = RepartidorEstadoEnum.Inactivo;
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool RepartidorExists(int id)
    {
        return _context.Repartidores.Any(r => r.Id == id);
    }
}
