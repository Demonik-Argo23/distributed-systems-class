using PokedexApi.Models;

namespace PokedexApi.Gateways
{
    public interface ITrainerGateway
    {
        Task<Trainer> GetTrainerById(string id, CancellationToken cancellationToken);
        //IAsyncEnumerable<Trainer> GetTrainersByName(string name, CancellationToken cancellationToken);
        //Task DeleteTrainer(string id, CancellationToken cancellationToken);
        //Task UpdateTrainer(Trainer trainer, CancellationToken cancellationToken);
        //Task<(int SuccessCount, IList<Trainer> Trainers)> CreateTrainers(List<Trainer> trainers, CancellationToken cancellationToken);
    }
}