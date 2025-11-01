namespace PizzaHubAPI.Models.DTOs;

public class CrearPedidoDTO
{
    public string DireccionEntrega { get; set; } = null!;
    public string? TelefonoContacto { get; set; }
    public string? Observaciones { get; set; }
    public List<DetallePedidoDTO> Detalles { get; set; } = new List<DetallePedidoDTO>();
}

public class DetallePedidoDTO
{
    public int ProductoId { get; set; }
    public int Cantidad { get; set; }
    public string? Observaciones { get; set; }
}

public class ActualizarEstadoPedidoDTO
{
    public EstadoPedido NuevoEstado { get; set; }
    public string? Observaciones { get; set; }
}

public class PedidoResumenDTO
{
    public int Id { get; set; }
    public string Estado { get; set; } = null!;
    public decimal Total { get; set; }
    public string DireccionEntrega { get; set; } = null!;
    public string? TelefonoContacto { get; set; }
    public DateTime CreadoEn { get; set; }
    public string Cliente { get; set; } = null!;
    public string? Repartidor { get; set; }
    public List<DetallePedidoResumenDTO> Detalles { get; set; } = new List<DetallePedidoResumenDTO>();
}

public class DetallePedidoResumenDTO
{
    public string Producto { get; set; } = null!;
    public int Cantidad { get; set; }
    public decimal PrecioUnitario { get; set; }
    public decimal Subtotal { get; set; }
    public string? Observaciones { get; set; }
}