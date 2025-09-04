using System.ServiceModel;
using WarframeApi.Dtos;

namespace WarframeApi.Services;

[ServiceContract(Name = "WarframeService", Namespace = "http://Warframe-api/Warframe-service")]
public interface IWarframeService
{
    [OperationContract]
    Task<WarframeResponseDto> CreateWarframe(CreateWarframeDto Warframe, CancellationToken cancellationToken);


}