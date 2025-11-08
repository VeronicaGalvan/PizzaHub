using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class SesionCaja
{
    [Key]
    public int Id { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal Diferencia { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal EfectivoFinal { get; set; }

    public DateTime FechaApertura { get; set; }

    public DateTime? FechaCierre { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal FondoInicial { get; set; }

    public int IdUsuarioApertura { get; set; }

    public int? IdUsuarioCierre { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal VentasSistema { get; set; }
}
