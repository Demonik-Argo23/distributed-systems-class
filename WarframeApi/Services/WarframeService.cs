using WarframeApi.Dtos;
using WarframeApi.Repositories;
using WarframeApi.Mappers;
using WarframeApi.Validators;

namespace WarframeApi.Services;

public class WarframeService : IWarframeService
{
    private readonly IWarframeRepository _warframeRepository;

    public WarframeService(IWarframeRepository warframeRepository)
    {
        _warframeRepository = warframeRepository;
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