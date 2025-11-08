using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class HistorialEstadoPedidoController : ControllerBase
{
    private readonly PizzaHubContext _context;
    public HistorialEstadoPedidoController(PizzaHubContext context) { _context = context; }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<HistorialEstadoPedido>>> GetAll() => await _context.HistorialEstadoPedido.Include(h=>h.Usuario).ToListAsync();

    [HttpGet("{id}")]
    public async Task<ActionResult<HistorialEstadoPedido>> Get(int id)
    {
        var h = await _context.HistorialEstadoPedido.FindAsync(id);
        if (h == null) return NotFound();
        return h;
    }

    [HttpPost]
    public async Task<ActionResult<HistorialEstadoPedido>> Create(HistorialEstadoPedido historial)
    {
        historial.CreadoEn = DateTime.UtcNow;
        _context.HistorialEstadoPedido.Add(historial);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(Get), new { id = historial.Id }, historial);
    }
}
