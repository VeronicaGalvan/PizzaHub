using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

[Table("pedidos")]
public class Pedido
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Column("cliente_id")]
    public int? ClienteId { get; set; }
    
    [Column("repartidor_id")]
    public int? RepartidorId { get; set; }
    
    [Required]
    [Column("tipo")]
    public TipoPedidoEnum Tipo { get; set; }
    
    [Required]
    [Column("estado")]
    public EstadoPedidoEnum Estado { get; set; } = EstadoPedidoEnum.Pendiente;
    
    [Required]
    [Column("metodo_pago")]
    public MetodoPagoEnum MetodoPago { get; set; }
    
    [Required]
    [Column("origen")]
    public OrigenPedidoEnum Origen { get; set; } = OrigenPedidoEnum.Mostrador;
    
    [Required]
    [Column("total", TypeName = "decimal(10,2)")]
    public decimal Total { get; set; } = 0;
    
    [Column("direccion_entrega")]
    public string? DireccionEntrega { get; set; }
    
    [Column("observaciones")]
    public string? Observaciones { get; set; }
    
    [Column("fecha_pedido")]
    public DateTime FechaPedido { get; set; } = DateTime.Now;
    
    // Relaciones
    [ForeignKey("ClienteId")]
    public virtual Cliente? Cliente { get; set; }
    
    [ForeignKey("RepartidorId")]
    public virtual Repartidor? Repartidor { get; set; }
    
    [JsonIgnore]
    public virtual ICollection<DetallePedido> Detalles { get; set; } = new List<DetallePedido>();
    [JsonIgnore]
    public virtual ICollection<Calificacion> Calificaciones { get; set; } = new List<Calificacion>();
    [JsonIgnore]
    public virtual ICollection<Venta> Ventas { get; set; } = new List<Venta>();
}

public enum TipoPedidoEnum
{
    Mostrador,
    [Display(Name = "Llamada-Recoge")]
    LlamadaRecoge,
    [Display(Name = "Llamada-Envio")]
    LlamadaEnvio,
    Plataforma,
    App
}

public enum EstadoPedidoEnum
{
    Pendiente,
    [Display(Name = "En preparación")]
    EnPreparacion,
    [Display(Name = "En camino")]
    EnCamino,
    Entregado,
    Cancelado
}

public enum MetodoPagoEnum
{
    Efectivo,
    Tarjeta,
    Plataforma,
    Transferencia
}

public enum OrigenPedidoEnum
{
    App,
    Llamada,
    Plataforma,
    Mostrador
}