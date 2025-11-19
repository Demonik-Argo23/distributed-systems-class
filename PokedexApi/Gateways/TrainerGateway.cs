using Grpc.Core;
using PokedexApi.Exceptions;
using PokedexApi.Models;
using System.Runtime.CompilerServices;
using Google.Protobuf.Collections;

namespace PokedexApi.Gateways;

public class TrainerGateway : ITrainerGateway
{
    private readonly TrainerService.TrainerServiceClient _client;

    public TrainerGateway(TrainerService.TrainerServiceClient client)
    {
        _client = client;
    }

    public async Task<Trainer> GetTrainerById(string id, CancellationToken cancellationToken)
    {
        try
        {
            var trainer = await _client.GetTrainerByIdAsync(new TrainerByIdRequest { Id = id }, cancellationToken: cancellationToken);
            return ToModel(trainer);
        }

        catch (RpcException ex) when (ex.StatusCode == StatusCode.NotFound)
        {
            throw new TrainerNotFoundException(id);
        }

    }

    public async Task DeleteAsync(string id, CancellationToken cancellationToken)
    {
        try
        {
            await _client.DeleteTrainerAsync(new TrainerByIdRequest { Id = id },
            cancellationToken: cancellationToken);
        }
        catch (RpcException ex) when (ex.StatusCode == StatusCode.NotFound)
        {
            throw new TrainerNotFoundException(id);

        }
        catch (RpcException ex) when (ex.StatusCode == StatusCode.InvalidArgument)
        {
            throw new TrainerInvalidException(id);
        }
    }

    public async Task UpdateAsync(Trainer trainer, CancellationToken cancellationToken)
    {
        try
        {
            var request = new UpdateTrainerRequest
            {
                Id = trainer.Id,
                Name = trainer.Name,
                Age = trainer.Age,
                Birthdate = Google.Protobuf.WellKnownTypes.Timestamp.FromDateTime(trainer.BirthDate),
                Medals = { trainer.Medals.Select(m => new Medal
                {
                    Region = m.Region,
                    Type = (PokedexApi.MedalType)m.Type,
                }) }
            };

            await _client.UpdateTrainerAsync(request, cancellationToken: cancellationToken);
        }
        catch (RpcException ex) when (ex.StatusCode == StatusCode.NotFound)
        {
            throw new TrainerNotFoundException(trainer.Id);
        }
        catch (RpcException ex) when (ex.StatusCode == StatusCode.AlreadyExists)
        {
            throw new TrainerAlreadyExistsException(trainer.Id);
        }
        catch (RpcException ex) when (ex.StatusCode == StatusCode.InvalidArgument)
        {
            throw new TrainerValidationException("Trainer must be over 18 years old.");
        }
    }

    public async IAsyncEnumerable<Trainer> GetTrainersByName(string name, [EnumeratorCancellation] CancellationToken cancellationToken)
    {
        var request = new TrainersByNameRequest { Name = name };
        using var call = _client.GetAllTrainersByName(request, cancellationToken: cancellationToken);
        while (await call.ResponseStream.MoveNext(cancellationToken))
        {
            yield return ToModel(call.ResponseStream.Current);
        }
    }

    public async Task<(int SuccesCount, IList<Trainer> CreatedTrainers)> CreateTrainersBulk(IEnumerable<Trainer> trainers, CancellationToken cancellationToken)
    {
        using var call = _client.CreateTrainers(cancellationToken: cancellationToken);
        foreach (var trainer in trainers)
        {
            var request = new CreateTrainerRequest
            {
                Name = trainer.Name,
                Age = trainer.Age,
                Birthdate = Google.Protobuf.WellKnownTypes.Timestamp.FromDateTime(trainer.BirthDate),
                Medals = { trainer.Medals.Select(m => new Medal
                {
                    Region = m.Region,
                    Type = (PokedexApi.MedalType)m.Type,
                }) }
            };

            await call.RequestStream.WriteAsync(request, cancellationToken);
        }

        await call.RequestStream.CompleteAsync();
        var response = await call.ResponseAsync;

        return (response.SuccessCount, ToModel(response.Trainers));
    }

    private static Trainer ToModel(TrainerResponse trainerResponse)
    {
        return new Trainer
        {
            Id = trainerResponse.Id,
            Name = trainerResponse.Name,
            Age = trainerResponse.Age,
            BirthDate = trainerResponse.Birthdate.ToDateTime(),
            CreatedAt = trainerResponse.CreatedAt.ToDateTime(),
            Medals = trainerResponse.Medals.Select(ToModel).ToList()
        };
    }

    private static Models.Medal ToModel(Medal medal)
    {
        return new Models.Medal
        {
            Region = medal.Region,
            Type = (MedalType)(int)medal.Type
        };
    }

    private static IList<Trainer> ToModel(RepeatedField<TrainerResponse> trainerResponse)
    {
        return trainerResponse.Select(ToModel).ToList();
    }
}