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
public class PedidosNewController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public PedidosNewController(PizzaHubContext context)
    {
        _context = context;
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

        return MapToPedidoCompletoDto(pedido);
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
            FechaPedido = DateTime.Now,
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
