using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models
{
    public enum EstadoPedido
    {
        PENDIENTE,
        PREPARACION,
        EN_CAMINO,
        ENTREGADO,
        CANCELADO
    }

    public class Pedido
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public int UsuarioId { get; set; }

        [Required]
        public DateTime FechaPedido { get; set; } = DateTime.UtcNow;

        [Required]
        public EstadoPedido Estado { get; set; } = EstadoPedido.PENDIENTE;

        [Required]
        [Column(TypeName = "decimal(18,2)")]
        public decimal Total { get; set; }

        public string? DireccionEntrega { get; set; }

        public int? RepartidorId { get; set; }

        public DateTime? FechaAsignacion { get; set; }
        public DateTime? FechaRecogida { get; set; }
        public DateTime? FechaEntrega { get; set; }

        public int? CalificacionEntrega { get; set; }
        public string? ComentarioEntrega { get; set; }

        // Relaciones
        public virtual Usuario Usuario { get; set; }
        public virtual Repartidor? Repartidor { get; set; }
        public virtual ICollection<DetallePedido> DetallesPedido { get; set; }
    }
}