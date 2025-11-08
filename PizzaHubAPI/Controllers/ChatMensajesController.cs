using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ChatMensajesController : ControllerBase
{
    private readonly PizzaHubContext _context;
    public ChatMensajesController(PizzaHubContext context) { _context = context; }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<ChatMensaje>>> GetAll() => await _context.ChatMensajes.ToListAsync();

    [HttpGet("{id}")]
    public async Task<ActionResult<ChatMensaje>> Get(int id)
    {
        var m = await _context.ChatMensajes.FindAsync(id);
        if (m == null) return NotFound();
        return m;
    }

    [HttpPost]
    public async Task<ActionResult<ChatMensaje>> Create(ChatMensaje mensaje)
    {
        mensaje.Fecha = DateTime.UtcNow;
        _context.ChatMensajes.Add(mensaje);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(Get), new { id = mensaje.Id }, mensaje);
    }
}
