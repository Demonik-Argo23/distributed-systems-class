using System.ServiceModel;
using CodexApi.Infrastructure.Soap.Dtos;

namespace CodexApi.Infrastructure.Soap.Contracts;

[ServiceContract(Name = "WarframeService", Namespace = "http://Warframe-api/Warframe-service")]
public interface IWarframeServiceContract
{
    [OperationContract]
    Task<WarframeResponseDto> GetWarframeById(Guid id, CancellationToken cancellationToken);
}