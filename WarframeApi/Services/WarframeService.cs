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

    public async Task<WarframeResponseDto> UpdateWarframe(Guid id, CreateWarframeDto warframeToUpdate, CancellationToken cancellationToken)
    {
        Console.WriteLine($"UpdateWarframe called with ID: {id}, DTO: {warframeToUpdate?.Name ?? "null"}");
        
        if (warframeToUpdate is null)
        {
            throw new FaultException("UpdateWarframeDto cannot be null. Check SOAP message format and serialization.");
        }

        var warframe = await _warframeRepository.GetWarframesByIdAsync(id, cancellationToken);
        if (warframe == null)
        {
            throw new FaultException($"No Warframe with {id} ID found.");
        }
        
        if (!await IsUpdatable(id, warframeToUpdate, cancellationToken))
        {
            throw new FaultException($"Another Warframe with name {warframeToUpdate.Name} already exists.");
        }

        warframe.Name = warframeToUpdate.Name;
        warframe.Rank = warframeToUpdate.Rank;
        warframe.Rol = warframeToUpdate.Rol;
        warframe.Stats.Hp = warframeToUpdate.Stats.Hp;
        warframe.Stats.Shield = warframeToUpdate.Stats.Shield;
        warframe.Stats.Armor = warframeToUpdate.Stats.Armor;
        warframe.Stats.Energy = warframeToUpdate.Stats.Energy;

        await _warframeRepository.UpdateWarframeAsync(warframe, cancellationToken);
        return warframe.ToResponseDto();
    }

    public async Task<WarframeResponseDto> PatchWarframe(Guid id, CreateWarframeDto warframeToPatch, CancellationToken cancellationToken)
    {
        Console.WriteLine($"PatchWarframe called with ID: {id}, DTO: {warframeToPatch?.Name ?? "null"}");
        
        if (warframeToPatch is null)
        {
            throw new FaultException("PatchWarframeDto cannot be null. Check SOAP message format and serialization.");
        }

        var warframe = await _warframeRepository.GetWarframesByIdAsync(id, cancellationToken);
        if (warframe == null)
        {
            throw new FaultException($"No Warframe with {id} ID found.");
        }

        if (!string.IsNullOrWhiteSpace(warframeToPatch.Name))
        {
            if (warframe.Name != warframeToPatch.Name)
            {
                var duplicatedWarframe = await _warframeRepository.GetWFByNameAsync(warframeToPatch.Name, cancellationToken);
                if (duplicatedWarframe != null)
                {
                    throw new FaultException($"Another Warframe with name {warframeToPatch.Name} already exists.");
                }
            }
            warframe.Name = warframeToPatch.Name;
        }

        if (!string.IsNullOrWhiteSpace(warframeToPatch.Rol))
        {
            warframe.Rol = warframeToPatch.Rol;
        }

        if (warframeToPatch.Rank > 0)
        {
            warframe.Rank = warframeToPatch.Rank;
        }

        if (warframeToPatch.Stats != null)
        {
            if (warframeToPatch.Stats.Hp > 0)
                warframe.Stats.Hp = warframeToPatch.Stats.Hp;
            
            if (warframeToPatch.Stats.Shield > 0)
                warframe.Stats.Shield = warframeToPatch.Stats.Shield;
            
            if (warframeToPatch.Stats.Armor > 0)
                warframe.Stats.Armor = warframeToPatch.Stats.Armor;
            
            if (warframeToPatch.Stats.Energy > 0)
                warframe.Stats.Energy = warframeToPatch.Stats.Energy;
        }

        await _warframeRepository.UpdateWarframeAsync(warframe, cancellationToken);
        return warframe.ToResponseDto();
    }

    private async Task<bool> IsUpdatable(Guid id, CreateWarframeDto warframeToUpdate, CancellationToken cancellationToken)
    {
        var duplicatedWarframe = await _warframeRepository.GetWFByNameAsync(warframeToUpdate.Name, cancellationToken);
        return duplicatedWarframe is null || IsTheSameWarframe(duplicatedWarframe, id);
    }

    private static bool IsTheSameWarframe(Warframe warframe, Guid id)
    {
        return warframe.Id == id;
    }

    public async Task<DeleteWarframeResponseDto> DeleteWarframe(Guid id, CancellationToken cancellationToken)
    {
        var warframe = await _warframeRepository.GetWarframesByIdAsync(id, cancellationToken);
        if (warframe == null)
        {
            throw new FaultException($"No Warframe with {id} ID found.");
        }

        await _warframeRepository.DeleteWarframeAsync(warframe, cancellationToken);
        return new DeleteWarframeResponseDto 
        { 
            Success = true, 
            Message = $"Warframe with ID {id} deleted successfully." 
        };
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
        try
        {
            Console.WriteLine($"CreateWarframe called with: {warframeDto?.Name ?? "null DTO"}");
            
            if (warframeDto is null)
            {
                throw new FaultException("CreateWarframeDto cannot be null. Check SOAP message format and serialization.");
            }

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
        catch (ArgumentNullException ex)
        {
            throw new FaultException($"Invalid request: {ex.Message}");
        }
        catch (ArgumentException ex)
        {
            throw new FaultException($"Invalid data: {ex.Message}");
        }
        catch (FaultException)
        {
            throw; 
        }
        catch (Exception ex)
        {
            throw new FaultException($"An error occurred while creating the warframe: {ex.Message}");
        }
    }

    public async Task<PagedResultDto<WarframeResponseDto>> GetWarframeByNamePaged(string name, PaginationRequestDto pagination, CancellationToken cancellationToken)
    {
        var (items, totalCount) = await _warframeRepository.GetWarframesByNamePagedAsync(name, pagination.PageNumber, pagination.PageSize, cancellationToken);
        
        return new PagedResultDto<WarframeResponseDto>
        {
            Items = items.ToResponseDto().ToList(),
            PageNumber = pagination.PageNumber,
            PageSize = pagination.PageSize,
            TotalCount = totalCount,
            TotalPages = (int)Math.Ceiling((double)totalCount / pagination.PageSize),
            HasNextPage = pagination.PageNumber * pagination.PageSize < totalCount,
            HasPreviousPage = pagination.PageNumber > 1
        };
    }

    public async Task<PagedResultDto<WarframeResponseDto>> GetWarframes(int page, int pageSize, string name, CancellationToken cancellationToken)
    {
        var (items, totalCount) = await _warframeRepository.GetWarframesByNamePagedAsync(name, page, pageSize, cancellationToken);
        
        return new PagedResultDto<WarframeResponseDto>
        {
            Items = items.ToResponseDto().ToList(),
            PageNumber = page,
            PageSize = pageSize,
            TotalCount = totalCount,
            TotalPages = (int)Math.Ceiling((double)totalCount / pageSize),
            HasNextPage = page * pageSize < totalCount,
            HasPreviousPage = page > 1
        };
    }
}