using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class CalificacionesController : ControllerBase
{
    private readonly PizzaHubContext _context;
    public CalificacionesController(PizzaHubContext context) { _context = context; }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<CalificacionPedido>>> GetAll() => await _context.CalificacionesPedido.ToListAsync();

    [HttpGet("{id}")]
    public async Task<ActionResult<CalificacionPedido>> Get(int id)
    {
        var c = await _context.CalificacionesPedido.FindAsync(id);
        if (c == null) return NotFound();
        return c;
    }

    [HttpPost]
    public async Task<ActionResult<CalificacionPedido>> Create(CalificacionPedido cal)
    {
        cal.FechaRegistro = DateTime.UtcNow;
        _context.CalificacionesPedido.Add(cal);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(Get), new { id = cal.Id }, cal);
    }
}
