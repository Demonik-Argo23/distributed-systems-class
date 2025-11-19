namespace PokedexApi.Dtos;

public class UpdateTrainerRequestDto
{
    public string Name { get; set; }
    public int Age { get; set; }
    public DateTime Birthdate { get; set; }
    public List<MedalDto> Medals { get; set; }
    
}