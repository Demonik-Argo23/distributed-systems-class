using CodexApi.Models;
using CodexApi.Dtos;

namespace CodexApi.Services;

public interface IWarframeService
{
    Task<Warframe> GetWarframeByIdAsync(Guid id, CancellationToken cancellationToken);
    Task<IEnumerable<Warframe>> GetWarframesAsync(int page, int pageSize, string name, CancellationToken cancellationToken);
    Task<Warframe> CreateWarframeAsync(CreateWarframeRequest request, CancellationToken cancellationToken);
    Task<Warframe> UpdateWarframeAsync(Guid id, UpdateWarframeRequest request, CancellationToken cancellationToken);
    Task<Warframe> PatchWarframeAsync(Guid id, PatchWarframeRequest request, CancellationToken cancellationToken);
    Task DeleteWarframeAsync(Guid id, CancellationToken cancellationToken);
}