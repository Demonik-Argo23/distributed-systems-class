using TennoApi.Infrastructure.Documents;

namespace TennoApi.Repositories;

public interface ITennoRepository
{
    Task<TennoDocument?> GetByIdAsync(string id, CancellationToken cancellationToken);
    Task<IEnumerable<TennoDocument>> GetByNameAsync(string name, CancellationToken cancellationToken);
    Task<IEnumerable<TennoDocument>> GetByMasteryRankAsync(int masteryRank, CancellationToken cancellationToken);
    Task<TennoDocument> CreateAsync(TennoDocument tenno, CancellationToken cancellationToken);
    Task<IEnumerable<TennoDocument>> CreateManyAsync(IEnumerable<TennoDocument> tennos, CancellationToken cancellationToken);
    Task UpdateAsync(TennoDocument tenno, CancellationToken cancellationToken);
    Task DeleteAsync(string id, CancellationToken cancellationToken);
}