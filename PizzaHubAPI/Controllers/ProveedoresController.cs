using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ProveedoresController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public ProveedoresController(PizzaHubContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<Proveedor>>> GetAll()
    {
        return await _context.Proveedores.ToListAsync();
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<Proveedor>> Get(int id)
    {
        var prov = await _context.Proveedores.FindAsync(id);
        if (prov == null) return NotFound();
        return prov;
    }

    [HttpPost]
    public async Task<ActionResult<Proveedor>> Create(Proveedor proveedor)
    {
        _context.Proveedores.Add(proveedor);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(Get), new { id = proveedor.Id }, proveedor);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> Update(int id, Proveedor proveedor)
    {
        if (id != proveedor.Id) return BadRequest();
        _context.Entry(proveedor).State = EntityState.Modified;
        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!await _context.Proveedores.AnyAsync(e => e.Id == id)) return NotFound();
            throw;
        }
        return NoContent();
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var prov = await _context.Proveedores.FindAsync(id);
        if (prov == null) return NotFound();
        _context.Proveedores.Remove(prov);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}
