using PokedexApi.Infrastructure.Soap.Dtos;

public class PagedPokemonResponseDto
{
    public List<PokemonResponseDto> Pokemons { get; set; }
    public int TotalRecords { get; set; }
}