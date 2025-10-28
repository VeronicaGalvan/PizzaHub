namespace PizzaHubAPI.Models.DTOs
{
    public class MensajeChatDTO
    {
        public int Id { get; set; }
        public int UsuarioId { get; set; }
        public string Mensaje { get; set; }
        public bool EsUsuario { get; set; }
        public DateTime FechaEnvio { get; set; }
    }

    public class EnviarMensajeChatDTO
    {
        public string Mensaje { get; set; }
    }
}