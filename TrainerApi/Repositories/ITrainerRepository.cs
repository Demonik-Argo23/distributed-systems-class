using TrainerApi.Models;

namespace TrainerApi.Repositories;

public interface ITrainerRepository
{
    Task<Trainer?> GetByIdAsync(string id, CancellationToken cancellationToken);
}