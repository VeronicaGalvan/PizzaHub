using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

[Table("detalle_compra_insumos")]
public class DetalleCompraInsumo
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [Column("compra_id")]
    public int CompraId { get; set; }
    
    [Required]
    [Column("insumo_id")]
    public int InsumoId { get; set; }
    
    [Required]
    [Column("cantidad", TypeName = "decimal(10,2)")]
    public decimal Cantidad { get; set; }
    
    [Required]
    [Column("precio_unitario", TypeName = "decimal(10,2)")]
    public decimal PrecioUnitario { get; set; }
    
    [Column("subtotal", TypeName = "decimal(10,2)")]
    public decimal Subtotal { get; set; }

    // Relaciones
    [ForeignKey("CompraId")]
    [JsonIgnore]
    public virtual CompraInsumo Compra { get; set; } = null!;
    
    [ForeignKey("InsumoId")]
    public virtual Insumo Insumo { get; set; } = null!;
}
