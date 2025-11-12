using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

[Table("detalle_pedido")]
public class DetallePedido
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [Column("pedido_id")]
    public int PedidoId { get; set; }
    
    [Required]
    [Column("producto_id")]
    public int ProductoId { get; set; }
    
    [Required]
    [Column("cantidad")]
    public int Cantidad { get; set; } = 1;
    
    [Required]
    [Column("subtotal", TypeName = "decimal(10,2)")]
    public decimal Subtotal { get; set; } = 0;
    
    // Relaciones
    [ForeignKey("PedidoId")]
    public virtual Pedido Pedido { get; set; } = null!;
    
    [ForeignKey("ProductoId")]
    public virtual Producto Producto { get; set; } = null!;
}