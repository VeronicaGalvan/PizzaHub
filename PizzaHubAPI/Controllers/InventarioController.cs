using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Controllers;

[Authorize]
[ApiController]
[Route("api/[controller]")]
public class InventarioController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public InventarioController(PizzaHubContext context)
    {
        _context = context;
    }

    [HttpGet("movimientos")]
    public async Task<ActionResult<IEnumerable<MovimientoInventario>>> GetMovimientos()
    {
        return await _context.MovimientosInventario
            .Include(m => m.Producto)
            .Include(m => m.Usuario)
            .OrderByDescending(m => m.CreadoEn)
            .ToListAsync();
    }

    [HttpGet("movimientos/{id}")]
    public async Task<ActionResult<MovimientoInventario>> GetMovimiento(int id)
    {
        var movimiento = await _context.MovimientosInventario
            .Include(m => m.Producto)
            .Include(m => m.Usuario)
            .FirstOrDefaultAsync(m => m.Id == id);

        if (movimiento == null)
        {
            return NotFound();
        }

        return movimiento;
    }

    [Authorize(Roles = "Admin")]
    [HttpPost("movimientos")]
    public async Task<ActionResult<MovimientoInventario>> CreateMovimiento(MovimientoInventario movimiento)
    {
        var producto = await _context.Productos.FindAsync(movimiento.ProductoId);
        if (producto == null || !producto.Activo)
        {
            return BadRequest("Producto no encontrado o inactivo");
        }

        // Actualizar stock del producto
        switch (movimiento.Tipo)
        {
            case TipoMovimiento.Entrada:
                producto.StockActual += movimiento.Cantidad;
                break;
            case TipoMovimiento.Salida:
                if (producto.StockActual < movimiento.Cantidad)
                {
                    return BadRequest("Stock insuficiente");
                }
                producto.StockActual -= movimiento.Cantidad;
                break;
            case TipoMovimiento.Ajuste:
                producto.StockActual = movimiento.Cantidad;
                break;
        }

        // Asignar usuario que realiza el movimiento
        var usuarioId = int.Parse(User.FindFirst("UserId")?.Value ?? "0");
        movimiento.UsuarioId = usuarioId;
        movimiento.CreadoEn = DateTime.UtcNow;

        _context.MovimientosInventario.Add(movimiento);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetMovimiento), new { id = movimiento.Id }, movimiento);
    }

    [HttpGet("productos-bajo-stock")]
    public async Task<ActionResult<IEnumerable<Producto>>> GetProductosBajoStock()
    {
        return await _context.Productos
            .Where(p => p.Activo && p.StockActual <= p.StockMinimo)
            .ToListAsync();
    }
}