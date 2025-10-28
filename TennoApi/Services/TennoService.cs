using Grpc.Core;
using TennoApi.Repositories;
using TennoApi.Mappers;

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
}