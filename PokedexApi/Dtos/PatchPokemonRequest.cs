namespace Pokedex.Dtos;
public class PatchPokemonRequest
{
    public string? Name { get; set; }
    public string? Type { get; set; }
    public int? Attack { get; set; }
    public int? Defense { get; set; }
    public int? Speed { get; set; }
    public int? HP { get; set; }
}