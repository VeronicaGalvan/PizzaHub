using System.ComponentModel.DataAnnotations;

namespace PizzaHubAPI.Models;

public class Rol
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(50)]
    public string Nombre { get; set; } = null!;
    
    [MaxLength(200)]
    public string? Descripcion { get; set; }
    
    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;
    
    // Relaciones
    public virtual ICollection<UsuarioRol> UsuariosRoles { get; set; } = new List<UsuarioRol>();
}