using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class UsuarioRol
{
    [Key]
    public int Id { get; set; }
    
    public int UsuarioId { get; set; }
    public int RolId { get; set; }
    
    [ForeignKey("UsuarioId")]
    public virtual Usuario Usuario { get; set; } = null!;
    
    [ForeignKey("RolId")]
    public virtual Rol Rol { get; set; } = null!;
}