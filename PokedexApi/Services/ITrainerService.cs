using PokedexApi.Models;

namespace PokedexApi.Services;

public interface ITrainerService
{
    Task<Trainer> GetByIdAsync(string id, CancellationToken cancellationToken);
}