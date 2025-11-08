using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class MermasController : ControllerBase
{
    private readonly PizzaHubContext _context;
    public MermasController(PizzaHubContext context) { _context = context; }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<Merma>>> GetAll() => await _context.Mermas.Include(m=>m.MateriaPrima).ToListAsync();

    [HttpGet("{id}")]
    public async Task<ActionResult<Merma>> Get(int id)
    {
        var item = await _context.Mermas.Include(m=>m.MateriaPrima).FirstOrDefaultAsync(x => x.Id == id);
        if (item == null) return NotFound();
        return item;
    }

    [HttpPost]
    public async Task<ActionResult<Merma>> Create(Merma merma)
    {
        merma.FechaRegistro = DateTime.UtcNow;
        _context.Mermas.Add(merma);

        // Ajustar stock si existe la materia prima
        var materia = await _context.MateriasPrimas.FindAsync(merma.MateriaPrimaId);
        if (materia != null)
        {
            materia.CantidadActual -= merma.Cantidad;
            materia.FechaActualizacion = DateTime.UtcNow;
        }

        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(Get), new { id = merma.Id }, merma);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var m = await _context.Mermas.FindAsync(id);
        if (m == null) return NotFound();
        _context.Mermas.Remove(m);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}
