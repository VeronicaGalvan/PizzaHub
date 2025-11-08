using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class MateriasPrimasController : ControllerBase
{
    private readonly PizzaHubContext _context;
    public MateriasPrimasController(PizzaHubContext context) { _context = context; }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<MateriaPrima>>> GetAll() => await _context.MateriasPrimas.Include(m=>m.Proveedor).ToListAsync();

    [HttpGet("{id}")]
    public async Task<ActionResult<MateriaPrima>> Get(int id)
    {
        var m = await _context.MateriasPrimas.Include(x=>x.Proveedor).FirstOrDefaultAsync(x => x.Id == id);
        if (m == null) return NotFound();
        return m;
    }

    [HttpPost]
    public async Task<ActionResult<MateriaPrima>> Create(CrearMateriaPrimaDTO materiaDto)
    {
        var materia = new MateriaPrima
        {
            CantidadActual = materiaDto.CantidadActual,
            Nombre = materiaDto.Nombre,
            ProveedorId = materiaDto.ProveedorId,
            PuntoReorden = materiaDto.PuntoReorden,
            UnidadMedida = materiaDto.UnidadMedida,
            FechaActualizacion = DateTime.UtcNow
        };

        _context.MateriasPrimas.Add(materia);
        await _context.SaveChangesAsync();
        // load proveedor for response
        await _context.Entry(materia).Reference(m => m.Proveedor).LoadAsync();
        return CreatedAtAction(nameof(Get), new { id = materia.Id }, materia);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> Update(int id, MateriaPrima materia)
    {
        if (id != materia.Id) return BadRequest();
        materia.FechaActualizacion = DateTime.UtcNow;
        _context.Entry(materia).State = EntityState.Modified;
        try { await _context.SaveChangesAsync(); }
        catch (DbUpdateConcurrencyException)
        {
            if (!await _context.MateriasPrimas.AnyAsync(e => e.Id == id)) return NotFound();
            throw;
        }
        return NoContent();
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var m = await _context.MateriasPrimas.FindAsync(id);
        if (m == null) return NotFound();
        _context.MateriasPrimas.Remove(m);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}
