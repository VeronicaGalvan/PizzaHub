namespace PizzaHubAPI.Configurations;

public class JwtSettings
{
    public string SecretKey { get; set; } = null!;
    public string Issuer { get; set; } = null!;
    public string Audience { get; set; } = null!;
    public int AccessTokenDurationInMinutes { get; set; }
    public int RefreshTokenDurationInDays { get; set; }
}