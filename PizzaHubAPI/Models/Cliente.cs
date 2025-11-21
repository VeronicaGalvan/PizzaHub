using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace PizzaHubAPI.Models;

[Table("clientes")]
public class Cliente
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
    
    [MaxLength(50)]
    [Column("colonia")]
    public string? Colonia { get; set; }
    
    [MaxLength(50)]
    [Column("calle")]
    public string? Calle { get; set; }
    
    [MaxLength(20)]
    [Column("numero_casa")]
    public string? NumeroCasa { get; set; }
    
    [Column("observaciones")]
    public string? Observaciones { get; set; }
    
    [Column("usuario_id")]
    public int? UsuarioId { get; set; }
    
    [MaxLength(255)]
    [Column("fcm_token")]
    public string? FcmToken { get; set; }

    // Relaciones
    [ForeignKey("UsuarioId")]
    public virtual Usuario? Usuario { get; set; }
    
    [JsonIgnore]
    public virtual ICollection<Pedido> Pedidos { get; set; } = new List<Pedido>();
    
    [JsonIgnore]
    public virtual ICollection<Notificacion> Notificaciones { get; set; } = new List<Notificacion>();
}