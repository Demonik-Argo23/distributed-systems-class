using Microsoft.AspNetCore.Mvc;
using PokedexApi.Exceptions;
using PokedexApi.Models;
using PokedexApi.Dtos;
using PokedexApi.Services;

namespace PokedexApi.Controllers;

[ApiController]
[Route("api/v1/[controller]")]
public class PokemonsController : ControllerBase
{
    private readonly IPokemonService _pokemonService;

    public PokemonsController(IPokemonService pokemonService)
    {
        _pokemonService = pokemonService;
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<PokemonResponse>> GetPokemonById(Guid id, CancellationToken cancellationToken)
    {
        try
        {
            var pokemon = await _pokemonService.GetPokemonByIdAsync(id, cancellationToken);
            return Ok(ToDto(pokemon));
        }
        catch (PokemonNotFoundException)
        {
            return NotFound();
        }
    }

    [HttpGet]
    public async Task<ActionResult<PagedResponse<PokemonResponse>>> GetPokemons(
        [FromQuery] string name,
        [FromQuery] string type,
        [FromQuery] int pageNumber = 1,
        [FromQuery] int pageSize = 10,
        [FromQuery] string orderBy = "name",
        [FromQuery] string orderDirection = "asc",
        CancellationToken cancellationToken = default)
    {
        var result = await _pokemonService.GetPokemonsAsync(name, type, pageNumber, pageSize, orderBy, orderDirection, cancellationToken);
        return Ok(result);
    }

    [HttpPost]
    public async Task<ActionResult<PokemonResponse>> CreatePokemon([FromBody] CreatePokemonRequest request, CancellationToken cancellationToken)
    {
        try
        {
            var pokemon = ToModel(request);
            var createdPokemon = await _pokemonService.CreatePokemonAsync(pokemon, cancellationToken);
            return CreatedAtAction(nameof(GetPokemonById), new { id = createdPokemon.Id }, ToDto(createdPokemon));
        }
        catch (PokemonAlreadyExistsException)
        {
            return Conflict();
        }
    }

    [HttpPut("{id}")]
    public async Task<ActionResult<PokemonResponse>> UpdatePokemon(Guid id, [FromBody] UpdatePokemonRequest request, CancellationToken cancellationToken)
    {
        try
        {
            var pokemon = ToModel(id, request);
            var updatedPokemon = await _pokemonService.UpdatePokemonAsync(pokemon, cancellationToken);
            return Ok(ToDto(updatedPokemon));
        }
        catch (PokemonNotFoundException)
        {
            return NotFound();
        }
    }

    [HttpPatch("{id}")]
    public async Task<ActionResult<PokemonResponse>> PatchPokemon(Guid id, [FromBody] PatchPokemonRequest request, CancellationToken cancellationToken)
    {
        try
        {
            var updatedPokemon = await _pokemonService.PatchPokemonAsync(id, request.Name, request.Type, request.Attack, request.Defense, request.Speed, request.HP, cancellationToken);
            return Ok(ToDto(updatedPokemon));
        }
        catch (PokemonNotFoundException)
        {
            return NotFound();
        }
    }

    [HttpDelete("{id}")]
    public async Task<ActionResult> DeletePokemon(Guid id, CancellationToken cancellationToken)
    {
        try
        {
            await _pokemonService.DeletePokemonAsync(id, cancellationToken);
            return NoContent();
        }
        catch (PokemonNotFoundException)
        {
            return NotFound();
        }
    }

    private static PokemonResponse ToDto(Pokemon pokemon)
    {
        return new PokemonResponse
        {
            Id = pokemon.Id,
            Name = pokemon.Name,
            Type = pokemon.Type,
            Attack = pokemon.Stats.Attack
        };
    }

    private static Pokemon ToModel(CreatePokemonRequest request)
    {
        return new Pokemon
        {
            Id = Guid.NewGuid(),
            Name = request.Name,
            Type = request.Type,
            Level = request.Level,
            Stats = new Stats
            {
                HP = request.Stats.HP,
                Attack = request.Stats.Attack,
                Defense = request.Stats.Defense,
                Speed = request.Stats.Speed
            }
        };
    }

    private static Pokemon ToModel(Guid id, UpdatePokemonRequest request)
    {
        return new Pokemon
        {
            Id = id,
            Name = request.Name,
            Type = request.Type,
            Level = 1, // Default level since UpdatePokemonRequest doesn't have Level
            Stats = new Stats
            {
                HP = request.Stats.HP,
                Attack = request.Stats.Attack,
                Defense = request.Stats.Defense,
                Speed = request.Stats.Speed
            }
        };
    }
}