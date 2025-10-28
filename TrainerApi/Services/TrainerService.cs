using Google.Protobuf.WellKnownTypes;
using Grpc.Core;
using TrainerApi.Infrastructure.Documents;
using TrainerApi.Mappers;
using TrainerApi.Repositories;

namespace TrainerApi.Services;

public class TrainerService : TrainerApi.TrainerService.TrainerServiceBase
{
    private readonly ITrainerRepository _trainerRepository;
    public TrainerService(ITrainerRepository trainerRepository)
    {
        _trainerRepository = trainerRepository;

    }
    public override async Task<TrainerResponse> GetTrainerById(TrainerByIdRequest request, ServerCallContext context)
    {
        var trainer = await _trainerRepository.GetByIdAsync(request.Id, context.CancellationToken);
        if (trainer is null)
            throw new RpcException(new Status(StatusCode.NotFound, $"Trainer with ID {request.Id} not found."));

        return trainer.ToResponse();
    }
}