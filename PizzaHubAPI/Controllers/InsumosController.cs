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
public class InsumosController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public InsumosController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/Insumos
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Insumo>>> GetInsumos()
    {
        return await _context.Insumos.ToListAsync();
    }

    // GET: api/Insumos/5
    [HttpGet("{id}")]
    public async Task<ActionResult<Insumo>> GetInsumo(int id)
    {
        var insumo = await _context.Insumos.FindAsync(id);

        if (insumo == null)
        {
            return NotFound(new { message = "Insumo no encontrado" });
        }

        return insumo;
    }

    // POST: api/Insumos
    [HttpPost]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Insumo>> CreateInsumo(CrearInsumoDto dto)
    {
        var insumo = new Insumo
        {
            Nombre = dto.Nombre,
            UnidadMedida = dto.UnidadMedida,
            StockActual = dto.StockActual,
            StockMinimo = dto.StockMinimo
        };

        _context.Insumos.Add(insumo);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetInsumo), new { id = insumo.Id }, insumo);
    }

    // PUT: api/Insumos/5
    [HttpPut("{id}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<IActionResult> UpdateInsumo(int id, CrearInsumoDto dto)
    {
        var insumo = await _context.Insumos.FindAsync(id);
        if (insumo == null)
        {
            return NotFound(new { message = "Insumo no encontrado" });
        }

        insumo.Nombre = dto.Nombre;
        insumo.UnidadMedida = dto.UnidadMedida;
        insumo.StockActual = dto.StockActual;
        insumo.StockMinimo = dto.StockMinimo;
        insumo.UltimaActualizacion = DateTime.Now;

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!InsumoExists(id))
            {
                return NotFound();
            }
            throw;
        }

        return NoContent();
    }

    // DELETE: api/Insumos/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteInsumo(int id)
    {
        var insumo = await _context.Insumos.FindAsync(id);
        if (insumo == null)
        {
            return NotFound(new { message = "Insumo no encontrado" });
        }

        _context.Insumos.Remove(insumo);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    // GET: api/Insumos/bajo-stock
    [HttpGet("bajo-stock")]
    public async Task<ActionResult<IEnumerable<Insumo>>> GetInsumosBajoStock()
    {
        return await _context.Insumos
            .Where(i => i.StockActual <= i.StockMinimo)
            .ToListAsync();
    }

    private bool InsumoExists(int id)
    {
        return _context.Insumos.Any(e => e.Id == id);
    }
}
