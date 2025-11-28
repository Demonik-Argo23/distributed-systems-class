using Grpc.Core;
using TennoApi.Repositories;
using TennoApi.Mappers;
using TennoApi.Infrastructure.Documents;
using TennoApi;
using TennoStats = TennoApi.Models.TennoStats;

namespace TennoApi.Services;

public class TennoService : TennoApi.TennoService.TennoServiceBase
{
    private readonly ITennoRepository _tennoRepository;
    private readonly ILogger<TennoService> _logger;

    public TennoService(ITennoRepository tennoRepository, ILogger<TennoService> logger)
    {
        _tennoRepository = tennoRepository;
        _logger = logger;
    }

    public override async Task<TennoResponse> GetTennoById(TennoByIdRequest request, ServerCallContext context)
    {
        _logger.LogInformation("Getting Tenno by ID: {TennoId}", request.Id);

        var tenno = await _tennoRepository.GetByIdAsync(request.Id, context.CancellationToken);
        if (tenno is null)
        {
            throw new RpcException(new Status(StatusCode.NotFound, $"Tenno with ID {request.Id} not found."));
        }

        return tenno.ToResponse();
    }

    public override async Task<CreateTennoResponse> CreateTennos(IAsyncStreamReader<CreateTennoRequest> requestStream, ServerCallContext context)
    {
        _logger.LogInformation("Starting batch Tenno creation");

        var createdTennos = new List<TennoResponse>();
        var tennosToCreate = new List<Infrastructure.Documents.TennoDocument>();

        await foreach (var request in requestStream.ReadAllAsync())
        {
            var tennoDocument = request.ToDocument();
            tennosToCreate.Add(tennoDocument);
        }

        if (tennosToCreate.Any())
        {
            var createdDocuments = await _tennoRepository.CreateManyAsync(tennosToCreate, context.CancellationToken);
            createdTennos.AddRange(createdDocuments.Select(d => d.ToResponse()));
        }

        _logger.LogInformation("Created {Count} Tennos", createdTennos.Count);

        return new CreateTennoResponse
        {
            Tennos = { createdTennos },
            TotalCreated = createdTennos.Count
        };
    }

    public override async Task GetAllTennosByName(TennosByNameRequest request, IServerStreamWriter<TennoResponse> responseStream, ServerCallContext context)
    {
        _logger.LogInformation("Getting Tennos by name: {Name}", request.Name);

        var tennos = await _tennoRepository.GetByNameAsync(request.Name, context.CancellationToken);

        foreach (var tenno in tennos)
        {
            await responseStream.WriteAsync(tenno.ToResponse());
        }

        _logger.LogInformation("Streamed {Count} Tennos for name: {Name}", tennos.Count(), request.Name);
    }

    public override async Task GetTennosByMastery(TennosByMasteryRequest request, IServerStreamWriter<TennoResponse> responseStream, ServerCallContext context)
    {
        _logger.LogInformation("Getting Tennos by mastery rank: {MasteryRank}", request.MasteryRank);

        var tennos = await _tennoRepository.GetByMasteryRankAsync(request.MasteryRank, context.CancellationToken);

        foreach (var tenno in tennos)
        {
            await responseStream.WriteAsync(tenno.ToResponse());
        }

        _logger.LogInformation("Streamed {Count} Tennos for mastery rank: {MasteryRank}", tennos.Count(), request.MasteryRank);
    }

    public override async Task<TennoResponse> UpdateTenno(UpdateTennoRequest request, ServerCallContext context)
    {
        _logger.LogInformation("Updating Tenno with ID: {TennoId}", request.Id);

        if (!IdFormatIsValid(request.Id))
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Invalid ID format."));

        if (string.IsNullOrEmpty(request.Name) || request.Name.Length < 3)
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Tenno name must be at least 3 characters long."));

        if (request.MasteryRank < 0 || request.MasteryRank > 30)
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Mastery rank must be between 0 and 30."));

        var tenno = await _tennoRepository.GetByIdAsync(request.Id, context.CancellationToken);
        if (tenno is null)
        {
            throw new RpcException(new Status(StatusCode.NotFound, $"Tenno with ID {request.Id} not found."));
        }

        var tennos = await _tennoRepository.GetByNameAsync(request.Name, context.CancellationToken);
        var tennoAlreadyExists = tennos.Any(t => t.Id != request.Id);
        if (tennoAlreadyExists)
            throw new RpcException(new Status(StatusCode.AlreadyExists, $"Tenno with name {request.Name} already exists."));

        tenno.Name = request.Name;
        tenno.Clan = request.Clan;
        tenno.MasteryRank = request.MasteryRank;
        tenno.FocusSchool = request.FocusSchool;
        tenno.Stats = request.Stats.ToDocument();

        await _tennoRepository.UpdateAsync(tenno, context.CancellationToken);

        _logger.LogInformation("Successfully updated Tenno with ID: {TennoId}", request.Id);

        return tenno.ToResponse();
    }

    public override async Task<DeleteTennoResponse> DeleteTenno(TennoByIdRequest request, ServerCallContext context)
    {
        _logger.LogInformation("Deleting Tenno with ID: {TennoId}", request.Id);

        if (!IdFormatIsValid(request.Id))
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Invalid ID format."));

        var tenno = await _tennoRepository.GetByIdAsync(request.Id, context.CancellationToken);
        if (tenno is null)
        {
            throw new RpcException(new Status(StatusCode.NotFound, $"Tenno with ID {request.Id} not found."));
        }

        await _tennoRepository.DeleteAsync(request.Id, context.CancellationToken);

        _logger.LogInformation("Successfully deleted Tenno with ID: {TennoId}", request.Id);

        return new DeleteTennoResponse
        {
            Success = true,
            Message = $"Tenno with ID {request.Id} deleted successfully."
        };
    }

    private static bool IdFormatIsValid(string id)
    {
        return !string.IsNullOrWhiteSpace(id) && id.Length > 20;
    }
}