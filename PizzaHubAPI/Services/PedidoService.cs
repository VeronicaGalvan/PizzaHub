using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Data;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Services;

public class PedidoService
{
    private readonly PizzaHubContext _context;

    public PedidoService(PizzaHubContext context)
    {
        _context = context;
    }

    public async Task<Repartidor?> AsignarRepartidorDisponible()
    {
        // Obtener repartidores que no están en un pedido en curso
        var repartidoresOcupados = await _context.Pedidos
            .Where(p => p.Estado == EstadoPedido.EN_CAMINO)
            .Select(p => p.RepartidorId)
            .ToListAsync();

        // Buscar un repartidor disponible que no esté en un pedido en curso
        var repartidorDisponible = await _context.Repartidores
            .Include(r => r.Usuario)
            .Where(r => r.Disponible && 
                       r.Activo &&
                       r.Usuario.Activo &&
                       !repartidoresOcupados.Contains(r.Id))
            .FirstOrDefaultAsync();

        return repartidorDisponible;
    }

    public async Task<bool> ActualizarEstadoPedido(Pedido pedido, EstadoPedido nuevoEstado, int usuarioId, string? observaciones = null)
    {
        // Validar transición de estado
        if (!EsTransicionValida(pedido.Estado, nuevoEstado))
        {
            return false;
        }

        // Actualizar estado del pedido
        pedido.Estado = nuevoEstado;
        pedido.ActualizadoEn = DateTime.UtcNow;

        // Actualizar fechas según el estado
        switch (nuevoEstado)
        {
            case EstadoPedido.PREPARACION:
                pedido.FechaPreparacion = DateTime.UtcNow;
                break;
            case EstadoPedido.EN_CAMINO:
                pedido.FechaEnvio = DateTime.UtcNow;
                // Asignar repartidor si no tiene uno
                if (pedido.RepartidorId == null)
                {
                    var repartidor = await AsignarRepartidorDisponible();
                    if (repartidor == null)
                    {
                        return false;
                    }
                    pedido.RepartidorId = repartidor.Id;
                }
                break;
            case EstadoPedido.ENTREGADO:
                pedido.FechaEntrega = DateTime.UtcNow;
                break;
        }

        // Registrar en historial
        var historial = new HistorialEstadoPedido
        {
            PedidoId = pedido.Id,
            Estado = nuevoEstado,
            UsuarioId = usuarioId,
            Observaciones = observaciones,
            CreadoEn = DateTime.UtcNow
        };

        _context.HistorialEstadoPedido.Add(historial);
        await _context.SaveChangesAsync();

        return true;
    }

    private bool EsTransicionValida(EstadoPedido estadoActual, EstadoPedido nuevoEstado)
    {
        return (estadoActual, nuevoEstado) switch
        {
            (EstadoPedido.PENDIENTE, EstadoPedido.PREPARACION) => true,
            (EstadoPedido.PENDIENTE, EstadoPedido.CANCELADO) => true,
            (EstadoPedido.PREPARACION, EstadoPedido.EN_CAMINO) => true,
            (EstadoPedido.PREPARACION, EstadoPedido.CANCELADO) => true,
            (EstadoPedido.EN_CAMINO, EstadoPedido.ENTREGADO) => true,
            (EstadoPedido.EN_CAMINO, EstadoPedido.CANCELADO) => true,
            _ => false
        };
    }
}