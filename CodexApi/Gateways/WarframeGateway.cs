using System.ServiceModel;
using CodexApi.Models;
using CodexApi.Infrastructure.Soap.Contracts;
using CodexApi.Infrastructure.Soap.Dtos;
using CodexApi.Dtos;
using CodexApi.Exceptions;

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
        catch (System.ServiceModel.FaultException)
        {
            return null;
        }
        catch (Exception)
        {
            return null;
        }
    }

    public async Task<IEnumerable<Warframe>> GetWarframesAsync(int page, int pageSize, string name, CancellationToken cancellationToken)
    {
        try
        {
            using var channelFactory = new ChannelFactory<IWarframeServiceContract>(_binding, _endpoint);
            var client = channelFactory.CreateChannel();

            var pagedResult = await client.GetWarframes(page, pageSize, name, cancellationToken);
            return pagedResult.Items.Select(ToModel);
        }
        catch (Exception)
        {
            return Enumerable.Empty<Warframe>();
        }
    }

    public async Task<Warframe> CreateWarframeAsync(CreateWarframeRequest request, CancellationToken cancellationToken)
    {
        try
        {
            using var channelFactory = new ChannelFactory<IWarframeServiceContract>(_binding, _endpoint);
            var client = channelFactory.CreateChannel();

            var createDto = new CreateWarframeDto
            {
                Name = request.Name,
                Rol = request.Rol,
                Rank = request.Rank,
                Stats = new Infrastructure.Soap.Dtos.StatsDto
                {
                    Hp = request.Stats.Hp,
                    Shield = request.Stats.Shield,
                    Armor = request.Stats.Armor,
                    Energy = request.Stats.Energy
                }
            };

            Console.WriteLine($"Sending CreateWarframeDto: Name={createDto.Name}, Rol={createDto.Rol}, Rank={createDto.Rank}");
            Console.WriteLine($"Stats: Hp={createDto.Stats?.Hp}, Shield={createDto.Stats?.Shield}, Armor={createDto.Stats?.Armor}, Energy={createDto.Stats?.Energy}");

            var warframeDto = await client.CreateWarframe(createDto, cancellationToken);
            return ToModel(warframeDto);
        }
        catch (System.ServiceModel.FaultException ex) when (ex.Message.Contains("already exists"))
        {
            // Convert SOAP FaultException to our custom exception for name conflicts
            throw new WarframeAlreadyExistsException(ex.Message);
        }
    }

    public async Task<bool> UpdateWarframeAsync(Guid id, UpdateWarframeRequest request, CancellationToken cancellationToken)
    {
        try
        {
            using var channelFactory = new ChannelFactory<IWarframeServiceContract>(_binding, _endpoint);
            var client = channelFactory.CreateChannel();

            var updateDto = new CreateWarframeDto
            {
                Name = request.Name,
                Rol = request.Rol,
                Rank = request.Rank,
                Stats = new Infrastructure.Soap.Dtos.StatsDto
                {
                    Hp = request.Stats.Hp,
                    Shield = request.Stats.Shield,
                    Armor = request.Stats.Armor,
                    Energy = request.Stats.Energy
                }
            };

            var response = await client.UpdateWarframe(id, updateDto, cancellationToken);
            return response != null;
        }
        catch (System.ServiceModel.FaultException ex) when (ex.Message.Contains("not found") || ex.Message.Contains("ID found"))
        {
            throw new WarframeNotFoundException(id);
        }
        catch (System.ServiceModel.FaultException ex) when (ex.Message.Contains("already exists"))
        {
            throw new WarframeAlreadyExistsException(ex.Message);
        }
    }

    public async Task<bool> PatchWarframeAsync(Guid id, PatchWarframeRequest request, CancellationToken cancellationToken)
    {
        try
        {
            using var channelFactory = new ChannelFactory<IWarframeServiceContract>(_binding, _endpoint);
            var client = channelFactory.CreateChannel();

            var patchDto = new CreateWarframeDto
            {
                Name = request.Name ?? string.Empty,
                Rol = request.Rol ?? string.Empty,
                Rank = request.Rank ?? 0,
                Stats = request.Stats != null ? new Infrastructure.Soap.Dtos.StatsDto
                {
                    Hp = request.Stats.Hp ?? 0,
                    Shield = request.Stats.Shield ?? 0,
                    Armor = request.Stats.Armor ?? 0,
                    Energy = request.Stats.Energy ?? 0
                } : new Infrastructure.Soap.Dtos.StatsDto()
            };

            Console.WriteLine($"Sending PatchWarframeDto: Name='{patchDto.Name}', Rol='{patchDto.Rol}', Rank={patchDto.Rank}");
            Console.WriteLine($"Stats: Hp={patchDto.Stats?.Hp}, Shield={patchDto.Stats?.Shield}, Armor={patchDto.Stats?.Armor}, Energy={patchDto.Stats?.Energy}");

            var response = await client.PatchWarframe(id, patchDto, cancellationToken);
            return response != null;
        }
        catch (System.ServiceModel.FaultException ex) when (ex.Message.Contains("not found") || ex.Message.Contains("ID found"))
        {
            throw new WarframeNotFoundException(id);
        }
        catch (System.ServiceModel.FaultException ex) when (ex.Message.Contains("already exists"))
        {
            throw new WarframeAlreadyExistsException(ex.Message);
        }
    }

    public async Task<bool> DeleteWarframeAsync(Guid id, CancellationToken cancellationToken)
    {
        try
        {
            Console.WriteLine($"Gateway: Attempting to delete Warframe with ID {id}");
            using var channelFactory = new ChannelFactory<IWarframeServiceContract>(_binding, _endpoint);
            var client = channelFactory.CreateChannel();

            var response = await client.DeleteWarframe(id, cancellationToken);
            Console.WriteLine($"Gateway: DELETE response - Success: {response.Success}, Message: {response.Message}");
            return response.Success;
        }
        catch (System.ServiceModel.FaultException ex)
        {
            Console.WriteLine($"Gateway: FaultException caught - {ex.Message}");
            throw new WarframeNotFoundException(id);
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Gateway: Unexpected exception - {ex.GetType().Name}: {ex.Message}");
            throw;
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