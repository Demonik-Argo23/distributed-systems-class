using WarframeApi.Models;

namespace WarframeApi.Repositories;

public interface IWarframeRepository
{
    Task<Warframe> CreateAsync(Warframe Warframe, CancellationToken cancellationToken);

    Task<Warframe> GetWarframesByIdAsync(Guid id, CancellationToken cancellationToken);

    Task<Warframe> GetWFByNameAsync(string name, CancellationToken cancellationToken);

    Task DeleteWarframeAsync(Warframe warframe, CancellationToken cancellationToken);

    Task UpdateWarframeAsync(Warframe warframe, CancellationToken cancellationToken);

    Task<IReadOnlyList<Warframe>> GetWarframesByNameAsync(string name, CancellationToken cancellationToken);

    Task<IReadOnlyList<Warframe>> GetWarframesByRankAsync(int rank, CancellationToken cancellationToken);

}