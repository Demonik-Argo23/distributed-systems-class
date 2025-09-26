using PokedexApi.Models;

namespace PokedexApi.Services;

public interface IPokemonService
{
    Task<Pokemon> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken);

    Task<Pokemon> CreatePokemonAsync(Pokemon pokemon, CancellationToken cancellationToken);

    Task<IList<Pokemon>> GetPokemonsAsync(string name, string type, CancellationToken cancellationToken);

    Task DeletePokemonAsync(Guid id, CancellationToken cancellationToken);
    Task<Pokemon> UpdatePokemonAsync(Pokemon pokemon, CancellationToken cancellationToken);

    Task<Pokemon> PatchPokemonAsync(
        Guid id,
        string? name,
        string? type,
        int? attack,
        int? defense,
        int? speed,
        int? HP, CancellationToken cancellationToken);
}
