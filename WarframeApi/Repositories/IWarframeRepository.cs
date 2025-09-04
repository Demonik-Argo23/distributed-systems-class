using WarframeApi.Models;

namespace WarframeApi.Repositories;

public interface IWarframeRepository
{
    Task<Warframe> CreateAsync(Warframe Warframen, CancellationToken cancellationToken);

}