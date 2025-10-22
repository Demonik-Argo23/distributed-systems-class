using Google.Protobuf.WellKnownTypes;
using Grpc.Core;
using MongoDB.Driver;
using TrainerApi.Infrastructure.Documents; // Add this using

namespace TrainerApi.Services;

public class TrainerService : TrainerApi.TrainerService.TrainerServiceBase
{
    private readonly IMongoCollection<TrainerDocument> _trainersCollection;

    public TrainerService(IMongoDatabase database)
    {
        _trainersCollection = database.GetCollection<TrainerDocument>("Trainers");
    }

    public override async Task<TrainerResponse> GetTrainerById(TrainerByIdRequest request, ServerCallContext context)
    {
        // For now, returning mock data
        // You can implement MongoDB queries here later
        return new TrainerResponse
        {
            Id = request.Id,
            Name = "Ash Ketchum",
            Age = 10,
            CreatedAt = Timestamp.FromDateTime(DateTime.UtcNow),
            Birthday = Timestamp.FromDateTime(DateTime.UtcNow),
        };
    }
}

// Remove this duplicate class - use the one in Infrastructure/Documents instead