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