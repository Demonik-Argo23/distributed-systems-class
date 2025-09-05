using Microsoft.AspNetCore.Mvc;
using PokedexApi.Dtos;

namespace PokedexApi.Controllers;

[ApiController]
[Route("api/v1/[controller]")]
public class PokemonsController : ControllerBase
{
    //localhost:PORT/api/v1/pokemons/ID
    [HttpGet("{id}")]
    public async Task<ActionResult<PokemonResponse>> GetPokemonByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        //HTTP 200
        return Ok();
    }
}