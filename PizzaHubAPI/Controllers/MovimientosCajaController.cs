using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class MovimientosCajaController : ControllerBase
{
    private readonly PizzaHubContext _context;
    public MovimientosCajaController(PizzaHubContext context) { _context = context; }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<MovimientoCaja>>> GetAll() => await _context.MovimientosCaja.Include(m=>m.Sesion).ToListAsync();

    [HttpGet("{id}")]
    public async Task<ActionResult<MovimientoCaja>> Get(int id)
    {
        var m = await _context.MovimientosCaja.Include(x=>x.Sesion).FirstOrDefaultAsync(x=>x.Id==id);
        if (m == null) return NotFound();
        return m;
    }

    [HttpPost]
    public async Task<ActionResult<MovimientoCaja>> Create(MovimientoCaja movimiento)
    {
        movimiento.FechaRegistro = DateTime.UtcNow;
        _context.MovimientosCaja.Add(movimiento);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(Get), new { id = movimiento.Id }, movimiento);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var m = await _context.MovimientosCaja.FindAsync(id);
        if (m == null) return NotFound();
        _context.MovimientosCaja.Remove(m);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}
