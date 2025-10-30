using Google.Protobuf.WellKnownTypes;
using Grpc.Core;
using TrainerApi.Infrastructure.Documents;
using TrainerApi.Mappers;
using TrainerApi.Models;
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

        var trainer = await GetTrainerAsync(request.Id, context.CancellationToken);
        return trainer.ToResponse();
    }

    public override async Task<Empty> UpdateTrainer(UpdateTrainerRequest request, ServerCallContext context)
    {
        if (!IdFormatIsValid(request.Id))
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Invalid ID format."));
        if (request.Age < 18)
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Trainer must be over 18 years old."));
        if (string.IsNullOrEmpty(request.Name) || request.Name.Length < 3)
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Trainer name must be at least 3 characters long."));

        var trainer = await GetTrainerAsync(request.Id, context.CancellationToken);
        var trainers = await _trainerRepository.GetByNameAsync(request.Name, context.CancellationToken);
        var trainerAlreadyExists = trainers.Any(t => t.Id != request.Id); // Fixed logic
        if(trainerAlreadyExists)
            throw new RpcException(new Status(StatusCode.AlreadyExists, $"Trainer with name {request.Name} already exists."));

        // Update trainer properties BEFORE calling UpdateAsync
        trainer.Name = request.Name;
        trainer.Age = request.Age;
        trainer.Birthdate = request.Birthdate.ToDateTime();
        trainer.Medals = request.Medals.Select(m => m.ToModel()).ToList();
        
        await _trainerRepository.UpdateAsync(trainer, context.CancellationToken);
        return new Empty();
    }

    public override async Task<CreateTrainerResponse> CreateTrainers(IAsyncStreamReader<CreateTrainerRequest> requestStream, ServerCallContext context)
    {
        var createdTrainers = new List<TrainerResponse>();


        while (await requestStream.MoveNext(cancellationToken: context.CancellationToken))
        {
            var request = requestStream.Current;
            var trainer = request.ToModel();
            var trainersExists = await _trainerRepository.GetByNameAsync(trainer.Name, context.CancellationToken);

            if (trainersExists.Any())
            {
                continue;
            }

            var createdTrainer = await _trainerRepository.CreateAsync(trainer, context.CancellationToken);
            createdTrainers.Add(createdTrainer.ToResponse());
        }

        return new CreateTrainerResponse
        {
            SuccessCount = createdTrainers.Count,
            Trainers = { createdTrainers },
        };
    }

    public override async Task<Empty> DeleteTrainer(TrainerByIdRequest request, ServerCallContext context)
    {
        if (!IdFormatIsValid(request.Id))
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Invalid ID format."));

        var trainer = await GetTrainerAsync(request.Id, context.CancellationToken);
        await _trainerRepository.DeleteAsync(request.Id, context.CancellationToken);
        return new Empty();
    }
    public override async Task GetAllTrainersByName(TrainersByNameRequest request, IServerStreamWriter<TrainerResponse> responseStream, ServerCallContext context)
    {
        var trainers = await _trainerRepository.GetByNameAsync(request.Name, context.CancellationToken);

        foreach (var trainer in trainers)
        {
            if (context.CancellationToken.IsCancellationRequested)
                break;
            await responseStream.WriteAsync(trainer.ToResponse());
            await Task.Delay(TimeSpan.FromSeconds(5), context.CancellationToken);
        }
    }

    private async Task<Trainer> GetTrainerAsync(string id, CancellationToken cancellationToken)
    {
        var trainer = await _trainerRepository.GetByIdAsync(id, cancellationToken);
        return trainer ?? throw new RpcException(new Status(StatusCode.NotFound, $"Trainer with ID {id} not found."));
    }

    private static bool IdFormatIsValid(string id)
    {
        return !string.IsNullOrWhiteSpace(id) && id.Length > 20;
    }
}