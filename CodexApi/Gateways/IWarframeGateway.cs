using CodexApi.Models;
using CodexApi.Dtos;

namespace CodexApi.Gateways;

public interface IWarframeGateway
{
    Task<Warframe?> GetWarframeByIdAsync(Guid id, CancellationToken cancellationToken);
    Task<IEnumerable<Warframe>> GetWarframesAsync(int page, int pageSize, string name, CancellationToken cancellationToken);
    Task<Warframe> CreateWarframeAsync(CreateWarframeRequest request, CancellationToken cancellationToken);
    Task<bool> UpdateWarframeAsync(Guid id, UpdateWarframeRequest request, CancellationToken cancellationToken);
    Task<bool> PatchWarframeAsync(Guid id, PatchWarframeRequest request, CancellationToken cancellationToken);
    Task<bool> DeleteWarframeAsync(Guid id, CancellationToken cancellationToken);
}