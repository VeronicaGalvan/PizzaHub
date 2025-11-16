using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Controllers;

/// <summary>
/// Controlador exclusivo para repartidores autenticados
/// Gestiona los pedidos asignados al repartidor actual
/// </summary>
[ApiController]
[Route("api/[controller]")]
[Authorize(Roles = "Repartidor")]
public class MisEnviosController : ControllerBase
{
    private readonly PizzaHubContext _context;

    public MisEnviosController(PizzaHubContext context)
    {
        _context = context;
    }

    /// <summary>
    /// Obtiene el perfil del repartidor autenticado
    /// </summary>
    [HttpGet("mi-perfil")]
    public async Task<ActionResult<object>> GetMiPerfil()
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .Include(r => r.Usuario)
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        return new
        {
            repartidor.Id,
            repartidor.Nombre,
            repartidor.Apellidos,
            repartidor.Telefono,
            repartidor.Estado,
            Email = repartidor.Usuario.Correo
        };
    }

    /// <summary>
    /// Obtiene todos los pedidos asignados al repartidor actual
    /// </summary>
    [HttpGet("mis-pedidos")]
    public async Task<ActionResult<IEnumerable<object>>> GetMisPedidos()
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        var pedidos = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .Where(p => p.RepartidorId == repartidor.Id)
            .OrderByDescending(p => p.FechaPedido)
            .Select(p => new
            {
                p.Id,
                p.Estado,
                p.Total,
                p.DireccionEntrega,
                p.Observaciones,
                p.FechaPedido,
                p.MetodoPago,
                p.Tipo,
                Cliente = p.Cliente != null ? new
                {
                    Nombre = p.Cliente.Nombre + " " + p.Cliente.Apellidos,
                    p.Cliente.Telefono,
                    Direccion = $"{p.Cliente.Calle} {p.Cliente.NumeroCasa}, {p.Cliente.Colonia}"
                } : null,
                CantidadProductos = p.Detalles.Sum(d => d.Cantidad),
                Productos = p.Detalles.Select(d => new
                {
                    d.Producto.Nombre,
                    d.Cantidad,
                    d.Subtotal
                }).ToList()
            })
            .ToListAsync();

        return Ok(pedidos);
    }

    /// <summary>
    /// Obtiene solo los pedidos pendientes asignados al repartidor
    /// </summary>
    [HttpGet("pendientes")]
    public async Task<ActionResult<IEnumerable<object>>> GetPedidosPendientes()
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        var pedidos = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .Where(p => p.RepartidorId == repartidor.Id 
                   && (p.Estado == EstadoPedidoEnum.Pendiente 
                       || p.Estado == EstadoPedidoEnum.EnPreparacion))
            .OrderBy(p => p.FechaPedido)
            .Select(p => new
            {
                p.Id,
                p.Estado,
                p.Total,
                p.DireccionEntrega,
                p.Observaciones,
                p.FechaPedido,
                p.MetodoPago,
                p.Tipo,
                Cliente = p.Cliente != null ? new
                {
                    Nombre = p.Cliente.Nombre + " " + p.Cliente.Apellidos,
                    p.Cliente.Telefono,
                    Direccion = $"{p.Cliente.Calle} {p.Cliente.NumeroCasa}, {p.Cliente.Colonia}"
                } : null,
                CantidadProductos = p.Detalles.Sum(d => d.Cantidad)
            })
            .ToListAsync();

        return Ok(pedidos);
    }

    /// <summary>
    /// Obtiene los pedidos que están en camino (en ruta de entrega)
    /// </summary>
    [HttpGet("en-camino")]
    public async Task<ActionResult<IEnumerable<object>>> GetPedidosEnCamino()
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        var pedidos = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .Where(p => p.RepartidorId == repartidor.Id 
                   && p.Estado == EstadoPedidoEnum.EnCamino)
            .OrderBy(p => p.FechaPedido)
            .Select(p => new
            {
                p.Id,
                p.Estado,
                p.Total,
                p.DireccionEntrega,
                p.Observaciones,
                p.FechaPedido,
                p.MetodoPago,
                Cliente = p.Cliente != null ? new
                {
                    Nombre = p.Cliente.Nombre + " " + p.Cliente.Apellidos,
                    p.Cliente.Telefono,
                    Direccion = $"{p.Cliente.Calle} {p.Cliente.NumeroCasa}, {p.Cliente.Colonia}"
                } : null,
                Productos = p.Detalles.Select(d => new
                {
                    d.Producto.Nombre,
                    d.Cantidad
                }).ToList()
            })
            .ToListAsync();

        return Ok(pedidos);
    }

    /// <summary>
    /// Obtiene el historial de pedidos entregados por el repartidor
    /// </summary>
    [HttpGet("historial")]
    public async Task<ActionResult<IEnumerable<object>>> GetHistorial()
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        var pedidos = await _context.Pedidos
            .Include(p => p.Cliente)
            .Where(p => p.RepartidorId == repartidor.Id 
                   && p.Estado == EstadoPedidoEnum.Entregado)
            .OrderByDescending(p => p.FechaPedido)
            .Select(p => new
            {
                p.Id,
                p.Estado,
                p.Total,
                p.DireccionEntrega,
                p.FechaPedido,
                p.MetodoPago,
                Cliente = p.Cliente != null ? new
                {
                    Nombre = p.Cliente.Nombre + " " + p.Cliente.Apellidos
                } : null
            })
            .ToListAsync();

        return Ok(pedidos);
    }

    /// <summary>
    /// Ver detalles completos de un pedido específico
    /// </summary>
    [HttpGet("pedido/{id}")]
    public async Task<ActionResult<object>> GetDetallePedido(int id)
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        var pedido = await _context.Pedidos
            .Include(p => p.Cliente)
            .Include(p => p.Detalles)
                .ThenInclude(d => d.Producto)
            .Where(p => p.Id == id && p.RepartidorId == repartidor.Id)
            .Select(p => new
            {
                p.Id,
                p.Estado,
                p.Total,
                p.DireccionEntrega,
                p.Observaciones,
                p.FechaPedido,
                p.MetodoPago,
                p.Tipo,
                p.Origen,
                Cliente = p.Cliente != null ? new
                {
                    p.Cliente.Id,
                    Nombre = p.Cliente.Nombre + " " + p.Cliente.Apellidos,
                    p.Cliente.Telefono,
                    Calle = p.Cliente.Calle,
                    NumeroCasa = p.Cliente.NumeroCasa,
                    Colonia = p.Cliente.Colonia,
                    DireccionCompleta = $"{p.Cliente.Calle} {p.Cliente.NumeroCasa}, {p.Cliente.Colonia}",
                    Observaciones = p.Cliente.Observaciones
                } : null,
                Productos = p.Detalles.Select(d => new
                {
                    d.Id,
                    d.Producto.Nombre,
                    d.Cantidad,
                    PrecioUnitario = d.Subtotal / d.Cantidad,
                    d.Subtotal
                }).ToList(),
                TotalProductos = p.Detalles.Sum(d => d.Cantidad)
            })
            .FirstOrDefaultAsync();

        if (pedido == null)
            return NotFound(new { message = "Pedido no encontrado o no asignado a ti" });

        return Ok(pedido);
    }

    /// <summary>
    /// Marcar que el repartidor está listo para recoger el pedido
    /// Cambia el estado a "En camino"
    /// </summary>
    [HttpPut("pedido/{id}/recoger")]
    public async Task<IActionResult> RecogerPedido(int id)
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        var pedido = await _context.Pedidos
            .FirstOrDefaultAsync(p => p.Id == id && p.RepartidorId == repartidor.Id);

        if (pedido == null)
            return NotFound(new { message = "Pedido no encontrado o no asignado a ti" });

        if (pedido.Estado != EstadoPedidoEnum.EnPreparacion)
            return BadRequest(new { message = "El pedido debe estar en preparación para recogerlo" });

        pedido.Estado = EstadoPedidoEnum.EnCamino;
        repartidor.Estado = RepartidorEstadoEnum.Ocupado;
        
        await _context.SaveChangesAsync();

        return Ok(new { message = "Pedido recogido, en camino al cliente" });
    }

    /// <summary>
    /// Marcar el pedido como entregado
    /// </summary>
    [HttpPut("pedido/{id}/entregar")]
    public async Task<IActionResult> EntregarPedido(int id)
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        var pedido = await _context.Pedidos
            .FirstOrDefaultAsync(p => p.Id == id && p.RepartidorId == repartidor.Id);

        if (pedido == null)
            return NotFound(new { message = "Pedido no encontrado o no asignado a ti" });

        if (pedido.Estado != EstadoPedidoEnum.EnCamino)
            return BadRequest(new { message = "El pedido debe estar en camino para marcarlo como entregado" });

        pedido.Estado = EstadoPedidoEnum.Entregado;
        
        // Verificar si el repartidor tiene más pedidos en camino
        var tieneMasPedidos = await _context.Pedidos
            .AnyAsync(p => p.RepartidorId == repartidor.Id 
                      && p.Estado == EstadoPedidoEnum.EnCamino);

        if (!tieneMasPedidos)
        {
            repartidor.Estado = RepartidorEstadoEnum.Disponible;
        }
        
        await _context.SaveChangesAsync();

        return Ok(new { message = "Pedido entregado exitosamente" });
    }

    /// <summary>
    /// Cambiar disponibilidad del repartidor
    /// </summary>
    [HttpPatch("disponibilidad")]
    public async Task<IActionResult> CambiarDisponibilidad([FromBody] CambiarDisponibilidadDto dto)
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        // No permitir cambiar a Disponible si tiene pedidos en camino
        if (dto.Disponible && repartidor.Estado == RepartidorEstadoEnum.Ocupado)
        {
            var tienePedidosEnCamino = await _context.Pedidos
                .AnyAsync(p => p.RepartidorId == repartidor.Id 
                          && p.Estado == EstadoPedidoEnum.EnCamino);

            if (tienePedidosEnCamino)
                return BadRequest(new { message = "No puedes estar disponible mientras tengas pedidos en camino" });
        }

        repartidor.Estado = dto.Disponible ? RepartidorEstadoEnum.Disponible : RepartidorEstadoEnum.Inactivo;
        await _context.SaveChangesAsync();

        return Ok(new { 
            message = dto.Disponible ? "Ahora estás disponible" : "Te has marcado como no disponible",
            estado = repartidor.Estado
        });
    }

    /// <summary>
    /// Obtener estadísticas del repartidor
    /// </summary>
    [HttpGet("estadisticas")]
    public async Task<ActionResult<object>> GetEstadisticas()
    {
        var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
        
        var repartidor = await _context.Repartidores
            .FirstOrDefaultAsync(r => r.UsuarioId == userId);

        if (repartidor == null)
            return NotFound(new { message = "Perfil de repartidor no encontrado" });

        var hoy = DateTime.Today;
        var pedidosHoy = await _context.Pedidos
            .Where(p => p.RepartidorId == repartidor.Id 
                   && p.FechaPedido.Date == hoy)
            .ToListAsync();

        var pedidosTotal = await _context.Pedidos
            .Where(p => p.RepartidorId == repartidor.Id)
            .ToListAsync();

        return Ok(new
        {
            Hoy = new
            {
                Total = pedidosHoy.Count,
                Entregados = pedidosHoy.Count(p => p.Estado == EstadoPedidoEnum.Entregado),
                EnCamino = pedidosHoy.Count(p => p.Estado == EstadoPedidoEnum.EnCamino),
                Pendientes = pedidosHoy.Count(p => p.Estado == EstadoPedidoEnum.Pendiente 
                                                 || p.Estado == EstadoPedidoEnum.EnPreparacion),
                TotalGanancias = pedidosHoy
                    .Where(p => p.Estado == EstadoPedidoEnum.Entregado)
                    .Sum(p => p.Total)
            },
            General = new
            {
                TotalEntregas = pedidosTotal.Count(p => p.Estado == EstadoPedidoEnum.Entregado),
                TotalPedidos = pedidosTotal.Count,
                TotalGanancias = pedidosTotal
                    .Where(p => p.Estado == EstadoPedidoEnum.Entregado)
                    .Sum(p => p.Total)
            },
            EstadoActual = repartidor.Estado.ToString()
        });
    }
}

// DTOs adicionales
public class CambiarDisponibilidadDto
{
    public bool Disponible { get; set; }
}
