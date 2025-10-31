namespace PokedexApi.Dtos;

public class TrainerResponseDto
{
    public string Id { get; set; } = string.Empty;
    public string Name { get; set; } = string.Empty;
    public int Age { get; set; }
    public DateTime Birthdate { get; set; }
    public DateTime CreatedAt { get; set; }
    public required IList<MedalDto> Medals { get; set; }
}

public class MedalDto
{
    public string Region { get; set; } = string.Empty;
    public int Type { get; set; }
}