using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize(Roles = "Administrador,Empleado")]
public class ComprasInsumosController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public ComprasInsumosController(PizzaHubContext context)
    {
        _context = context;
    }

    // GET: api/ComprasInsumos
    /// <summary>
    /// Obtiene todas las compras de insumos registradas
    /// </summary>
    [HttpGet]
    public async Task<ActionResult<IEnumerable<CompraInsumo>>> GetCompras()
    {
        return await _context.ComprasInsumos
            .Include(c => c.Detalles)
                .ThenInclude(d => d.Insumo)
            .Include(c => c.Empleado)
            .OrderByDescending(c => c.FechaCompra)
            .ToListAsync();
    }

    // GET: api/ComprasInsumos/5
    /// <summary>
    /// Obtiene una compra específica por su ID
    /// </summary>
    [HttpGet("{id}")]
    public async Task<ActionResult<CompraInsumo>> GetCompra(int id)
    {
        var compra = await _context.ComprasInsumos
            .Include(c => c.Detalles)
                .ThenInclude(d => d.Insumo)
            .Include(c => c.Empleado)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (compra == null)
        {
            return NotFound(new { message = "Compra no encontrada" });
        }

        return compra;
    }

    // POST: api/ComprasInsumos
    /// <summary>
    /// Registra una nueva compra de múltiples insumos (tipo carrito)
    /// Actualiza automáticamente el inventario y crea los registros en el log
    /// </summary>
    [HttpPost]
    public async Task<ActionResult<CompraInsumo>> RegistrarCompra(RegistrarCompraInsumosDto dto)
    {
        // Validar que haya al menos un detalle
        if (dto.Detalles == null || !dto.Detalles.Any())
        {
            return BadRequest(new { message = "Debe incluir al menos un insumo en la compra" });
        }

        // Validar que todos los insumos existan
        var insumosIds = dto.Detalles.Select(d => d.InsumoId).Distinct().ToList();
        var insumos = await _context.Insumos
            .Where(i => insumosIds.Contains(i.Id))
            .ToListAsync();

        if (insumos.Count != insumosIds.Count)
        {
            return BadRequest(new { message = "Uno o más insumos no existen" });
        }

        // Obtener el empleado autenticado
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        var empleado = await _context.Empleados
            .FirstOrDefaultAsync(e => e.UsuarioId == userId);

        using var transaction = await _context.Database.BeginTransactionAsync();
        try
        {
            // Crear la compra
            var compra = new CompraInsumo
            {
                Proveedor = dto.Proveedor,
                NumeroFactura = dto.NumeroFactura,
                Observaciones = dto.Observaciones,
                EmpleadoId = empleado?.Id,
                FechaCompra = DateTime.UtcNow,
                Total = 0 // Se calculará después
            };

            _context.ComprasInsumos.Add(compra);
            await _context.SaveChangesAsync(); // Guardar para obtener el ID

            decimal total = 0;

            // Crear los detalles y actualizar inventario
            foreach (var detalleDto in dto.Detalles)
            {
                var subtotal = detalleDto.Cantidad * detalleDto.PrecioUnitario;
                total += subtotal;

                // Crear detalle de compra
                var detalle = new DetalleCompraInsumo
                {
                    CompraId = compra.Id,
                    InsumoId = detalleDto.InsumoId,
                    Cantidad = detalleDto.Cantidad,
                    PrecioUnitario = detalleDto.PrecioUnitario,
                    Subtotal = subtotal
                };

                _context.DetallesCompraInsumos.Add(detalle);

                // Actualizar stock del insumo
                var insumo = insumos.First(i => i.Id == detalleDto.InsumoId);
                insumo.StockActual += detalleDto.Cantidad;
                insumo.UltimaActualizacion = DateTime.UtcNow;

                // Crear registro en el log de inventario
                var log = new InventarioLog
                {
                    InsumoId = detalleDto.InsumoId,
                    Cantidad = detalleDto.Cantidad,
                    TipoMovimiento = TipoMovimientoEnum.Entrada,
                    Motivo = $"Compra #{compra.Id} - {dto.Proveedor}",
                    Fecha = DateTime.UtcNow
                };

                _context.InventarioLogs.Add(log);
            }

            // Actualizar el total de la compra
            compra.Total = total;

            await _context.SaveChangesAsync();
            await transaction.CommitAsync();

            // Recargar la compra con sus relaciones para devolverla completa
            var compraCompleta = await _context.ComprasInsumos
                .Include(c => c.Detalles)
                    .ThenInclude(d => d.Insumo)
                .Include(c => c.Empleado)
                .FirstOrDefaultAsync(c => c.Id == compra.Id);

            return CreatedAtAction(nameof(GetCompra), new { id = compra.Id }, compraCompleta);
        }
        catch (Exception)
        {
            await transaction.RollbackAsync();
            throw;
        }
    }

    // GET: api/ComprasInsumos/proveedor/{proveedor}
    /// <summary>
    /// Obtiene todas las compras de un proveedor específico
    /// </summary>
    [HttpGet("proveedor/{proveedor}")]
    public async Task<ActionResult<IEnumerable<CompraInsumo>>> GetComprasPorProveedor(string proveedor)
    {
        var compras = await _context.ComprasInsumos
            .Include(c => c.Detalles)
                .ThenInclude(d => d.Insumo)
            .Where(c => c.Proveedor.Contains(proveedor))
            .OrderByDescending(c => c.FechaCompra)
            .ToListAsync();

        return compras;
    }

    // GET: api/ComprasInsumos/fecha/{fecha}
    /// <summary>
    /// Obtiene todas las compras de una fecha específica
    /// </summary>
    [HttpGet("fecha/{fecha}")]
    public async Task<ActionResult<IEnumerable<CompraInsumo>>> GetComprasPorFecha(DateTime fecha)
    {
        var fechaBuscada = fecha.Date;
        var compras = await _context.ComprasInsumos
            .Include(c => c.Detalles)
                .ThenInclude(d => d.Insumo)
            .Where(c => c.FechaCompra.Date == fechaBuscada)
            .OrderBy(c => c.FechaCompra)
            .ToListAsync();

        return compras;
    }

    // DELETE: api/ComprasInsumos/5
    /// <summary>
    /// Elimina una compra y revierte los cambios en el inventario
    /// </summary>
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeleteCompra(int id)
    {
        var compra = await _context.ComprasInsumos
            .Include(c => c.Detalles)
                .ThenInclude(d => d.Insumo)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (compra == null)
        {
            return NotFound(new { message = "Compra no encontrada" });
        }

        using var transaction = await _context.Database.BeginTransactionAsync();
        try
        {
            // Revertir el stock de cada insumo
            foreach (var detalle in compra.Detalles)
            {
                detalle.Insumo.StockActual -= detalle.Cantidad;
                detalle.Insumo.UltimaActualizacion = DateTime.UtcNow;

                // Eliminar el log de inventario asociado
                var logs = await _context.InventarioLogs
                    .Where(l => l.InsumoId == detalle.InsumoId 
                           && l.Motivo != null 
                           && l.Motivo.Contains($"Compra #{compra.Id}"))
                    .ToListAsync();

                _context.InventarioLogs.RemoveRange(logs);
            }

            _context.ComprasInsumos.Remove(compra);
            await _context.SaveChangesAsync();
            await transaction.CommitAsync();

            return NoContent();
        }
        catch (Exception)
        {
            await transaction.RollbackAsync();
            throw;
        }
    }
}
