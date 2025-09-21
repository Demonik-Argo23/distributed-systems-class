using CodexApi.Models;

namespace CodexApi.Gateways;

public interface IWarframeGateway
{
    Task<Warframe?> GetWarframeByIdAsync(Guid id, CancellationToken cancellationToken);
}