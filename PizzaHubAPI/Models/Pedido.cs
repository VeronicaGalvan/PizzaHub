using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

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
    
    [ForeignKey("Cliente")]
    public int ClienteId { get; set; }
    
    [ForeignKey("Repartidor")]
    public int? RepartidorId { get; set; }
    
    [Required]
    public EstadoPedido Estado { get; set; } = EstadoPedido.PENDIENTE;
    
    [Required]
    [Column(TypeName = "decimal(18,2)")]
    public decimal Total { get; set; }
    
    [Required]
    [MaxLength(200)]
    public string DireccionEntrega { get; set; } = null!;
    
    [MaxLength(500)]
    public string? Observaciones { get; set; }
    
    [MaxLength(20)]
    public string? TelefonoContacto { get; set; }
    
    public DateTime? FechaPreparacion { get; set; }
    
    public DateTime? FechaEnvio { get; set; }
    
    public DateTime? FechaEntrega { get; set; }
    
    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;
    
    public DateTime CreadoEn { get; set; }
    
    public DateTime? ActualizadoEn { get; set; }
    
    // Relaciones
    public virtual Cliente Cliente { get; set; } = null!;
    public virtual Repartidor? Repartidor { get; set; }
    public virtual ICollection<DetallePedido> Detalles { get; set; } = new List<DetallePedido>();
    public virtual ICollection<HistorialEstadoPedido> HistorialEstados { get; set; } = new List<HistorialEstadoPedido>();
}