namespace PizzaHubAPI.Models.DTOs
{
    public class NotificacionDTO
    {
        public int Id { get; set; }
        public string Titulo { get; set; }
        public string Mensaje { get; set; }
        public string? Datos { get; set; }
        public bool Leida { get; set; }
        public DateTime FechaCreacion { get; set; }
        public DateTime? FechaLectura { get; set; }
    }

    public class CrearNotificacionDTO
    {
        public int UsuarioId { get; set; }
        public string Titulo { get; set; }
        public string Mensaje { get; set; }
        public string? Datos { get; set; }
    }

    public class ActualizarTokenDispositivoDTO
    {
        public string TokenDispositivo { get; set; }
    }
}