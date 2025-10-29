using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Services.Interfaces
{
    public interface IRepartidorService
    {
        Task<List<RepartidorDTO>> ObtenerTodosAsync();
        Task<RepartidorDTO> ObtenerPorIdAsync(int id);
        Task<RepartidorDTO> CrearAsync(CrearRepartidorDTO dto);
        Task<RepartidorDTO> ActualizarEstadoAsync(int id, ActualizarEstadoRepartidorDTO dto);
        Task<bool> DesactivarAsync(int id);
        Task<List<RepartidorDTO>> ObtenerDisponiblesAsync();
        Task<Dictionary<string, object>> ObtenerEstadisticasAsync(int id);
    }
}