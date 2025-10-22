using CodexApi.Models;
using CodexApi.Gateways;
using CodexApi.Dtos;
using CodexApi.Exceptions;

namespace CodexApi.Services;

public class WarframeService : IWarframeService
{
    private readonly IWarframeGateway _warframeGateway;

    public WarframeService(IWarframeGateway warframeGateway)
    {
        _warframeGateway = warframeGateway;
    }

    public async Task<Warframe> GetWarframeByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        var warframe = await _warframeGateway.GetWarframeByIdAsync(id, cancellationToken);
        if (warframe is null)
        {
            throw new WarframeNotFoundException(id);
        }
        return warframe;
    }

    public async Task<IEnumerable<Warframe>> GetWarframesAsync(int page, int pageSize, string name, CancellationToken cancellationToken)
    {
        return await _warframeGateway.GetWarframesAsync(page, pageSize, name, cancellationToken);
    }

    public async Task<Warframe> CreateWarframeAsync(CreateWarframeRequest request, CancellationToken cancellationToken)
    {

        if (string.IsNullOrWhiteSpace(request.Name))
        {
            throw new ValidationException("name", "Name cannot be empty or whitespace");
        }

        if (string.IsNullOrWhiteSpace(request.Rol))
        {
            throw new ValidationException("rol", "Role cannot be empty or whitespace");
        }

        try
        {
            return await _warframeGateway.CreateWarframeAsync(request, cancellationToken);
        }
        catch (Exception ex) when (ex.Message.Contains("already exists"))
        {
            throw new WarframeAlreadyExistsException(request.Name);
        }
    }

    public async Task<Warframe> UpdateWarframeAsync(Guid id, UpdateWarframeRequest request, CancellationToken cancellationToken)
    {
        if (id == Guid.Empty)
        {
            throw new ArgumentException("Warframe ID cannot be empty", nameof(id));
        }

        if (string.IsNullOrWhiteSpace(request.Name))
        {
            throw new ValidationException("name", "Name cannot be empty or whitespace");
        }

        if (string.IsNullOrWhiteSpace(request.Rol))
        {
            throw new ValidationException("rol", "Role cannot be empty or whitespace");
        }

        var success = await _warframeGateway.UpdateWarframeAsync(id, request, cancellationToken);
        if (!success)
        {
            throw new WarframeNotFoundException(id);
        }

        return await GetWarframeByIdAsync(id, cancellationToken);
    }

    public async Task<Warframe> PatchWarframeAsync(Guid id, PatchWarframeRequest request, CancellationToken cancellationToken)
    {
        if (id == Guid.Empty)
        {
            throw new ArgumentException("Warframe ID cannot be empty", nameof(id));
        }

        // Validate that at least one field is provided for patching
        if (request.Name == null && request.Rol == null && request.Rank == null && request.Stats == null)
        {
            throw new ValidationException("request", "At least one field must be provided for updating");
        }

        var success = await _warframeGateway.PatchWarframeAsync(id, request, cancellationToken);
        if (!success)
        {
            throw new WarframeNotFoundException(id);
        }

        return await GetWarframeByIdAsync(id, cancellationToken);
    }

    public async Task DeleteWarframeAsync(Guid id, CancellationToken cancellationToken)
    {
        if (id == Guid.Empty)
        {
            throw new ArgumentException("Warframe ID cannot be empty", nameof(id));
        }

        var success = await _warframeGateway.DeleteWarframeAsync(id, cancellationToken);
        if (!success)
        {
            throw new WarframeNotFoundException(id);
        }
    }
}