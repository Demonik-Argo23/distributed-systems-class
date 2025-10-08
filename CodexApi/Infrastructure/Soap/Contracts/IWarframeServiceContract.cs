using System.ServiceModel;
using CodexApi.Infrastructure.Soap.Dtos;

namespace CodexApi.Infrastructure.Soap.Contracts;

[ServiceContract(Name = "WarframeService", Namespace = "http://Warframe-api/Warframe-service")]
public interface IWarframeServiceContract
{

    [OperationContract]
    Task<WarframeResponseDto> GetWarframeById(Guid id, CancellationToken cancellationToken);

    [OperationContract]
    Task<PagedResultDto<WarframeResponseDto>> GetWarframes(int page, int pageSize, string name, CancellationToken cancellationToken);

    [OperationContract]
    Task<WarframeResponseDto> CreateWarframe(CreateWarframeDto warframeDto, CancellationToken cancellationToken);

    [OperationContract]
    Task<WarframeResponseDto> UpdateWarframe(Guid id, CreateWarframeDto warframeDto, CancellationToken cancellationToken);

    [OperationContract]
    Task<WarframeResponseDto> PatchWarframe(Guid id, CreateWarframeDto warframeDto, CancellationToken cancellationToken);

    [OperationContract]
    Task<DeleteWarframeResponseDto> DeleteWarframe(Guid id, CancellationToken cancellationToken);
}