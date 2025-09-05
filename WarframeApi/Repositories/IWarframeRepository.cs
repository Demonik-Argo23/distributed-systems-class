using WarframeApi.Models;

namespace WarframeApi.Repositories;

public interface IWarframeRepository
{
    Task<Warframe> CreateAsync(Warframe Warframe, CancellationToken cancellationToken);

    Task<Warframe> GetWarframesByIdAsync(Guid id, CancellationToken cancellationToken);

    Task DeleteWarframeAsync(Warframe warframe, CancellationToken cancellationToken);

}