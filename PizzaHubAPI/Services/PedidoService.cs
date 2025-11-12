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
            .Where(p => p.Estado == EstadoPedidoEnum.EnCamino)
            .Select(p => p.RepartidorId)
            .ToListAsync();

        // Buscar un repartidor disponible que no esté en un pedido en curso
        var repartidorDisponible = await _context.Repartidores
            .Where(r => r.Estado == RepartidorEstadoEnum.Disponible && !repartidoresOcupados.Contains(r.Id))
            .FirstOrDefaultAsync();

        return repartidorDisponible;
    }

    public async Task<bool> ActualizarEstadoPedido(Pedido pedido, EstadoPedidoEnum nuevoEstado)
    {
        // Validar transición de estado
        if (!EsTransicionValida(pedido.Estado, nuevoEstado))
        {
            return false;
        }

        // Actualizar estado del pedido
        pedido.Estado = nuevoEstado;

        // Asignar repartidor si pasa a En Camino y no tiene uno
        if (nuevoEstado == EstadoPedidoEnum.EnCamino && pedido.RepartidorId == null)
        {
            var repartidor = await AsignarRepartidorDisponible();
            if (repartidor == null)
            {
                return false;
            }
            pedido.RepartidorId = repartidor.Id;
        }

        await _context.SaveChangesAsync();

        return true;
    }

    private bool EsTransicionValida(EstadoPedidoEnum estadoActual, EstadoPedidoEnum nuevoEstado)
    {
        return (estadoActual, nuevoEstado) switch
        {
            (EstadoPedidoEnum.Pendiente, EstadoPedidoEnum.EnPreparacion) => true,
            (EstadoPedidoEnum.Pendiente, EstadoPedidoEnum.Cancelado) => true,
            (EstadoPedidoEnum.EnPreparacion, EstadoPedidoEnum.EnCamino) => true,
            (EstadoPedidoEnum.EnPreparacion, EstadoPedidoEnum.Cancelado) => true,
            (EstadoPedidoEnum.EnCamino, EstadoPedidoEnum.Entregado) => true,
            (EstadoPedidoEnum.EnCamino, EstadoPedidoEnum.Cancelado) => true,
            _ => false
        };
    }
}