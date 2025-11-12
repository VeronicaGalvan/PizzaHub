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
public class CalificacionesController : ControllerBase
{
    private readonly PizzaHubContext _context;
    
    public CalificacionesController(PizzaHubContext context) 
    { 
        _context = context; 
    }

    // GET: api/Calificaciones
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Calificacion>>> GetCalificaciones()
    {
        return await _context.Calificaciones
            .Include(c => c.Pedido)
            .OrderByDescending(c => c.Fecha)
            .ToListAsync();
    }

    // GET: api/Calificaciones/5
    [HttpGet("{id}")]
    public async Task<ActionResult<Calificacion>> GetCalificacion(int id)
    {
        var calificacion = await _context.Calificaciones
            .Include(c => c.Pedido)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (calificacion == null)
        {
            return NotFound(new { message = "Calificación no encontrada" });
        }

        return calificacion;
    }

    // POST: api/Calificaciones/pedido/5
    [HttpPost("pedido/{pedidoId}")]
    public async Task<ActionResult<Calificacion>> RegistrarCalificacion(int pedidoId, RegistrarCalificacionDto dto)
    {
        // Verificar que el pedido existe
        var pedido = await _context.Pedidos.FindAsync(pedidoId);
        if (pedido == null)
        {
            return BadRequest(new { message = "Pedido no encontrado" });
        }

        // Verificar que el pedido está entregado
        if (pedido.Estado != EstadoPedidoEnum.Entregado)
        {
            return BadRequest(new { message = "Solo se pueden calificar pedidos entregados" });
        }

        // Verificar que no exista ya una calificación para este pedido
        var calificacionExistente = await _context.Calificaciones
            .FirstOrDefaultAsync(c => c.PedidoId == pedidoId);

        if (calificacionExistente != null)
        {
            return BadRequest(new { message = "El pedido ya tiene una calificación" });
        }

        var calificacion = new Calificacion
        {
            PedidoId = pedidoId,
            Estrellas = dto.Estrellas,
            Comentario = dto.Comentario,
            Fecha = DateTime.Now
        };

        _context.Calificaciones.Add(calificacion);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetCalificacion), new { id = calificacion.Id }, calificacion);
    }

    // GET: api/Calificaciones/pedido/5
    [HttpGet("pedido/{pedidoId}")]
    public async Task<ActionResult<Calificacion>> GetCalificacionPorPedido(int pedidoId)
    {
        var calificacion = await _context.Calificaciones
            .Include(c => c.Pedido)
            .FirstOrDefaultAsync(c => c.PedidoId == pedidoId);

        if (calificacion == null)
        {
            return NotFound(new { message = "No se encontró calificación para este pedido" });
        }

        return calificacion;
    }

    // GET: api/Calificaciones/promedio
    [HttpGet("promedio")]
    public async Task<ActionResult<object>> GetPromedioCalificaciones()
    {
        var calificaciones = await _context.Calificaciones.ToListAsync();
        
        if (calificaciones.Count == 0)
        {
            return new { promedio = 0.0, total = 0 };
        }

        var promedio = calificaciones.Average(c => c.Estrellas);
        
        return new 
        { 
            promedio = Math.Round(promedio, 2), 
            total = calificaciones.Count 
        };
    }

    // GET: api/Calificaciones/estadisticas
    [HttpGet("estadisticas")]
    [Authorize(Roles = "Administrador")]
    public async Task<ActionResult<object>> GetEstadisticas()
    {
        var calificaciones = await _context.Calificaciones.ToListAsync();
        
        if (calificaciones.Count == 0)
        {
            return new 
            { 
                promedio = 0.0, 
                total = 0,
                porEstrellas = new Dictionary<int, int>()
            };
        }

        var promedio = calificaciones.Average(c => c.Estrellas);
        var porEstrellas = calificaciones
            .GroupBy(c => c.Estrellas)
            .ToDictionary(g => g.Key, g => g.Count());

        return new 
        { 
            promedio = Math.Round(promedio, 2), 
            total = calificaciones.Count,
            porEstrellas = porEstrellas
        };
    }

    // DELETE: api/Calificaciones/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteCalificacion(int id)
    {
        var calificacion = await _context.Calificaciones.FindAsync(id);
        if (calificacion == null)
        {
            return NotFound(new { message = "Calificación no encontrada" });
        }

        _context.Calificaciones.Remove(calificacion);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool CalificacionExists(int id)
    {
        return _context.Calificaciones.Any(e => e.Id == id);
    }
}
