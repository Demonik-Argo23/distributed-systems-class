using CodexApi.Models;
using CodexApi.Gateways;

namespace CodexApi.Services;

public class WarframeService : IWarframeService
{
    private readonly IWarframeGateway _warframeGateway;

    public WarframeService(IWarframeGateway warframeGateway)
    {
        _warframeGateway = warframeGateway;
    }

    public async Task<Warframe?> GetWarframeByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        return await _warframeGateway.GetWarframeByIdAsync(id, cancellationToken);
    }
}