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
public class EmpleadosController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public EmpleadosController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/Empleados
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Empleado>>> GetEmpleados()
    {
        return await _context.Empleados
            .Include(e => e.Usuario)
            .ToListAsync();
    }

    // GET: api/Empleados/5
    [HttpGet("{id}")]
    public async Task<ActionResult<Empleado>> GetEmpleado(int id)
    {
        var empleado = await _context.Empleados
            .Include(e => e.Usuario)
            .FirstOrDefaultAsync(e => e.Id == id);

        if (empleado == null)
        {
            return NotFound(new { message = "Empleado no encontrado" });
        }

        return empleado;
    }

    // POST: api/Empleados
    [HttpPost]
    [Authorize(Roles = "Administrador")]
    public async Task<ActionResult<Empleado>> CreateEmpleado(CrearEmpleadoDto dto)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }
        // Verificar que el usuario existe
        var usuario = await _context.Usuarios.FindAsync(dto.UsuarioId);
        if (usuario == null)
        {
            return BadRequest(new { message = "Usuario no encontrado" });
        }

        var empleado = new Empleado
        {
            Nombre = dto.Nombre,
            Apellidos = dto.Apellidos,
            Telefono = dto.Telefono,
            UsuarioId = dto.UsuarioId,
            FechaIngreso = DateTime.Now.Date,
            Activo = true
        };

        _context.Empleados.Add(empleado);
        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateException ex)
        {
            return Problem(detail: ex.InnerException?.Message ?? ex.Message, title: "Error al guardar Empleado");
        }
        catch (Exception ex)
        {
            return Problem(detail: ex.Message, title: "Error inesperado al crear Empleado");
        }

        return CreatedAtAction(nameof(GetEmpleado), new { id = empleado.Id }, empleado);
    }

    // PUT: api/Empleados/5
    [HttpPut("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> UpdateEmpleado(int id, CrearEmpleadoDto dto)
    {
        var empleado = await _context.Empleados.FindAsync(id);
        if (empleado == null)
        {
            return NotFound(new { message = "Empleado no encontrado" });
        }

        empleado.Nombre = dto.Nombre;
        empleado.Apellidos = dto.Apellidos;
        empleado.Telefono = dto.Telefono;

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!EmpleadoExists(id))
            {
                return NotFound();
            }
            throw;
        }

        return NoContent();
    }

    // DELETE: api/Empleados/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteEmpleado(int id)
    {
        var empleado = await _context.Empleados.FindAsync(id);
        if (empleado == null)
        {
            return NotFound(new { message = "Empleado no encontrado" });
        }

        // Soft delete
        empleado.Activo = false;
        await _context.SaveChangesAsync();

        return NoContent();
    }

    // GET: api/Empleados/activos
    [HttpGet("activos")]
    public async Task<ActionResult<IEnumerable<Empleado>>> GetEmpleadosActivos()
    {
        return await _context.Empleados
            .Include(e => e.Usuario)
            .Where(e => e.Activo)
            .ToListAsync();
    }

    private bool EmpleadoExists(int id)
    {
        return _context.Empleados.Any(e => e.Id == id);
    }
}
