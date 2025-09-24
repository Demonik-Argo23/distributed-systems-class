using CodexApi.Models;

namespace CodexApi.Services;

public interface IWarframeService
{
    Task<Warframe?> GetWarframeByIdAsync(Guid id, CancellationToken cancellationToken);
}