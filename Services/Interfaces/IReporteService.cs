using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Services.Interfaces
{
    public interface IReporteService
    {
        Task<Dictionary<string, object>> ObtenerEstadisticasVentasAsync(DateTime inicio, DateTime fin);
        Task<Dictionary<string, object>> ObtenerEstadisticasPedidosAsync(DateTime inicio, DateTime fin);
        Task<Dictionary<string, object>> ObtenerEstadisticasRepartidoresAsync(DateTime inicio, DateTime fin);
        Task<List<Dictionary<string, object>>> ObtenerProductosMasVendidosAsync(int limite = 10);
        Task<Dictionary<string, object>> ObtenerEstadisticasCalificacionesAsync(DateTime inicio, DateTime fin);
    }
}