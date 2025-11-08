using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class Merma
{
    [Key]
    public int Id { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal Cantidad { get; set; }

    [MaxLength(255)]
    public string? Comentarios { get; set; }

    public DateTime FechaRegistro { get; set; }

    [Required]
    public int MateriaPrimaId { get; set; }

    public int Tipo { get; set; }

    // Relaciones
    public virtual MateriaPrima? MateriaPrima { get; set; }
}
