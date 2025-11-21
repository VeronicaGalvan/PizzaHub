using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

[Table("notificaciones")]
public class Notificacion
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [Column("cliente_id")]
    public int ClienteId { get; set; }
    
    [Required]
    [MaxLength(100)]
    [Column("titulo")]
    public string Titulo { get; set; } = null!;
    
    [Required]
    [Column("mensaje")]
    public string Mensaje { get; set; } = null!;
    
    [Required]
    [MaxLength(50)]
    [Column("tipo")]
    public string Tipo { get; set; } = null!; // "pedido", "promocion", "sistema"
    
    [Column("pedido_id")]
    public int? PedidoId { get; set; }
    
    [Required]
    [Column("leida")]
    public bool Leida { get; set; } = false;
    
    [Required]
    [Column("enviada")]
    public bool Enviada { get; set; } = false;
    
    [Column("fecha_creacion")]
    public DateTime FechaCreacion { get; set; } = DateTime.Now;
    
    [Column("fecha_lectura")]
    public DateTime? FechaLectura { get; set; }
    
    // Relaciones
    [ForeignKey("ClienteId")]
    public virtual Cliente Cliente { get; set; } = null!;
    
    [ForeignKey("PedidoId")]
    public virtual Pedido? Pedido { get; set; }
}

public enum TipoNotificacionEnum
{
    Pedido,
    Promocion,
    Sistema
}
