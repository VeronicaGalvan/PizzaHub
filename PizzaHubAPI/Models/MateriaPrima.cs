using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

public class MateriaPrima
{
    [Key]
    public int Id { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal CantidadActual { get; set; }

    public DateTime FechaActualizacion { get; set; }

    [Required]
    [MaxLength(100)]
    public string Nombre { get; set; } = null!;

    public int? ProveedorId { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal PuntoReorden { get; set; }

    public int UnidadMedida { get; set; }

    // Relaciones
    [JsonIgnore]
    public virtual Proveedor? Proveedor { get; set; }
}
