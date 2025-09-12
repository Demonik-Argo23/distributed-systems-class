using PokedexApi.Gateways;
using PokedexApi.Models;

namespace PokedexApi.Services;

public class PokemonService : IPokemonService
{
    private readonly IPokemonGateway _pokemonGateway;
    public PokemonService(IPokemonGateway pokemonGateway)
    {
        _pokemonGateway = pokemonGateway;
    }
    public async Task<Pokemon> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        return await _pokemonGateway.GetPokemonByIdAsync(id, cancellationToken);
    }
}

//Arquitectura (BFF o Experience API o Aggregator Pattern)
//Cliente (Postman, Insomnia, algun aplicativo WEB) -> Rest API (Pokedex Api) -> PokedexApi (SOAP)