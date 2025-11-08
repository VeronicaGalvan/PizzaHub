using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class SesionesCajaController : ControllerBase
{
    private readonly PizzaHubContext _context;
    public SesionesCajaController(PizzaHubContext context) { _context = context; }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<SesionCaja>>> GetAll() => await _context.SesionesCaja.ToListAsync();

    [HttpGet("{id}")]
    public async Task<ActionResult<SesionCaja>> Get(int id)
    {
        var s = await _context.SesionesCaja.FindAsync(id);
        if (s == null) return NotFound();
        return s;
    }

    [HttpPost]
    public async Task<ActionResult<SesionCaja>> Create(SesionCaja sesion)
    {
        if (sesion.FechaApertura == default) sesion.FechaApertura = DateTime.UtcNow;
        _context.SesionesCaja.Add(sesion);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(Get), new { id = sesion.Id }, sesion);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> Update(int id, SesionCaja sesion)
    {
        if (id != sesion.Id) return BadRequest();
        _context.Entry(sesion).State = EntityState.Modified;
        try { await _context.SaveChangesAsync(); }
        catch (DbUpdateConcurrencyException)
        {
            if (!await _context.SesionesCaja.AnyAsync(e => e.Id == id)) return NotFound();
            throw;
        }
        return NoContent();
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var s = await _context.SesionesCaja.FindAsync(id);
        if (s == null) return NotFound();
        _context.SesionesCaja.Remove(s);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}
