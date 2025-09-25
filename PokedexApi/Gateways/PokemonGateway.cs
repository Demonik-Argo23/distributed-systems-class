using System.ServiceModel;
using PokedexApi.Infrastructure.Soap.Contracts;
using PokedexApi.Models;
using PokedexApi.Mappers;
using PokedexApi.Exceptions;
using PokedexApi.Infrastructure.Soap.Dtos;
using PokedexApi.Dtos;
using System.ServiceModel.Channels;

namespace PokedexApi.Gateways;

public class PokemonGateway : IPokemonGateway
{
    public readonly IPokemonContract _pokemonContract;
    private readonly ILogger<PokemonGateway> _logger;

    public PokemonGateway(IConfiguration configuration, ILogger<PokemonGateway> logger)
    {
        var binding = new BasicHttpBinding();
        var endpoint = new EndpointAddress(configuration.GetValue<string>("PokemonService:Url"));
        _pokemonContract = new ChannelFactory<IPokemonContract>(binding, endpoint).CreateChannel();
        _logger = logger;
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
            _logger.LogWarning(ex, "Pokemon not found");
            throw new PokemonNotFoundException(id);
        }
    }

    public async Task DeletePokemonAsync(Guid id, CancellationToken cancellationToken)
    {
        try
        {
            await _pokemonContract.DeletePokemon(id, cancellationToken);
        }
        catch (FaultException ex) when (ex.Message == "Pokemon not found")
        {
            _logger.LogWarning(ex, "Pokemon not found");
            throw new PokemonNotFoundException(id);
        }
    }

    public async Task<IList<Pokemon>> GetPokemonByNameAsync(string name, CancellationToken cancellationToken)
    {
        var pokemons = await _pokemonContract.GetPokemonByName(name, cancellationToken);
        return pokemons.ToModel();
    }

    public async Task<Pokemon> CreatePokemonAsync(Pokemon pokemon, CancellationToken cancellationToken)
    {
        try
        {
            _logger.LogInformation("Sending request to SOAP API, with pokemon: {name}", pokemon.Name);

            var createdPokemon = await _pokemonContract.CreatePokemon(pokemon.ToRequest(), cancellationToken);
            return createdPokemon.ToModel();

        }
        catch (Exception e)
        {
            throw new Exception("Error creating pokemon", e);
        }
    }

    public async Task<PagedResponse<Pokemon>> GetPokemonsPagedAsync(PaginationParameters parameters, CancellationToken cancellationToken)
    {
        var allPokemons = await _pokemonContract.GetPokemonByName(parameters.Name ?? "", cancellationToken);
        var pokemonList = allPokemons.ToModel().ToList();
        
        if (!string.IsNullOrEmpty(parameters.Type))
        {
            pokemonList = pokemonList.Where(p => p.Type.ToLower().Contains(parameters.Type.ToLower())).ToList();
        }

        pokemonList = ApplyOrdering(pokemonList, parameters.OrderBy, parameters.OrderDirection);

        var totalRecords = pokemonList.Count;
        var totalPages = (int)Math.Ceiling((double)totalRecords / parameters.PageSize);
        //función ceiling para redondear hacia arriba
        
        var pagedPokemons = pokemonList
            .Skip((parameters.PageNumber - 1) * parameters.PageSize)
            .Take(parameters.PageSize)
            .ToList();

        return new PagedResponse<Pokemon>
        {
            PageNumber = parameters.PageNumber,
            PageSize = parameters.PageSize,
            TotalRecords = totalRecords,
            TotalPages = totalPages,
            Data = pagedPokemons
        };
    }

    private List<Pokemon> ApplyOrdering(List<Pokemon> pokemons, string orderBy, string orderDirection)
    {
        var isDescending = orderDirection.ToLower() == "desc";
        
        return orderBy.ToLower() switch
        {
            "name" => isDescending ? pokemons.OrderByDescending(p => p.Name).ToList() : pokemons.OrderBy(p => p.Name).ToList(),
            "type" => isDescending ? pokemons.OrderByDescending(p => p.Type).ToList() : pokemons.OrderBy(p => p.Type).ToList(),
            "level" => isDescending ? pokemons.OrderByDescending(p => p.Level).ToList() : pokemons.OrderBy(p => p.Level).ToList(),
            "attack" => isDescending ? pokemons.OrderByDescending(p => p.Stats.Attack).ToList() : pokemons.OrderBy(p => p.Stats.Attack).ToList(),
            _ => pokemons.OrderBy(p => p.Name).ToList()
        };
    }
}