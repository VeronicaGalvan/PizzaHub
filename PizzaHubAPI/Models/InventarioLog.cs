using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

[Table("inventario_log")]
public class InventarioLog
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [Column("insumo_id")]
    public int InsumoId { get; set; }
    
    [Required]
    [Column("cantidad", TypeName = "decimal(10,2)")]
    public decimal Cantidad { get; set; }
    
    [Required]
    [Column("tipo_movimiento")]
    public TipoMovimientoEnum TipoMovimiento { get; set; }
    
    [Column("motivo")]
    public string? Motivo { get; set; }
    
    [Column("fecha")]
    public DateTime Fecha { get; set; } = DateTime.Now;

    // Relaciones
    [ForeignKey("InsumoId")]
    public virtual Insumo Insumo { get; set; } = null!;
}

public enum TipoMovimientoEnum
{
    Entrada,
    Salida
}
