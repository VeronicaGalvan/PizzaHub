using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

[Table("ventas")]
public class Venta
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [Column("caja_id")]
    public int CajaId { get; set; }
    
    [Column("pedido_id")]
    public int? PedidoId { get; set; }
    
    [Column("empleado_id")]
    public int? EmpleadoId { get; set; }
    
    [Required]
    [Column("metodo_pago")]
    public MetodoPagoEnum MetodoPago { get; set; }
    
    [Required]
    [Column("total", TypeName = "decimal(10,2)")]
    public decimal Total { get; set; } = 0;
    
    [Column("fecha_venta")]
    public DateTime FechaVenta { get; set; } = DateTime.Now;

    // Relaciones
    [ForeignKey("CajaId")]
    public virtual Caja Caja { get; set; } = null!;
    
    [ForeignKey("PedidoId")]
    public virtual Pedido? Pedido { get; set; }
    
    [ForeignKey("EmpleadoId")]
    public virtual Empleado? Empleado { get; set; }
}
