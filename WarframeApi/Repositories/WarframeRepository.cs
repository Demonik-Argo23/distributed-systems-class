using WarframeApi.Infrastructure;
using WarframeApi.Models;
using WarframeApi.Mappers;
using Microsoft.EntityFrameworkCore;

namespace WarframeApi.Repositories;

public class WarframeRepository : IWarframeRepository
{
    private readonly RelationalDbContext _context;

    public WarframeRepository(RelationalDbContext context)
    {
        _context = context;
    }

    public async Task<Warframe> GetWFByNameAsync(string name, CancellationToken cancellationToken)
    {
        var warframe = await _context.Warframes.AsNoTracking().FirstOrDefaultAsync(s => s.Name.Contains(name));
        return warframe.ToModel();
    }

    public async Task<IReadOnlyList<Warframe>> GetWarframesByRankAsync(int rank, CancellationToken cancellationToken)
    {
        var Warframes = await _context.Warframes.AsNoTracking()
        .Where(s => s.Rank.Equals(rank)).ToListAsync(cancellationToken);
        return Warframes.ToModel();
    }

    public async Task<IReadOnlyList<Warframe>> GetWarframesByNameAsync(string name, CancellationToken cancellationToken)
    {
        var Warframes = await _context.Warframes.AsNoTracking()
        .Where(s => s.Name.Contains(name)).ToListAsync(cancellationToken);
        return Warframes.ToModel();
    }

    public async Task UpdateWarframeAsync(Warframe warframe, CancellationToken cancellationToken)
    {
        _context.Warframes.Update(warframe.ToEntity());
        await _context.SaveChangesAsync(cancellationToken);
    }

    public async Task DeleteWarframeAsync(Warframe Warframe, CancellationToken cancellationToken)
    {
        _context.Warframes.Remove(Warframe.ToEntity());
        await _context.SaveChangesAsync(cancellationToken);
    }

    public async Task<Warframe> GetWarframesByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        var warframeEntity = await _context.Warframes.AsNoTracking().FirstOrDefaultAsync(w => w.Id == id, cancellationToken);
        return warframeEntity?.ToModel();
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