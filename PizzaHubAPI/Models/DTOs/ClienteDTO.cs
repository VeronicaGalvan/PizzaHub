namespace PizzaHubAPI.Models.DTOs;

public class RegistroClienteDTO
{
    public string Email { get; set; } = null!;
    public string Password { get; set; } = null!;
    public string NumeroCelular { get; set; } = null!;
    public string Nombre { get; set; } = null!;
    public string Apellido { get; set; } = null!;
    public string Colonia { get; set; } = null!;
    public string Calle { get; set; } = null!;
    public string Numero { get; set; } = null!;
    public decimal DistanciaAproximada { get; set; }
}

public class ClienteDTO
{
    public int Id { get; set; }
    public string NumeroCelular { get; set; } = null!;
    public string Nombre { get; set; } = null!;
    public string Apellido { get; set; } = null!;
    public string Colonia { get; set; } = null!;
    public string Calle { get; set; } = null!;
    public string Numero { get; set; } = null!;
    public decimal? DistanciaAproximada { get; set; }
    public DateTime FechaRegistro { get; set; }
}

public class CrearClienteDto
{
    public string Nombre { get; set; } = null!;
    public string Apellidos { get; set; } = null!;
    public string? Telefono { get; set; }
    public string? Colonia { get; set; }
    public string? Calle { get; set; }
    public string? NumeroCasa { get; set; }
    public string? Observaciones { get; set; }
    public int? UsuarioId { get; set; }
}

public class ActualizarMiPerfilDto
{
    public string Nombre { get; set; } = null!;
    public string Apellidos { get; set; } = null!;
    public string? Telefono { get; set; }
    public string? Colonia { get; set; }
    public string? Calle { get; set; }
    public string? NumeroCasa { get; set; }
    public string? Observaciones { get; set; }
}