using System.ServiceModel;
using WarframeApi.Dtos;

namespace WarframeApi.Services;

[ServiceContract(Name = "WarframeService", Namespace = "http://Warframe-api/Warframe-service")]
public interface IWarframeService
{
    [OperationContract]
    Task<WarframeResponseDto> CreateWarframe(CreateWarframeDto Warframe, CancellationToken cancellationToken);

    [OperationContract]
    Task<WarframeResponseDto> GetWarframeById(Guid id, CancellationToken cancellationToken);

    [OperationContract]
    Task<DeleteWarframeResponseDto> DeleteWarframe(Guid id, CancellationToken cancellationToken);
}