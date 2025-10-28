using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Services.Interfaces
{
    public interface IPedidoService
    {
        Task<List<PedidoDTO>> ObtenerTodosAsync();
        Task<PedidoDTO> ObtenerPorIdAsync(int id);
        Task<List<PedidoDTO>> ObtenerPorUsuarioAsync(int usuarioId);
        Task<PedidoDTO> CrearAsync(int usuarioId, CrearPedidoDTO dto);
        Task<PedidoDTO> ActualizarEstadoAsync(int id, EstadoPedido nuevoEstado);
        Task<PedidoDTO> AsignarRepartidorAsync(int id, int repartidorId);
        Task<PedidoDTO> RegistrarRecogidaAsync(int id);
        Task<PedidoDTO> RegistrarEntregaAsync(int id);
        Task<PedidoDTO> CalificarAsync(int id, CalificarPedidoDTO dto);
    }
}