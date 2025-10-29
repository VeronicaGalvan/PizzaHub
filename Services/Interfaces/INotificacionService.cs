using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Services.Interfaces
{
    public interface INotificacionService
    {
        Task<List<NotificacionDTO>> ObtenerPorUsuarioAsync(int usuarioId);
        Task<NotificacionDTO> CrearAsync(CrearNotificacionDTO dto);
        Task<bool> MarcarComoLeidaAsync(int id);
        Task ActualizarTokenDispositivoAsync(int usuarioId, string token);
        Task EnviarNotificacionPushAsync(CrearNotificacionDTO dto);
    }
}