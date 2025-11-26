using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

[Table("empleados")]
public class Empleado
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(50)]
    [Column("nombre")]
    public string Nombre { get; set; } = null!;
    
    [Required]
    [MaxLength(50)]
    [Column("apellidos")]
    public string Apellidos { get; set; } = null!;
    
    [MaxLength(20)]
    [Column("telefono")]
    public string? Telefono { get; set; }
    
    [Required]
    [Column("usuario_id")]
    public int UsuarioId { get; set; }
    
    [Column("fecha_ingreso")]
    public DateTime FechaIngreso { get; set; } = DateTime.UtcNow.Date;
    
    [Column("activo")]
    public bool Activo { get; set; } = true;

    // Relaciones
    [ForeignKey("UsuarioId")]
    public virtual Usuario Usuario { get; set; } = null!;
    
    [JsonIgnore]
    public virtual ICollection<Venta> Ventas { get; set; } = new List<Venta>();
    [JsonIgnore]
    public virtual ICollection<Caja> Cajas { get; set; } = new List<Caja>();
    [JsonIgnore]
    public virtual ICollection<CompraInsumo> ComprasInsumos { get; set; } = new List<CompraInsumo>();
}
