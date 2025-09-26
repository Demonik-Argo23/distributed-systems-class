using PokedexApi.Models;
using PokedexApi.Dtos;

namespace PokedexApi.Gateways;
//Como si fuera un repositorio pero para un servicio externo
//Clean architecture - Interface Adapter / Hexagonal architecture
public interface IPokemonGateway
{
    Task<Pokemon> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken);

    Task<IList<Pokemon>> GetPokemonByNameAsync(string name, CancellationToken cancellationToken);

    Task<Pokemon> CreatePokemonAsync(Pokemon pokemon, CancellationToken cancellationToken);

    Task DeletePokemonAsync(Guid id, CancellationToken cancellationToken);

    Task<(IList<Pokemon> pokemons, int totalRecords)> GetPokemonsAsync(
        string name,
        string type,
        int pageNumber,
        int pageSize,
        string orderBy,
        string orderDirection,
        CancellationToken cancellationToken);
}