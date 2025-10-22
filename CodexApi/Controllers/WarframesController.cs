using Microsoft.AspNetCore.Mvc;
using CodexApi.Dtos;
using CodexApi.Services;
using CodexApi.Mappers;
using CodexApi.Exceptions;

namespace CodexApi.Controllers;

/// <summary>
/// Controller for managing Warframe operations
/// </summary>
[ApiController]
[Route("api/v1/[controller]")]
public class WarframesController : ControllerBase
{
    private readonly IWarframeService _warframeService;

    public WarframesController(IWarframeService warframeService)
    {
        _warframeService = warframeService;
    }

    [HttpGet("{id}")]
    [ProducesResponseType(typeof(WarframeResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<ActionResult<WarframeResponse>> GetWarframeById(Guid id, CancellationToken cancellationToken)
    {
        if (id == Guid.Empty)
        {
            return BadRequest(new { message = "Invalid Warframe ID. ID cannot be empty." });
        }

        try
        {
            var warframe = await _warframeService.GetWarframeByIdAsync(id, cancellationToken);
            return Ok(warframe.ToResponse());
        }
        catch (WarframeNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
    }


    [HttpGet(Name = "GetWarframesAsync")]
    [ProducesResponseType(typeof(IEnumerable<WarframeResponse>), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<IEnumerable<WarframeResponse>>> GetWarframesAsync(
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 10,
        [FromQuery] string name = "",
        CancellationToken cancellationToken = default)
    {
        var warframes = await _warframeService.GetWarframesAsync(page, pageSize, name, cancellationToken);
        var response = warframes.Select(w => w.ToResponse());
        
        return Ok(response);
    }


    [HttpPost]
    [ProducesResponseType(typeof(WarframeResponse), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<ActionResult<WarframeResponse>> CreateWarframe(
        [FromBody] CreateWarframeRequest request,
        CancellationToken cancellationToken)
    {
        //revisar si el modelo de warframe es corecto, de lo contrario retorna 400
        if (!ModelState.IsValid)
        {
            var errors = ModelState
                .Where(x => x.Value?.Errors.Count > 0)
                .ToDictionary(
                    kvp => kvp.Key.ToLowerInvariant(),
                    kvp => kvp.Value?.Errors.Select(e => e.ErrorMessage).ToArray() ?? Array.Empty<string>()
                );

            return BadRequest(new { message = "Validation failed", errors });
        }

        try
        {
            var warframe = await _warframeService.CreateWarframeAsync(request, cancellationToken);
            var response = warframe.ToResponse();
            return CreatedAtAction(nameof(GetWarframeById), new { id = response.Id }, response);
        }
        catch (WarframeAlreadyExistsException ex)
        {
            return Conflict(new { message = ex.Message });
        }
        catch (ValidationException ex)
        {
            return BadRequest(new { message = ex.Message, errors = ex.Errors });
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpPut("{id}")]
    [ProducesResponseType(typeof(WarframeResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<WarframeResponse>> UpdateWarframe(
        Guid id,
        [FromBody] UpdateWarframeRequest request,
        CancellationToken cancellationToken)
    {
        if (id == Guid.Empty)
        {
            return BadRequest(new { message = "Invalid Warframe ID. ID cannot be empty." });
        }

        if (!ModelState.IsValid)
        {
            var errors = ModelState
                .Where(x => x.Value?.Errors.Count > 0)
                .ToDictionary(
                    kvp => kvp.Key.ToLowerInvariant(),
                    kvp => kvp.Value?.Errors.Select(e => e.ErrorMessage).ToArray() ?? Array.Empty<string>()
                );

            return BadRequest(new { message = "Validation failed", errors });
        }

        try
        {
            var warframe = await _warframeService.UpdateWarframeAsync(id, request, cancellationToken);
            return Ok(warframe.ToResponse());
        }
        catch (WarframeNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (ValidationException ex)
        {
            return BadRequest(new { message = ex.Message, errors = ex.Errors });
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpPatch("{id}")]
    [ProducesResponseType(typeof(WarframeResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<WarframeResponse>> PatchWarframe(
        Guid id,
        [FromBody] PatchWarframeRequest request,
        CancellationToken cancellationToken)
    {
        if (id == Guid.Empty)
        {
            return BadRequest(new { message = "Invalid Warframe ID. ID cannot be empty." });
        }

        if (!ModelState.IsValid)
        {
            var errors = ModelState
                .Where(x => x.Value?.Errors.Count > 0)
                .ToDictionary(
                    kvp => kvp.Key.ToLowerInvariant(),
                    kvp => kvp.Value?.Errors.Select(e => e.ErrorMessage).ToArray() ?? Array.Empty<string>()
                );

            return BadRequest(new { message = "Validation failed", errors });
        }

        try
        {
            var warframe = await _warframeService.PatchWarframeAsync(id, request, cancellationToken);
            return Ok(warframe.ToResponse());
        }
        catch (WarframeNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (ValidationException ex)
        {
            return BadRequest(new { message = ex.Message, errors = ex.Errors });
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpDelete("{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> DeleteWarframe(Guid id, CancellationToken cancellationToken)
    {
        if (id == Guid.Empty)
        {
            return BadRequest(new { message = "Invalid Warframe ID. ID cannot be empty." });
        }

        try
        {
            await _warframeService.DeleteWarframeAsync(id, cancellationToken);
            return NoContent();
        }
        catch (WarframeNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
    }
}