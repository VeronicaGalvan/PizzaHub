using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Services.Interfaces
{
    public interface IChatService
    {
        Task<List<MensajeChatDTO>> ObtenerHistorialAsync(int usuarioId);
        Task<MensajeChatDTO> EnviarMensajeAsync(int usuarioId, EnviarMensajeChatDTO dto);
        Task<MensajeChatDTO> ResponderMensajeAsync(int mensajeId);
    }
}