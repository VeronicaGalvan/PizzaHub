namespace PizzaHubAPI.Models.DTOs;

public class RegistroRepartidorDTO
{
    public string Email { get; set; } = null!;
    public string Password { get; set; } = null!;
    public string NumeroCelular { get; set; } = null!;
    public string Sobrenombre { get; set; } = null!;
    public string Nombre { get; set; } = null!;
    public string Apellido { get; set; } = null!;
    public string Colonia { get; set; } = null!;
    public string Calle { get; set; } = null!;
    public string Numero { get; set; } = null!;
}

public class RepartidorDTO
{
    public int Id { get; set; }
    public string NumeroCelular { get; set; } = null!;
    public string Sobrenombre { get; set; } = null!;
    public string Nombre { get; set; } = null!;
    public string Apellido { get; set; } = null!;
    public string Colonia { get; set; } = null!;
    public string Calle { get; set; } = null!;
    public string Numero { get; set; } = null!;
    public bool Disponible { get; set; }
}

public class CrearRepartidorDto
{
    public string Nombre { get; set; } = null!;
    public string Apellidos { get; set; } = null!;
    public string? Telefono { get; set; }
    public int UsuarioId { get; set; }
}

public class CambiarEstadoRepartidorDto
{
    public RepartidorEstadoEnum Estado { get; set; }
}