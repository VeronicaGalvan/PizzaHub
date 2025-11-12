using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

[Table("usuarios")]
public class Usuario
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(100)]
    [Column("nombre_usuario")]
    public string NombreUsuario { get; set; } = null!;
    
    [MaxLength(20)]
    [Column("telefono")]
    public string? Telefono { get; set; }
    
    [Required]
    [MaxLength(100)]
    [Column("correo")]
    public string Correo { get; set; } = null!;
    
    [Required]
    [MaxLength(255)]
    [Column("password_hash")]
    public string PasswordHash { get; set; } = null!;
    
    [Required]
    [Column("rol")]
    public UsuarioRolEnum Rol { get; set; } = UsuarioRolEnum.Cliente;
    
    [Column("activo")]
    public bool Activo { get; set; } = true;
    
    [Column("fecha_creacion")]
    public DateTime FechaCreacion { get; set; } = DateTime.Now;

    // Relaciones
    [JsonIgnore]
    public virtual ICollection<TokenRevocado> TokensRevocados { get; set; } = new List<TokenRevocado>();
    [JsonIgnore]
    public virtual ICollection<Empleado> Empleados { get; set; } = new List<Empleado>();
    [JsonIgnore]
    public virtual ICollection<Cliente> Clientes { get; set; } = new List<Cliente>();
    [JsonIgnore]
    public virtual ICollection<Repartidor> Repartidores { get; set; } = new List<Repartidor>();
}

public enum UsuarioRolEnum
{
    Administrador,
    Empleado,
    Repartidor,
    Cliente
}