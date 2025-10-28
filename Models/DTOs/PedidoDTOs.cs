namespace PizzaHubAPI.Models.DTOs
{
    public class PedidoDTO
    {
        public int Id { get; set; }
        public int UsuarioId { get; set; }
        public DateTime FechaPedido { get; set; }
        public EstadoPedido Estado { get; set; }
        public decimal Total { get; set; }
        public string? DireccionEntrega { get; set; }
        public int? RepartidorId { get; set; }
        public string? RepartidorNombre { get; set; }
        public DateTime? FechaAsignacion { get; set; }
        public DateTime? FechaRecogida { get; set; }
        public DateTime? FechaEntrega { get; set; }
        public List<DetallePedidoDTO> Detalles { get; set; }
        public int? CalificacionEntrega { get; set; }
        public string? ComentarioEntrega { get; set; }
    }

    public class CrearPedidoDTO
    {
        public string? DireccionEntrega { get; set; }
        public List<CrearDetallePedidoDTO> Detalles { get; set; }
    }

    public class DetallePedidoDTO
    {
        public int ProductoId { get; set; }
        public string ProductoNombre { get; set; }
        public int Cantidad { get; set; }
        public decimal PrecioUnitario { get; set; }
        public decimal Subtotal { get; set; }
    }

    public class CrearDetallePedidoDTO
    {
        public int ProductoId { get; set; }
        public int Cantidad { get; set; }
    }

    public class CalificarPedidoDTO
    {
        public int Calificacion { get; set; }
        public string? Comentario { get; set; }
    }
}