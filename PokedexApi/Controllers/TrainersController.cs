using Microsoft.AspNetCore.Mvc;
using PokedexApi.Exceptions;
using PokedexApi.Models;
using PokedexApi.Dtos;
using PokedexApi.Services;

namespace PokedexApi.Controllers;

[ApiController]
[Route("api/v1/[controller]")]
public class TrainersController : ControllerBase
{
    private readonly ITrainerService _TrainerService;
    public TrainersController(ITrainerService trainerService)
    {
        _TrainerService = trainerService;
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<TrainerResponseDto>> GetTrainerByIdAsync(string id, CancellationToken cancellationToken)
    {
        try
        {
            var trainer = await _TrainerService.GetByIdAsync(id, cancellationToken);
            var trainerDto = ToDto(trainer);
            return Ok(trainerDto);
        }
        catch (TrainerNotFoundException)
        {
            return NotFound();
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteTrainer(string id, CancellationToken cancellationToken)
    {
        try
        {
            await _TrainerService.DeleteTrainerAsync(id, cancellationToken);
            return NoContent();
        }
        catch (TrainerNotFoundException ex)
        {
            return NotFound(new { Message = ex.Message });
        }
        catch (TrainerInvalidException ex)
        {
            return BadRequest(new { Message = ex.Message });
        }
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateTrainer(string id,
    [FromBody] UpdateTrainerRequestDto request, CancellationToken cancellationToken)
    {
        try
        {
            var trainer = ToModel(id, request);
            await _TrainerService.UpdateTrainerAsync(trainer, cancellationToken);
            return NoContent();
        }
        catch (TrainerNotFoundException ex)
        {
            return NotFound(new { Message = ex.Message });
        }
        catch (TrainerValidationException ex)
        {
            return BadRequest(new { Message = ex.Message });
        }

        catch (TrainerAlreadyExistsException ex)
        {
            return Conflict(new { Message = ex.Message });
        }
    }

    private static Trainer ToModel(string id, UpdateTrainerRequestDto request)
    {
        return new Trainer
        {
            Id = id,
            Name = request.Name,
            Age = request.Age,
            BirthDate = request.Birthdate,
            Medals = request.Medals.Select(ToModel).ToList()
        };
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<TrainerResponseDto>>> GetTrainersByName([FromQuery] string name, CancellationToken cancellationToken)
    {
        var trainers = await _TrainerService.GetAllByNameAsync(name, cancellationToken);
        return Ok(trainers.Select(ToDto));
    }

    [HttpPost]
    public async Task<ActionResult> CreateTrainers([FromBody] List<CreateTrainerRequestDto> request, CancellationToken cancellationToken)
    {
        var trainers = request.Select(ToModel);
        var (successCount, createdTrainers) = await _TrainerService.CreateTrainersBulkAsync(trainers, cancellationToken);
        return Ok(new { SuccessCount = successCount, Trainers = createdTrainers.Select(ToDto) });
    }

    private static Trainer ToModel(CreateTrainerRequestDto request)
    {
        return new Trainer
        {
            Id = Guid.NewGuid().ToString(),
            Name = request.Name,
            Age = request.Age,
            BirthDate = request.Birthdate,
            CreatedAt = DateTime.UtcNow,
            Medals = request.Medals.Select(ToModel).ToList()
        };
    }

    private static Models.Medal ToModel(MedalDto medalDto)
    {
        return new Models.Medal
        {
            Region = medalDto.Region,
            Type = (MedalType)medalDto.Type
        };
    }

    private static TrainerResponseDto ToDto(Trainer trainer)
    {
        return new TrainerResponseDto
        {
            Id = trainer.Id,
            Name = trainer.Name,
            Age = trainer.Age,
            Birthdate = trainer.BirthDate,
            CreatedAt = trainer.CreatedAt,
            Medals = trainer.Medals.Select(m => ToDto(m)).ToList()
        };
    }

    private static MedalDto ToDto(Models.Medal medal)
    {
        return new MedalDto
        {
            Region = medal.Region,
            Type = (int)medal.Type
        };
    }
}