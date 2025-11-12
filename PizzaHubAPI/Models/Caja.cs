using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

[Table("caja")]
public class Caja
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [Column("fecha")]
    public DateTime Fecha { get; set; }
    
    [Column("saldo_inicial", TypeName = "decimal(10,2)")]
    public decimal SaldoInicial { get; set; } = 0;
    
    [Column("saldo_final", TypeName = "decimal(10,2)")]
    public decimal SaldoFinal { get; set; } = 0;
    
    [Required]
    [Column("estado")]
    public EstadoCajaEnum Estado { get; set; } = EstadoCajaEnum.Abierta;
    
    [Column("empleado_id")]
    public int? EmpleadoId { get; set; }

    // Relaciones
    [ForeignKey("EmpleadoId")]
    public virtual Empleado? Empleado { get; set; }
    
    [JsonIgnore]
    public virtual ICollection<Venta> Ventas { get; set; } = new List<Venta>();
}

public enum EstadoCajaEnum
{
    Abierta,
    Cerrada
}
