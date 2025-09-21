using System.ServiceModel;
using CodexApi.Models;
using CodexApi.Infrastructure.Soap.Contracts;
using CodexApi.Infrastructure.Soap.Dtos;

namespace CodexApi.Gateways;

public class WarframeGateway : IWarframeGateway
{
    private readonly string _soapServiceUrl;

    public WarframeGateway(IConfiguration configuration)
    {
        _soapServiceUrl = configuration["WarframeApi:BaseUrl"] ?? "http://localhost:8080";
    }

    public async Task<Warframe?> GetWarframeByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        try
        {
            var binding = new BasicHttpBinding();
            var endpoint = new EndpointAddress($"{_soapServiceUrl}/WarframeService.svc");
            
            using var channelFactory = new ChannelFactory<IWarframeServiceContract>(binding, endpoint);
            var client = channelFactory.CreateChannel();

            var warframeDto = await client.GetWarframeById(id, cancellationToken);
            
            if (warframeDto == null)
                return null;

            return ToModel(warframeDto);
        }
        catch (Exception)
        {
            return null;
        }
    }

    private static Warframe ToModel(WarframeResponseDto dto)
    {
        return new Warframe
        {
            Id = dto.Id,
            Name = dto.Name,
            Rol = dto.Rol,
            Rank = dto.Rank,
            Stats = new Stats
            {
                Hp = dto.Hp,
                Shield = dto.Shield,
                Armor = dto.Armor,
                Energy = dto.Energy
            }
        };
    }
}