using System.ServiceModel;
using PokedexApi.Infrastructure.Soap.Contracts;
using PokedexApi.Models;
using PokedexApi.Mappers;

namespace PokedexApi.Gateways;

public class PokemonGateway : IPokemonGateway
{
    public readonly IPokemonContract _pokemonContract;

    public PokemonGateway(IConfiguration configuration)
    {
        var binding = new BasicHttpBinding();
        var endpoint = new EndpointAddress(configuration.GetValue<string>("PokemonService:Url"));
        _pokemonContract = new ChannelFactory<IPokemonContract>(binding, endpoint).CreateChannel();
    }
    public async Task<Pokemon> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        try
        {
            var pokemon = await _pokemonContract.GetPokemonById(id, cancellationToken);
            return pokemon.ToModel();
        }
        catch (FaultException ex) when (ex.Message == "Pokemon not found")
        {
            return null;
        }
    }
}