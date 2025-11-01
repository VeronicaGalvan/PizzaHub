using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;
using PizzaHubAPI.Services;

namespace PizzaHubAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]
public class PedidosController : ControllerBase
{
    private readonly PizzaHubContext _context;
    private readonly PedidoService _pedidoService;

    public PedidosController(PizzaHubContext context, PedidoService pedidoService)
    {
        _context = context;
        _pedidoService = pedidoService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<PedidoResumenDTO>>> GetPedidos()
    {
        var usuarioId = int.Parse(User.FindFirst("UserId")?.Value ?? "0");
        var esAdmin = User.IsInRole("Admin");
        var esRepartidor = User.IsInRole("Repartidor");

        var query = _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Repartidor)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .AsQueryable();

        // Filtrar según el rol
        if (!esAdmin)
        {
            if (esRepartidor)
            {
                query = query.Where(p => p.RepartidorId == usuarioId);
            }
            else
            {
                query = query.Where(p => p.ClienteId == usuarioId);
            }
        }

        var pedidos = await query
            .OrderByDescending(p => p.CreadoEn)
            .Select(p => new PedidoResumenDTO
            {
                Id = p.Id,
                Estado = p.Estado.ToString(),
                Total = p.Total,
                DireccionEntrega = p.DireccionEntrega,
                TelefonoContacto = p.TelefonoContacto,
                CreadoEn = p.CreadoEn,
                Cliente = p.Cliente.NombreCompleto,
                Repartidor = p.Repartidor != null ? p.Repartidor.NombreCompleto : null,
                Detalles = p.Detalles.Select(d => new DetallePedidoResumenDTO
                {
                    Producto = d.Producto.Nombre,
                    Cantidad = d.Cantidad,
                    PrecioUnitario = d.PrecioUnitario,
                    Subtotal = d.Subtotal,
                    Observaciones = d.Observaciones
                }).ToList()
            })
            .ToListAsync();

        return pedidos;
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<PedidoResumenDTO>> GetPedido(int id)
    {
        var pedido = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Repartidor)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .FirstOrDefaultAsync(p => p.Id == id);

        if (pedido == null)
        {
            return NotFound();
        }

        var usuarioId = int.Parse(User.FindFirst("UserId")?.Value ?? "0");
        var esAdmin = User.IsInRole("Admin");
        var esRepartidor = User.IsInRole("Repartidor");

        // Verificar permisos
        if (!esAdmin && pedido.ClienteId != usuarioId && (!esRepartidor || pedido.RepartidorId != usuarioId))
        {
            return Forbid();
        }

        return new PedidoResumenDTO
        {
            Id = pedido.Id,
            Estado = pedido.Estado.ToString(),
            Total = pedido.Total,
            DireccionEntrega = pedido.DireccionEntrega,
            TelefonoContacto = pedido.TelefonoContacto,
            CreadoEn = pedido.CreadoEn,
            Cliente = pedido.Cliente.NombreCompleto,
            Repartidor = pedido.Repartidor?.NombreCompleto,
            Detalles = pedido.Detalles.Select(d => new DetallePedidoResumenDTO
            {
                Producto = d.Producto.Nombre,
                Cantidad = d.Cantidad,
                PrecioUnitario = d.PrecioUnitario,
                Subtotal = d.Subtotal,
                Observaciones = d.Observaciones
            }).ToList()
        };
    }

    [HttpPost]
    public async Task<ActionResult<PedidoResumenDTO>> CreatePedido(CrearPedidoDTO pedidoDto)
    {
        var usuarioId = int.Parse(User.FindFirst("UserId")?.Value ?? "0");

        // Validar productos y calcular total
        var detalles = new List<DetallePedido>();
        decimal total = 0;

        foreach (var detalle in pedidoDto.Detalles)
        {
            var producto = await _context.Productos.FindAsync(detalle.ProductoId);
            if (producto == null || !producto.Activo)
            {
                return BadRequest($"Producto {detalle.ProductoId} no encontrado o no disponible");
            }

            if (producto.StockActual < detalle.Cantidad)
            {
                return BadRequest($"Stock insuficiente para el producto {producto.Nombre}");
            }

            var subtotal = producto.Precio * detalle.Cantidad;
            total += subtotal;

            detalles.Add(new DetallePedido
            {
                ProductoId = detalle.ProductoId,
                Cantidad = detalle.Cantidad,
                PrecioUnitario = producto.Precio,
                Subtotal = subtotal,
                Observaciones = detalle.Observaciones
            });

            // Actualizar stock
            producto.StockActual -= detalle.Cantidad;
        }

        var pedido = new Pedido
        {
            ClienteId = usuarioId,
            DireccionEntrega = pedidoDto.DireccionEntrega,
            TelefonoContacto = pedidoDto.TelefonoContacto,
            Observaciones = pedidoDto.Observaciones,
            Total = total,
            Estado = EstadoPedido.PENDIENTE,
            CreadoEn = DateTime.UtcNow,
            Detalles = detalles
        };

        _context.Pedidos.Add(pedido);

        // Registrar estado inicial en historial
        var historial = new HistorialEstadoPedido
        {
            PedidoId = pedido.Id,
            Estado = EstadoPedido.PENDIENTE,
            UsuarioId = usuarioId,
            CreadoEn = DateTime.UtcNow
        };

        _context.HistorialEstadoPedido.Add(historial);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetPedido), new { id = pedido.Id }, 
            await GetPedido(pedido.Id));
    }

    [HttpPut("{id}/estado")]
    [Authorize(Roles = "Admin,Repartidor")]
    public async Task<IActionResult> UpdateEstadoPedido(int id, ActualizarEstadoPedidoDTO actualizacionDto)
    {
        var pedido = await _context.Pedidos.FindAsync(id);
        if (pedido == null)
        {
            return NotFound();
        }

        var usuarioId = int.Parse(User.FindFirst("UserId")?.Value ?? "0");
        var esAdmin = User.IsInRole("Admin");

        // Verificar permisos
        if (!esAdmin && (pedido.RepartidorId != usuarioId || actualizacionDto.NuevoEstado == EstadoPedido.PREPARACION))
        {
            return Forbid();
        }

        var resultado = await _pedidoService.ActualizarEstadoPedido(
            pedido, 
            actualizacionDto.NuevoEstado, 
            usuarioId,
            actualizacionDto.Observaciones
        );

        if (!resultado)
        {
            return BadRequest("No se puede actualizar el estado del pedido");
        }

        return NoContent();
    }
}