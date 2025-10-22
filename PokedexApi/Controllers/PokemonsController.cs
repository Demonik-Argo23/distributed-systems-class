using Microsoft.AspNetCore.Mvc;
using PokedexApi.Dtos;
using PokedexApi.Models;
using PokedexApi.Services;
using PokedexApi.Mappers;
using PokedexApi.Exceptions;
using Pokedex.Dtos;

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
    [HttpGet("{id}", Name = "GetPokemonByIdAsync")] // api/v1/pokemons/{id}
    public async Task<ActionResult<PokemonResponse>> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        var pokemon = await _pokemonService.GetPokemonByIdAsync(id, cancellationToken);
        return pokemon is null ? NotFound() : Ok(pokemon.ToResponse());
    }

    //localhost:PORT/api/v1/pokemons?name=pikachu&type=fire
    //HTTP STATUS - GET
    // 200 OK (Si existe o no pokemon (se regresa listado vacio))
    // 400 Bad Request (Si los parametros son invalidos)
    // 500 Internal Server Error (Error del servidor)
    [HttpGet]
    public async Task<ActionResult<PagedResponse<PokemonResponse>>> GetPokemonsAsync(
        [FromQuery] string? name,
        [FromQuery] string? type,
        CancellationToken cancellationToken,
        [FromQuery] int pageNumber = 1,
        [FromQuery] int pageSize = 10,
        [FromQuery] string orderBy = "Name",
        [FromQuery] string orderDirection = "asc")
    {
        var pokemons = await _pokemonService.GetPokemonsAsync(name, type, pageNumber, pageSize, orderBy, orderDirection, cancellationToken);
        return Ok(pokemons);
    }

    //Http Verb - Post
    //Http Status
    // 400 Bad Request (Si usuario manda informacion erronea)
    // 409 Conflict (Si el pokemon ya existe)
    // 422 Unprocessable Entity (Si el pokemon no cumple con las reglas de negocio interna)
    // 500 Internal Server Error (Error del servidor)
    // 200 OK (El recurso creado + id) no sigue muchos para buenas practicas de RESTFul
    // 201 Created (El recurso creado + id) href = hace referencia al get para obtener el recurso
    // 202 Accepted (Si la creacion del recurso es asincrona y toma tiempo)

    [HttpPost]
    public async Task<ActionResult<PokemonResponse>> CreatePokemonAsync([FromBody] CreatePokemonRequest createPokemon, CancellationToken cancellationToken)
    {
        try
        {
            if (!IsValidAttack(createPokemon))
            {
                return BadRequest(new { Message = "Attack does not have a valid value" });
            }

            var pokemon = await _pokemonService.CreatePokemonAsync(createPokemon.ToModel(), cancellationToken);

            // 201
            return CreatedAtRoute(nameof(GetPokemonByIdAsync), routeValues: new { id = pokemon.Id }, pokemon.ToResponse());
        }
        catch (PokemonAlreadyExistsException e)
        {
            // 409 Conflict - { Message = "Pokemon NAME already exists" }
            return Conflict(new { Message = e.Message });
        }
    }

    //localhost:PORT/api/v1/pokemons/ID
    // HTTP Verb - DELETE
    // HTTP STATUS
    // 204 No Content (Si se borro correctamente)
    // 404 Not Found (Si el pokemon no existe) No sigue muy bien las buenas practicas de RESTFul
    // 500 Internal Server Error (Error del servidor)
    [HttpDelete("{id}")]
    public async Task<ActionResult> DeletePokemonAsync(Guid id, CancellationToken cancellationToken)
    {
        try
        {
            await _pokemonService.DeletePokemonAsync(id, cancellationToken);
            return NoContent(); // 204
        }
        catch (PokemonNotFoundException)
        {
            return NotFound(); // 404
        }
    }

    private static bool IsValidAttack(CreatePokemonRequest createPokemon)
    {
        return createPokemon.Stats.Attack > 0;
    }

    //LocalHost:Port/api/v1/pokemons
    //Http Verb - Put
    //Http Status
    // 200 OK (Si modifico el pokemon) returna el pokemon modificado
    // 400 Bad Request (Si usuario manda informacion erronea)
    // 204 No Content (Si se actualizo correctamente) mas orientado a Restuful
    // 404 Not Found (Si el pokemon no existe)
    // 500 Internal Server Error (Error del servidor)

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdatePokemonAsync(Guid id, [FromBody] UpdatePokemonRequest pokemon ,CancellationToken cancellationToken)
    {
        try
        {
            if (!IsValidAttack(pokemon.Stats.Attack))
            {
                return BadRequest(new { Message = "Attack does not have a valid value" });
            }
            
            await _pokemonService.UpdatePokemonAsync(pokemon.ToModel(id), cancellationToken);
            return NoContent(); //204
        }
        catch (PokemonNotFoundException)
        {
            return NotFound(); //404
        }
        catch (PokemonAlreadyExistsException ex)
        {
            return Conflict(new { Message = ex.Message }); // 409
        }
    }

    //Localhost:PORT/api/v1/pokemons/id
    //Http Verb - Patch
    //Http Status
    // 200 OK (Si modifico el pokemon) mas recomendado aqui
    //204 No Content (Si se actualizo correctamente) 
    // 400 Bad Request (Si usuario manda informacion erronea)
    // 404 Not Found (Si el pokemon no existe)
    // 409 Conflict (Si el pokemon ya existe)

    [HttpPatch("{id}")]
    public async Task<ActionResult> PatchPokemonAsync(Guid id, [FromBody] PatchPokemonRequest pokemonRequest, CancellationToken cancellationToken)
    {
        try
        {
            if (pokemonRequest.Attack.HasValue && !IsValidAttack(pokemonRequest.Attack.Value))
            {
                return BadRequest(new { Message = "Attack does not have a valid value" });
            }

            var pokemon = await _pokemonService.PatchPokemonAsync(id, pokemonRequest.Name, pokemonRequest.Type, pokemonRequest.Attack, pokemonRequest.Defense, pokemonRequest.Speed, pokemonRequest.HP, cancellationToken);
            return Ok(pokemon.ToResponse()); //200
        }
        catch (PokemonNotFoundException)
        {
            return NotFound(); //404
        }
        catch (PokemonAlreadyExistsException ex)
        {
            return Conflict(new { Message = ex.Message }); // 409
        }
    }


    private static bool IsValidAttack(int attack)
    {
        return attack > 0;
    }
}