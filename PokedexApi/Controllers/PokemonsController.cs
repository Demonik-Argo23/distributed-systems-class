using Microsoft.AspNetCore.Mvc;
using PokedexApi.Dtos;
using PokedexApi.Models;
using PokedexApi.Services;
using PokedexApi.Mappers;

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

    //Http Status
    // 200 OK (Si existe el pokemon)
    //400 Bad Request (Id invalido) --- Casi no se usa
    // 404 Not Found (no existe el pokemon)
    // 500 Internal Server Error (Error del servidor)

    //Http Verb -Get
    [HttpGet("{id}")] // api/v1/pokemons/{id}
    public async Task<ActionResult<PokemonResponse>> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        var pokemon = await _pokemonService.GetPokemonByIdAsync(id, cancellationToken);
        return pokemon is null ? NotFound() : Ok(pokemon.ToResponse());
    }
}