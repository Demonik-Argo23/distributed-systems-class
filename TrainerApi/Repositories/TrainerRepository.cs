using Microsoft.Extensions.Options;
using MongoDB.Driver;
using TrainerApi.Infrastructure;
using TrainerApi.Infrastructure.Documents;
using TrainerApi.Models;
using TrainerApi.Mappers;

namespace TrainerApi.Repositories;

public class TrainerRepository : ITrainerRepository
{
    private readonly IMongoCollection<TrainerDocument> _trainerCollection;

    public TrainerRepository(IMongoDatabase database, IOptions<MongoDBSettings> settings)
    {
        _trainerCollection = database.GetCollection<TrainerDocument>(settings.Value.TrainerCollectionName);

    }
    

    public async Task<Trainer?> GetByIdAsync(string id, CancellationToken cancellationToken)
    {
        var trainer = await _trainerCollection.Find(t => t.Id == id).FirstOrDefaultAsync();
        return trainer?.ToDomain();
    }
}