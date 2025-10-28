using MongoDB.Driver;
using TennoApi.Infrastructure;
using TennoApi.Infrastructure.Documents;
using Microsoft.Extensions.Options;

namespace TennoApi.Repositories;

public class TennoRepository : ITennoRepository
{
    private readonly IMongoCollection<TennoDocument> _tennos;

    public TennoRepository(IMongoDatabase database, IOptions<MongoDBSettings> settings)
    {
        _tennos = database.GetCollection<TennoDocument>(settings.Value.TennoCollectionName);
    }

    public async Task<TennoDocument?> GetByIdAsync(string id, CancellationToken cancellationToken)
    {
        return await _tennos.Find(t => t.Id == id).FirstOrDefaultAsync(cancellationToken);
    }

    public async Task<IEnumerable<TennoDocument>> GetByNameAsync(string name, CancellationToken cancellationToken)
    {
        var filter = Builders<TennoDocument>.Filter.Regex(t => t.Name, new MongoDB.Bson.BsonRegularExpression(name, "i"));
        return await _tennos.Find(filter).ToListAsync(cancellationToken);
    }

    public async Task<IEnumerable<TennoDocument>> GetByMasteryRankAsync(int masteryRank, CancellationToken cancellationToken)
    {
        return await _tennos.Find(t => t.MasteryRank == masteryRank).ToListAsync(cancellationToken);
    }

    public async Task<TennoDocument> CreateAsync(TennoDocument tenno, CancellationToken cancellationToken)
    {
        await _tennos.InsertOneAsync(tenno, cancellationToken: cancellationToken);
        return tenno;
    }

    public async Task<IEnumerable<TennoDocument>> CreateManyAsync(IEnumerable<TennoDocument> tennos, CancellationToken cancellationToken)
    {
        var tennoList = tennos.ToList();
        await _tennos.InsertManyAsync(tennoList, cancellationToken: cancellationToken);
        return tennoList;
    }
}