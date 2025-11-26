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
public class VentasController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public VentasController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/Ventas
    [HttpGet]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<Venta>>> GetVentas()
    {
        return await _context.Ventas
            .Include(v => v.Caja)
            .Include(v => v.Pedido)
            .Include(v => v.Empleado)
            .OrderByDescending(v => v.FechaVenta)
            .ToListAsync();
    }

    // GET: api/Ventas/5
    [HttpGet("{id}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Venta>> GetVenta(int id)
    {
        var venta = await _context.Ventas
            .Include(v => v.Caja)
            .Include(v => v.Pedido)
            .Include(v => v.Empleado)
            .FirstOrDefaultAsync(v => v.Id == id);

        if (venta == null)
        {
            return NotFound(new { message = "Venta no encontrada" });
        }

        return venta;
    }

    // POST: api/Ventas
    [HttpPost]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<Venta>> RegistrarVenta(RegistrarVentaDto dto)
    {
        // Verificar que la caja existe y está abierta
        var caja = await _context.Cajas.FindAsync(dto.CajaId);
        if (caja == null)
        {
            return BadRequest(new { message = "Caja no encontrada" });
        }

        if (caja.Estado != EstadoCajaEnum.Abierta)
        {
            return BadRequest(new { message = "La caja está cerrada" });
        }

        // Si hay pedido, verificar que existe
        if (dto.PedidoId.HasValue)
        {
            var pedido = await _context.Pedidos.FindAsync(dto.PedidoId.Value);
            if (pedido == null)
            {
                return BadRequest(new { message = "Pedido no encontrado" });
            }
        }

        // Si hay empleado, verificar que existe
        if (dto.EmpleadoId.HasValue)
        {
            var empleado = await _context.Empleados.FindAsync(dto.EmpleadoId.Value);
            if (empleado == null)
            {
                return BadRequest(new { message = "Empleado no encontrado" });
            }
        }

        var venta = new Venta
        {
            CajaId = dto.CajaId,
            PedidoId = dto.PedidoId,
            EmpleadoId = dto.EmpleadoId,
            MetodoPago = dto.MetodoPago,
            Total = dto.Total,
            FechaVenta = DateTime.UtcNow
        };

        _context.Ventas.Add(venta);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetVenta), new { id = venta.Id }, venta);
    }

    // GET: api/Ventas/caja/5
    [HttpGet("caja/{cajaId}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<Venta>>> GetVentasPorCaja(int cajaId)
    {
        var ventas = await _context.Ventas
            .Include(v => v.Pedido)
            .Include(v => v.Empleado)
            .Where(v => v.CajaId == cajaId)
            .OrderBy(v => v.FechaVenta)
            .ToListAsync();

        return ventas;
    }

    // GET: api/Ventas/fecha/{fecha}
    [HttpGet("fecha/{fecha}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<Venta>>> GetVentasPorFecha(DateTime fecha)
    {
        var fechaBuscada = fecha.Date;
        var ventas = await _context.Ventas
            .Include(v => v.Caja)
            .Include(v => v.Pedido)
            .Include(v => v.Empleado)
            .Where(v => v.FechaVenta.Date == fechaBuscada)
            .OrderBy(v => v.FechaVenta)
            .ToListAsync();

        return ventas;
    }

    // GET: api/Ventas/empleado/5
    [HttpGet("empleado/{empleadoId}")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<ActionResult<IEnumerable<Venta>>> GetVentasPorEmpleado(int empleadoId)
    {
        var ventas = await _context.Ventas
            .Include(v => v.Caja)
            .Include(v => v.Pedido)
            .Where(v => v.EmpleadoId == empleadoId)
            .OrderByDescending(v => v.FechaVenta)
            .ToListAsync();

        return ventas;
    }

    // DELETE: api/Ventas/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteVenta(int id)
    {
        var venta = await _context.Ventas
            .Include(v => v.Caja)
            .FirstOrDefaultAsync(v => v.Id == id);

        if (venta == null)
        {
            return NotFound(new { message = "Venta no encontrada" });
        }

        if (venta.Caja.Estado == EstadoCajaEnum.Cerrada)
        {
            return BadRequest(new { message = "No se puede eliminar una venta de una caja cerrada" });
        }

        _context.Ventas.Remove(venta);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool VentaExists(int id)
    {
        return _context.Ventas.Any(e => e.Id == id);
    }
}
