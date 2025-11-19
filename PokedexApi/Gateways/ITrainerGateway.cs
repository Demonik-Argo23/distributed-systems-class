using PokedexApi.Models;

namespace PokedexApi.Gateways;

public interface ITrainerGateway
{
    Task<Trainer> GetTrainerById(string id, CancellationToken cancellationToken);
    IAsyncEnumerable<Trainer> GetTrainersByName(string name, CancellationToken cancellationToken);
    Task DeleteAsync(string id, CancellationToken cancellationToken);

    Task UpdateAsync(Trainer trainer, CancellationToken cancellationToken);

    Task<(int SuccesCount, IList<Trainer> CreatedTrainers)> CreateTrainersBulk(
        IEnumerable<Trainer> trainers, CancellationToken cancellationToken);
}