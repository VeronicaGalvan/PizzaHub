using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

[Table("insumos")]
public class Insumo
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(100)]
    [Column("nombre")]
    public string Nombre { get; set; } = null!;
    
    [Required]
    [Column("unidad_medida")]
    [MaxLength(10)]
    public string UnidadMedida { get; set; } = "Uds";
    
    [Column("stock_actual", TypeName = "decimal(10,2)")]
    public decimal StockActual { get; set; } = 0;
    
    [Column("stock_minimo", TypeName = "decimal(10,2)")]
    public decimal StockMinimo { get; set; } = 0;
    
    [Column("ultima_actualizacion")]
    public DateTime UltimaActualizacion { get; set; } = DateTime.UtcNow;

    // Relaciones
    [JsonIgnore]
    public virtual ICollection<InventarioLog> InventarioLogs { get; set; } = new List<InventarioLog>();
}
