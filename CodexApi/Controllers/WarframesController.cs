using Microsoft.AspNetCore.Mvc;
using CodexApi.Dtos;
using CodexApi.Services;
using CodexApi.Mappers;

namespace CodexApi.Controllers;

[ApiController]
[Route("api/v1/[controller]")]
public class WarframesController : ControllerBase
{
    private readonly IWarframeService _warframeService;

    public WarframesController(IWarframeService warframeService)
    {
        _warframeService = warframeService;
    }

    [HttpGet("{id}", Name = "GetWarframeByIdAsync")]
    [ProducesResponseType(typeof(WarframeResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<WarframeResponse>> GetWarframeByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        var warframe = await _warframeService.GetWarframeByIdAsync(id, cancellationToken);
        return warframe is null ? NotFound() : Ok(warframe.ToResponse());
    }
}