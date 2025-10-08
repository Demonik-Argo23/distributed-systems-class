using System.ServiceModel;
using WarframeApi.Dtos;

namespace WarframeApi.Services;

[ServiceContract(Name = "WarframeService", Namespace = "http://Warframe-api/Warframe-service")]
public interface IWarframeService
{
    [OperationContract]
    Task<WarframeResponseDto> CreateWarframe(CreateWarframeDto warframeDto, CancellationToken cancellationToken);

    [OperationContract]
    Task<WarframeResponseDto> GetWarframeById(Guid id, CancellationToken cancellationToken);

    [OperationContract]
    Task<DeleteWarframeResponseDto> DeleteWarframe(Guid id, CancellationToken cancellationToken);

    [OperationContract]
    Task<WarframeResponseDto> UpdateWarframe(Guid id, CreateWarframeDto warframeDto, CancellationToken cancellationToken);

    [OperationContract]
    Task<WarframeResponseDto> PatchWarframe(Guid id, CreateWarframeDto warframeDto, CancellationToken cancellationToken);

    [OperationContract]
    Task<List<WarframeResponseDto>> GetWarframeByName(string name, CancellationToken cancellationToken);

    [OperationContract]
    Task<List<WarframeResponseDto>> GetWarframeByRank(int rank, CancellationToken cancellationToken);

    [OperationContract]
    Task<PagedResultDto<WarframeResponseDto>> GetWarframeByNamePaged(string name, PaginationRequestDto pagination, CancellationToken cancellationToken);

    [OperationContract]
    Task<PagedResultDto<WarframeResponseDto>> GetWarframes(int page, int pageSize, string name, CancellationToken cancellationToken);

}