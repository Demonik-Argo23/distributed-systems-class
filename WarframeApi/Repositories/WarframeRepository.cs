using WarframeApi.Infrastructure;
using WarframeApi.Models;
using WarframeApi.Mappers;

namespace WarframeApi.Repositories;

public class WarframeRepository : IWarframeRepository
{
    private readonly RelationalDbContext _context;

    public WarframeRepository(RelationalDbContext context)
    {
        _context = context;
    }

    public async Task<Warframe> CreateAsync(Warframe warframe, CancellationToken cancellationToken)
    {
        // modelo a entidad
        var warframeEntity = warframe.ToEntity();

        _context.Warframes.Add(warframeEntity);
        await _context.SaveChangesAsync(cancellationToken);

        // entidad a modelo
        return warframeEntity.ToModel();
    }
}