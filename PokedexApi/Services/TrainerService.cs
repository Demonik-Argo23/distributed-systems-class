using PokedexApi.Gateways;
using PokedexApi.Models;

namespace PokedexApi.Services;

public class TrainerService : ITrainerService
{
    private readonly ITrainerGateway _trainerGateway;

    public TrainerService(ITrainerGateway trainerGateway)
    {
        _trainerGateway = trainerGateway;
    }

    public async Task<Trainer> GetByIdAsync(string id, CancellationToken cancellationToken)
    {
        return await _trainerGateway.GetTrainerById(id, cancellationToken);
    }
}