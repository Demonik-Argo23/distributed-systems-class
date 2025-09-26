using PokedexApi.Models;
using PokedexApi.Dtos;

namespace PokedexApi.Services;

public interface IPokemonService
{
    Task<Pokemon> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken);

    Task<Pokemon> CreatePokemonAsync(Pokemon pokemon, CancellationToken cancellationToken);

    Task<IList<Pokemon>> GetPokemonsAsync(string name, string type, CancellationToken cancellationToken);

    Task<PagedResponse<PokemonResponse>> GetPokemonsAsync(
        string name,
        string type,
        int pageNumber,
        int pageSize,
        string orderBy,
        string orderDirection,
        CancellationToken cancellationToken);
    Task DeletePokemonAsync(Guid id, CancellationToken cancellationToken);
}