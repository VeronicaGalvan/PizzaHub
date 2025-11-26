using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

[Table("compras_insumos")]
public class CompraInsumo
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(100)]
    [Column("proveedor")]
    public string Proveedor { get; set; } = null!;
    
    [MaxLength(50)]
    [Column("numero_factura")]
    public string? NumeroFactura { get; set; }
    
    [Column("total", TypeName = "decimal(10,2)")]
    public decimal Total { get; set; }
    
    [Column("fecha_compra")]
    public DateTime FechaCompra { get; set; } = DateTime.UtcNow;
    
    [Column("observaciones")]
    public string? Observaciones { get; set; }
    
    [Column("empleado_id")]
    public int? EmpleadoId { get; set; }

    // Relaciones
    [ForeignKey("EmpleadoId")]
    [JsonIgnore]
    public virtual Empleado? Empleado { get; set; }
    
    [JsonIgnore]
    public virtual ICollection<DetalleCompraInsumo> Detalles { get; set; } = new List<DetalleCompraInsumo>();
}
