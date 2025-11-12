using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

[Table("productos")]
public class Producto
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(100)]
    [Column("nombre")]
    public string Nombre { get; set; } = null!;
    
    [Column("descripcion")]
    public string? Descripcion { get; set; }
    
    [MaxLength(50)]
    [Column("tipo")]
    public string? Tipo { get; set; }
    
    [Required]
    [Column("precio", TypeName = "decimal(10,2)")]
    public decimal Precio { get; set; } = 0;
    
    [Column("almacenable")]
    public bool Almacenable { get; set; } = false;
    
    [MaxLength(255)]
    [Column("imagen_url")]
    public string? ImagenUrl { get; set; }
    
    [Column("activo")]
    public bool Activo { get; set; } = true;
    
    // Relaciones
    [JsonIgnore]
    public virtual ICollection<DetallePedido> DetallesPedido { get; set; } = new List<DetallePedido>();
}