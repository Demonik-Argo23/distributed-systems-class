using System.ServiceModel;
using CodexApi.Models;
using CodexApi.Infrastructure.Soap.Contracts;
using CodexApi.Infrastructure.Soap.Dtos;

namespace CodexApi.Gateways;

public class WarframeGateway : IWarframeGateway
{
    private readonly string _soapServiceUrl;
    private readonly BasicHttpBinding _binding;
    private readonly EndpointAddress _endpoint;

    public WarframeGateway(IConfiguration configuration)
    {
        _soapServiceUrl = configuration["WarframeApi:BaseUrl"] ?? "http://localhost:8080";
        _binding = new BasicHttpBinding();
        _endpoint = new EndpointAddress($"{_soapServiceUrl}/WarframeService.svc");
    }

    public async Task<Warframe?> GetWarframeByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        try
        {
            using var channelFactory = new ChannelFactory<IWarframeServiceContract>(_binding, _endpoint);
            var client = channelFactory.CreateChannel();

            var warframeDto = await client.GetWarframeById(id, cancellationToken);

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