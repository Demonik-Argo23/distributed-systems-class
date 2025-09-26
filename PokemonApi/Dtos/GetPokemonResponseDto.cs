using PokemonApi.Dtos;

public class PagedPokemonResponseDto
{
    public List<PokemonResponseDto> Pokemons { get; set; }
    public int TotalRecords { get; set; }
}