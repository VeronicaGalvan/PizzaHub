using System.Security.Claims;
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
public class PedidosNewController : ControllerBase
{
    private readonly PizzaHubContext _context;
    private readonly NotificacionService _notificacionService;

    public PedidosNewController(
        PizzaHubContext context,
        NotificacionService notificacionService)
    {
        _context = context;
        _notificacionService = notificacionService;
    }

    // GET: api/PedidosNew
    [HttpGet]
    public async Task<ActionResult<IEnumerable<PedidoCompletoDto>>> GetPedidos()
    {
        var pedidos = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Repartidor)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .OrderByDescending(p => p.FechaPedido)
            .ToListAsync();

        return pedidos.Select(p => MapToPedidoCompletoDto(p)).ToList();
    }

    // GET: api/PedidosNew/5
    [HttpGet("{id}")]
    public async Task<ActionResult<PedidoCompletoDto>> GetPedido(int id)
    {
        var pedido = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Repartidor)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .FirstOrDefaultAsync(p => p.Id == id);

        if (pedido == null)
        {
            return NotFound(new { message = "Pedido no encontrado" });
        }

        // Obtener id del usuario actual (si está presente)
        var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        int userId = 0;
        if (!string.IsNullOrEmpty(userIdClaim) && int.TryParse(userIdClaim, out var parsed))
        {
            userId = parsed;
        }

        // Si es un cliente, solo puede ver sus propios pedidos
        if (User.IsInRole("Cliente"))
        {
            if (!pedido.ClienteId.HasValue || pedido.ClienteId.Value != userId)
            {
                return Forbid();
            }
        }

        // Si es un repartidor, solo puede ver pedidos asignados a él
        if (User.IsInRole("Repartidor"))
        {
            if (!pedido.RepartidorId.HasValue || pedido.RepartidorId.Value != userId)
            {
                return Forbid();
            }
        }

        // Administradores y empleados pueden ver cualquier pedido
        return MapToPedidoCompletoDto(pedido);
    }

    // POST: api/PedidosNew/5/repetir
    [HttpPost("{id}/repetir")]
    [Authorize(Roles = "Cliente")]
    public async Task<ActionResult<PedidoCompletoDto>> RepetirPedido(int id)
    {
        var pedidoOriginal = await _context.Pedidos
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .FirstOrDefaultAsync(p => p.Id == id);

        if (pedidoOriginal == null)
            return NotFound(new { message = "Pedido original no encontrado" });

        var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (string.IsNullOrEmpty(userIdClaim) || !int.TryParse(userIdClaim, out var userId))
            return Unauthorized(new { message = "Usuario no identificado" });

        // Solo el cliente propietario puede repetir su pedido
        if (!pedidoOriginal.ClienteId.HasValue || pedidoOriginal.ClienteId.Value != userId)
            return Forbid();

        // Validar productos y calcular totales
        decimal total = 0;
        var detalles = new List<DetallePedido>();

        foreach (var d in pedidoOriginal.Detalles)
        {
            var producto = await _context.Productos.FindAsync(d.ProductoId);
            if (producto == null || !producto.Activo)
            {
                return BadRequest(new { message = $"Producto {d.ProductoId} no disponible para repetir pedido" });
            }

            var subtotal = producto.Precio * d.Cantidad;
            total += subtotal;

            detalles.Add(new DetallePedido
            {
                ProductoId = d.ProductoId,
                Cantidad = d.Cantidad,
                Subtotal = subtotal
            });
        }

        var nuevoPedido = new Pedido
        {
            ClienteId = userId,
            Tipo = pedidoOriginal.Tipo,
            Estado = EstadoPedidoEnum.Pendiente,
            MetodoPago = pedidoOriginal.MetodoPago,
            Origen = pedidoOriginal.Origen,
            Total = total,
            DireccionEntrega = pedidoOriginal.DireccionEntrega,
            Observaciones = pedidoOriginal.Observaciones,
            FechaPedido = DateTime.UtcNow,
            Detalles = detalles
        };

        _context.Pedidos.Add(nuevoPedido);
        await _context.SaveChangesAsync();

        await _context.Entry(nuevoPedido)
            .Collection(p => p.Detalles)
            .Query()
            .Include(d => d.Producto)
            .LoadAsync();

        await _context.Entry(nuevoPedido).Reference(p => p.Cliente).LoadAsync();

        // Notificar al cliente que el pedido fue registrado
        if (nuevoPedido.ClienteId.HasValue)
        {
            await _notificacionService.NotificarCambioEstadoPedidoAsync(nuevoPedido.Id, EstadoPedidoEnum.Pendiente);
        }

        return CreatedAtAction(nameof(GetPedido), new { id = nuevoPedido.Id }, MapToPedidoCompletoDto(nuevoPedido));
    }

    // POST: api/PedidosNew/registrar
    [HttpPost("registrar")]
    public async Task<ActionResult<PedidoCompletoDto>> RegistrarPedido(RegistrarPedidoDto dto)
    {
        // Validar que hay al menos un detalle
        if (dto.Detalles == null || dto.Detalles.Count == 0)
        {
            return BadRequest(new { message = "El pedido debe tener al menos un producto" });
        }

        // Si hay cliente, verificar que existe
        if (dto.ClienteId.HasValue)
        {
            var cliente = await _context.Clientes.FindAsync(dto.ClienteId.Value);
            if (cliente == null)
            {
                return BadRequest(new { message = "Cliente no encontrado" });
            }
        }

        // Calcular el total del pedido
        decimal total = 0;
        var detalles = new List<DetallePedido>();

        foreach (var detalleDto in dto.Detalles)
        {
            var producto = await _context.Productos.FindAsync(detalleDto.ProductoId);
            if (producto == null)
            {
                return BadRequest(new { message = $"Producto {detalleDto.ProductoId} no encontrado" });
            }

            if (!producto.Activo)
            {
                return BadRequest(new { message = $"Producto {producto.Nombre} no está activo" });
            }

            var subtotal = producto.Precio * detalleDto.Cantidad;
            total += subtotal;

            detalles.Add(new DetallePedido
            {
                ProductoId = detalleDto.ProductoId,
                Cantidad = detalleDto.Cantidad,
                Subtotal = subtotal
            });
        }

        // Crear el pedido
        var pedido = new Pedido
        {
            ClienteId = dto.ClienteId,
            Tipo = dto.Tipo,
            Estado = EstadoPedidoEnum.Pendiente,
            MetodoPago = dto.MetodoPago,
            Origen = dto.Origen,
            Total = total,
            DireccionEntrega = dto.DireccionEntrega,
            Observaciones = dto.Observaciones,
            FechaPedido = DateTime.UtcNow,
            Detalles = detalles
        };

        _context.Pedidos.Add(pedido);
        await _context.SaveChangesAsync();

        // Recargar el pedido con todas las relaciones
        await _context.Entry(pedido)
            .Collection(p => p.Detalles)
            .Query()
            .Include(d => d.Producto)
            .LoadAsync();

        await _context.Entry(pedido).Reference(p => p.Cliente).LoadAsync();

        // 🔔 Notificar al cliente que el pedido fue registrado
        if (pedido.ClienteId.HasValue)
        {
            await _notificacionService.NotificarCambioEstadoPedidoAsync(
                pedido.Id,
                EstadoPedidoEnum.Pendiente
            );
        }

        return CreatedAtAction(nameof(GetPedido), new { id = pedido.Id }, MapToPedidoCompletoDto(pedido));
    }

    // PUT: api/PedidosNew/5/asignar-repartidor
    [HttpPut("{id}/asignar-repartidor")]
    [Authorize(Roles = "Administrador,Empleado")]
    public async Task<IActionResult> AsignarRepartidor(int id, AsignarRepartidorDto dto)
    {
        var pedido = await _context.Pedidos.FindAsync(id);
        if (pedido == null)
        {
            return NotFound(new { message = "Pedido no encontrado" });
        }

        // Verificar que el repartidor existe
        var repartidor = await _context.Repartidores.FindAsync(dto.RepartidorId);
        if (repartidor == null)
        {
            return BadRequest(new { message = "Repartidor no encontrado" });
        }

        // Verificar que el repartidor está disponible
        if (repartidor.Estado != RepartidorEstadoEnum.Disponible)
        {
            return BadRequest(new { message = "El repartidor no está disponible" });
        }

        pedido.RepartidorId = dto.RepartidorId;
        
        // Cambiar estado del pedido si está pendiente
        if (pedido.Estado == EstadoPedidoEnum.Pendiente)
        {
            pedido.Estado = EstadoPedidoEnum.EnPreparacion;
        }

        // Cambiar estado del repartidor a ocupado
        repartidor.Estado = RepartidorEstadoEnum.Ocupado;

        await _context.SaveChangesAsync();

        return NoContent();
    }

    // PUT: api/PedidosNew/5/estado
    [HttpPut("{id}/estado")]
    [Authorize(Roles = "Administrador,Empleado,Repartidor")]
    public async Task<IActionResult> CambiarEstado(int id, [FromBody] EstadoPedidoEnum nuevoEstado)
    {
        var pedido = await _context.Pedidos
            .Include(p => p.Repartidor)
            .FirstOrDefaultAsync(p => p.Id == id);

        if (pedido == null)
        {
            return NotFound(new { message = "Pedido no encontrado" });
        }

        pedido.Estado = nuevoEstado;

        // Si el pedido se entrega o cancela, liberar al repartidor
        if ((nuevoEstado == EstadoPedidoEnum.Entregado || nuevoEstado == EstadoPedidoEnum.Cancelado)
            && pedido.Repartidor != null)
        {
            pedido.Repartidor.Estado = RepartidorEstadoEnum.Disponible;
        }

        await _context.SaveChangesAsync();

        // Enviar notificación al cliente sobre el cambio de estado
        if (pedido.ClienteId.HasValue)
        {
            await _notificacionService.NotificarCambioEstadoPedidoAsync(id, nuevoEstado);
        }

        return NoContent();
    }


    // GET: api/PedidosNew/cliente/5
    [HttpGet("cliente/{clienteId}")]
    public async Task<ActionResult<IEnumerable<PedidoCompletoDto>>> GetPedidosPorCliente(int clienteId)
    {
        var pedidos = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Repartidor)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .Where(p => p.ClienteId == clienteId)
            .OrderByDescending(p => p.FechaPedido)
            .ToListAsync();

        return pedidos.Select(p => MapToPedidoCompletoDto(p)).ToList();
    }

    // GET: api/PedidosNew/repartidor/5
    [HttpGet("repartidor/{repartidorId}")]
    public async Task<ActionResult<IEnumerable<PedidoCompletoDto>>> GetPedidosPorRepartidor(int repartidorId)
    {
        var pedidos = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Repartidor)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .Where(p => p.RepartidorId == repartidorId)
            .OrderByDescending(p => p.FechaPedido)
            .ToListAsync();

        return pedidos.Select(p => MapToPedidoCompletoDto(p)).ToList();
    }

    // GET: api/PedidosNew/estado/{estado}
    [HttpGet("estado/{estado}")]
    public async Task<ActionResult<IEnumerable<PedidoCompletoDto>>> GetPedidosPorEstado(EstadoPedidoEnum estado)
    {
        var pedidos = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Repartidor)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .Where(p => p.Estado == estado)
            .OrderByDescending(p => p.FechaPedido)
            .ToListAsync();

        return pedidos.Select(p => MapToPedidoCompletoDto(p)).ToList();
    }

    // DELETE: api/PedidosNew/5
    [HttpDelete("{id}")]
    [Authorize(Roles = "Administrador")]
    public async Task<IActionResult> DeletePedido(int id)
    {
        var pedido = await _context.Pedidos.FindAsync(id);
        if (pedido == null)
        {
            return NotFound(new { message = "Pedido no encontrado" });
        }

        // Solo permitir eliminar pedidos cancelados
        if (pedido.Estado != EstadoPedidoEnum.Cancelado)
        {
            return BadRequest(new { message = "Solo se pueden eliminar pedidos cancelados" });
        }

        _context.Pedidos.Remove(pedido);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    // Método auxiliar para mapear a DTO
    private PedidoCompletoDto MapToPedidoCompletoDto(Pedido pedido)
    {
        return new PedidoCompletoDto
        {
            Id = pedido.Id,
            ClienteId = pedido.ClienteId,
            ClienteNombre = pedido.Cliente != null ? $"{pedido.Cliente.Nombre} {pedido.Cliente.Apellidos}" : null,
            RepartidorId = pedido.RepartidorId,
            RepartidorNombre = pedido.Repartidor != null ? $"{pedido.Repartidor.Nombre} {pedido.Repartidor.Apellidos}" : null,
            Tipo = pedido.Tipo.ToString(),
            Estado = pedido.Estado.ToString(),
            MetodoPago = pedido.MetodoPago.ToString(),
            Origen = pedido.Origen.ToString(),
            Total = pedido.Total,
            DireccionEntrega = pedido.DireccionEntrega,
            Observaciones = pedido.Observaciones,
            FechaPedido = pedido.FechaPedido,
            Detalles = pedido.Detalles.Select(d => new DetallePedidoCompletoDto
            {
                Id = d.Id,
                ProductoId = d.ProductoId,
                ProductoNombre = d.Producto.Nombre,
                Cantidad = d.Cantidad,
                Subtotal = d.Subtotal
            }).ToList()
        };
    }

    private bool PedidoExists(int id)
    {
        return _context.Pedidos.Any(e => e.Id == id);
    }
}
