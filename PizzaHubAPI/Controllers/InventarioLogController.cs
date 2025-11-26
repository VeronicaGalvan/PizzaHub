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
public class InventarioLogController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public InventarioLogController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/InventarioLog
    [HttpGet]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<InventarioLog>>> GetInventarioLogs()
    {
        return await _context.InventarioLogs
            .Include(i => i.Insumo)
            .OrderByDescending(i => i.Fecha)
            .ToListAsync();
    }

    // GET: api/InventarioLog/5
    [HttpGet("{id}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<InventarioLog>> GetInventarioLog(int id)
    {
        var inventarioLog = await _context.InventarioLogs
            .Include(i => i.Insumo)
            .FirstOrDefaultAsync(i => i.Id == id);

        if (inventarioLog == null)
        {
            return NotFound(new { message = "Registro de inventario no encontrado" });
        }

        return inventarioLog;
    }

    // POST: api/InventarioLog
    [HttpPost]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<InventarioLog>> RegistrarMovimiento(RegistrarMovimientoInventarioDto dto)
    {
        // Verificar que el insumo existe
        var insumo = await _context.Insumos.FindAsync(dto.InsumoId);
        if (insumo == null)
        {
            return BadRequest(new { message = "Insumo no encontrado" });
        }

        // Actualizar stock del insumo
        if (dto.TipoMovimiento == TipoMovimientoEnum.Entrada)
        {
            insumo.StockActual += dto.Cantidad;
        }
        else if (dto.TipoMovimiento == TipoMovimientoEnum.Salida)
        {
            if (insumo.StockActual < dto.Cantidad)
            {
                return BadRequest(new { message = "Stock insuficiente" });
            }
            insumo.StockActual -= dto.Cantidad;
        }

        insumo.UltimaActualizacion = DateTime.UtcNow;

        // Crear registro en el log
        var inventarioLog = new InventarioLog
        {
            InsumoId = dto.InsumoId,
            Cantidad = dto.Cantidad,
            TipoMovimiento = dto.TipoMovimiento,
            Motivo = dto.Motivo,
            Fecha = DateTime.UtcNow
        };

        _context.InventarioLogs.Add(inventarioLog);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetInventarioLog), new { id = inventarioLog.Id }, inventarioLog);
    }

    // GET: api/InventarioLog/insumo/5
    [HttpGet("insumo/{insumoId}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<InventarioLog>>> GetMovimientosPorInsumo(int insumoId)
    {
        var logs = await _context.InventarioLogs
            .Include(i => i.Insumo)
            .Where(i => i.InsumoId == insumoId)
            .OrderByDescending(i => i.Fecha)
            .ToListAsync();

        return logs;
    }

    // GET: api/InventarioLog/entradas
    [HttpGet("entradas")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<InventarioLog>>> GetEntradas()
    {
        return await _context.InventarioLogs
            .Include(i => i.Insumo)
            .Where(i => i.TipoMovimiento == TipoMovimientoEnum.Entrada)
            .OrderByDescending(i => i.Fecha)
            .ToListAsync();
    }

    // GET: api/InventarioLog/salidas
    [HttpGet("salidas")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<InventarioLog>>> GetSalidas()
    {
        return await _context.InventarioLogs
            .Include(i => i.Insumo)
            .Where(i => i.TipoMovimiento == TipoMovimientoEnum.Salida)
            .OrderByDescending(i => i.Fecha)
            .ToListAsync();
    }

    // GET: api/InventarioLog/fecha/{fecha}
    [HttpGet("fecha/{fecha}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<InventarioLog>>> GetMovimientosPorFecha(DateTime fecha)
    {
        var fechaBuscada = fecha.Date;
        var logs = await _context.InventarioLogs
            .Include(i => i.Insumo)
            .Where(i => i.Fecha.Date == fechaBuscada)
            .OrderBy(i => i.Fecha)
            .ToListAsync();

        return logs;
    }

    // DELETE: api/InventarioLog/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteInventarioLog(int id)
    {
        var inventarioLog = await _context.InventarioLogs
            .Include(i => i.Insumo)
            .FirstOrDefaultAsync(i => i.Id == id);

        if (inventarioLog == null)
        {
            return NotFound(new { message = "Registro de inventario no encontrado" });
        }

        // Revertir el movimiento en el stock
        if (inventarioLog.TipoMovimiento == TipoMovimientoEnum.Entrada)
        {
            inventarioLog.Insumo.StockActual -= inventarioLog.Cantidad;
        }
        else if (inventarioLog.TipoMovimiento == TipoMovimientoEnum.Salida)
        {
            inventarioLog.Insumo.StockActual += inventarioLog.Cantidad;
        }

        inventarioLog.Insumo.UltimaActualizacion = DateTime.UtcNow;

        _context.InventarioLogs.Remove(inventarioLog);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool InventarioLogExists(int id)
    {
        return _context.InventarioLogs.Any(e => e.Id == id);
    }
}
