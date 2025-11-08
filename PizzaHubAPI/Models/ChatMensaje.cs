using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace PizzaHubAPI.Models;

public class ChatMensaje
{
    [Key]
    public int Id { get; set; }

    [Required]
    public int ClienteId { get; set; }

    [Required]
    public DateTime Fecha { get; set; }

    [Column(TypeName = "text")]
    public string? MensajeCliente { get; set; }

    [Column(TypeName = "text")]
    public string? RespuestaSistema { get; set; }

    // Relaciones
    [ForeignKey("ClienteId")]
    public virtual Cliente? Cliente { get; set; }
}
