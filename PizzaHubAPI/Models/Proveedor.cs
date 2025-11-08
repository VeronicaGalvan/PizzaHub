using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class Proveedor
{
    [Key]
    public int Id { get; set; }

    [MaxLength(255)]
    public string? Direccion { get; set; }

    [Required]
    [MaxLength(100)]
    public string Nombre { get; set; } = null!;

    [MaxLength(15)]
    public string? Telefono { get; set; }

    // Relaciones
    public virtual ICollection<MateriaPrima> MateriasPrimas { get; set; } = new List<MateriaPrima>();
}
