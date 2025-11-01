using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class Producto
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(100)]
    public string Nombre { get; set; } = null!;
    
    [Required]
    [MaxLength(500)]
    public string Descripcion { get; set; } = null!;
    
    [Required]
    [Column(TypeName = "decimal(18,2)")]
    public decimal Precio { get; set; }
    
    [Required]
    public int StockActual { get; set; }
    
    [Required]
    public int StockMinimo { get; set; }
    
    [MaxLength(255)]
    public string? RutaImagen { get; set; }
    
    public bool Activo { get; set; } = true;
    
    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;
    
    public DateTime CreadoEn { get; set; }
    
    public DateTime? ActualizadoEn { get; set; }
    
    // Relaciones
    public virtual ICollection<MovimientoInventario> MovimientosInventario { get; set; } = new List<MovimientoInventario>();
}