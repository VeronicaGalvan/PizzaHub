using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class Persona
{
    [Key]
    public int Id { get; set; }

    [MaxLength(50)]
    public string? Apellido { get; set; }

    [MaxLength(100)]
    public string? Calle { get; set; }

    [MaxLength(10)]
    public string? CodigoPostal { get; set; }

    [MaxLength(100)]
    public string? Colonia { get; set; }

    [MaxLength(100)]
    public string? Estado { get; set; }

    public DateTime FechaRegistro { get; set; }

    [MaxLength(100)]
    public string? Municipio { get; set; }

    [Required]
    [MaxLength(50)]
    public string Nombre { get; set; } = null!;

    [MaxLength(10)]
    public string? Numero { get; set; }

    [Required]
    [MaxLength(15)]
    public string Telefono { get; set; } = null!;

    public int? UsuarioId { get; set; }

    // Relaciones
    public virtual Usuario? Usuario { get; set; }
}
