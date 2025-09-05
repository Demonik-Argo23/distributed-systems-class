using WarframeApi.Dtos;
using WarframeApi.Repositories;
using WarframeApi.Mappers;
using WarframeApi.Validators;
using System.ServiceModel;

namespace WarframeApi.Services;

public class WarframeService : IWarframeService
{
    private readonly IWarframeRepository _warframeRepository;

    public WarframeService(IWarframeRepository warframeRepository)
    {
        _warframeRepository = warframeRepository;
    }

    public async Task<DeleteWarframeResponseDto> DeleteWarframe(Guid id, CancellationToken cancellationToken)
    {
        var warframe = await _warframeRepository.GetWarframesByIdAsync(id, cancellationToken);
        if (warframe == null)
        {
            throw new FaultException($"No Warframe with {id} ID found.");
        }

        await _warframeRepository.DeleteWarframeAsync(warframe, cancellationToken);
        return new DeleteWarframeResponseDto { Success = true };
    }

    public async Task<WarframeResponseDto> GetWarframeById(Guid id, CancellationToken cancellationToken)
    {
        var warframe = await _warframeRepository.GetWarframesByIdAsync(id, cancellationToken);
        if (warframe == null)
        {
            throw new KeyNotFoundException($"Warframe with ID {id} not found.");
        }
        return warframe.ToResponseDto();
    }

    public async Task<WarframeResponseDto> CreateWarframe(CreateWarframeDto warframeDto, CancellationToken cancellationToken)
    {
        warframeDto.ValidateRequiredFields();

        warframeDto
            .ValidateName()
            .ValidateRol()
            .ValidateRank()
            .ValidateStats();

        var warframe = warframeDto.ToModel();

        var createdWarframe = await _warframeRepository.CreateAsync(warframe, cancellationToken);

        return createdWarframe.ToResponseDto();
    }
}