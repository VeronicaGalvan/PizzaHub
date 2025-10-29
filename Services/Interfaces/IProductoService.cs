using PizzaHubAPI.Models;
using PizzaHubAPI.Models.DTOs;

namespace PizzaHubAPI.Services.Interfaces
{
    public interface IProductoService
    {
        Task<List<ProductoDTO>> ObtenerTodosAsync();
        Task<ProductoDTO> ObtenerPorIdAsync(int id);
        Task<ProductoDTO> CrearAsync(CrearProductoDTO dto);
        Task<ProductoDTO> ActualizarAsync(int id, ActualizarProductoDTO dto);
        Task<bool> EliminarAsync(int id);
        Task<bool> ActualizarStockAsync(int id, int cantidad);
    }
}