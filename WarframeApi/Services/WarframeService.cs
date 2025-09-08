using WarframeApi.Dtos;
using WarframeApi.Repositories;
using WarframeApi.Mappers;
using WarframeApi.Validators;
using System.ServiceModel;
using WarframeApi.Models;

namespace WarframeApi.Services;

public class WarframeService : IWarframeService
{
    private readonly IWarframeRepository _warframeRepository;

    public WarframeService(IWarframeRepository warframeRepository)
    {
        _warframeRepository = warframeRepository;
    }

    public async Task<List<WarframeResponseDto>> GetWarframeByName(string name, CancellationToken cancellationToken)
    {
        var warframes = await _warframeRepository.GetWarframesByNameAsync(name, cancellationToken);
        return warframes.ToResponseDto().ToList();
    }

    public async Task<List<WarframeResponseDto>> GetWarframeByRank(int rank, CancellationToken cancellationToken)
    {
        var warframes = await _warframeRepository.GetWarframesByRankAsync(rank, cancellationToken);
        return warframes.ToResponseDto().ToList();
    }

    public async Task<WarframeResponseDto> UpdateWarframe(UpdateWarframeDto warframeToUpdate, CancellationToken cancellationToken)
    {
        var warframe = await _warframeRepository.GetWarframesByIdAsync(warframeToUpdate.Id, cancellationToken);
        if (warframe == null)
        {
            throw new FaultException($"No Warframe with {warframeToUpdate.Id} ID found.");
        }

        warframe.Name = warframeToUpdate.Name;
        warframe.Rank = warframeToUpdate.Rank;
        warframe.Rol = warframeToUpdate.Role;
        warframe.Stats.Hp = warframeToUpdate.Stats.Hp;
        warframe.Stats.Shield = warframeToUpdate.Stats.Shield;
        warframe.Stats.Armor = warframeToUpdate.Stats.Armor;
        warframe.Stats.Energy = warframeToUpdate.Stats.Energy;

        await _warframeRepository.UpdateWarframeAsync(warframe, cancellationToken);
        return warframe.ToResponseDto();
    }

    private async Task<bool> IsUpdatable(UpdateWarframeDto WarframeToUpdate, CancellationToken cancellationToken)
    {
        var duplicatedWarframe = await _warframeRepository.GetWFByNameAsync(WarframeToUpdate.Name, cancellationToken);
        return duplicatedWarframe is null || IsTheSameWarframe(duplicatedWarframe, WarframeToUpdate);
    }

    private static bool IsTheSameWarframe(Warframe warframe, UpdateWarframeDto warframeToUpdate)
    {
        return warframe.Id == warframeToUpdate.Id;
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