using System.Runtime.Serialization;

namespace CodexApi.Infrastructure.Soap.Dtos;

[DataContract(Name = "CreateWarframeDto", Namespace = "http://Warframe-api/Warframe-service")]
public class CreateWarframeDto
{
    public CreateWarframeDto()
    {
        Name = string.Empty;
        Rol = string.Empty;
        Stats = new StatsDto();
    }

    [DataMember(Order = 1, IsRequired = true)]
    public string Name { get; set; }
    
    [DataMember(Order = 2, IsRequired = true)]
    public string Rol { get; set; }
    
    [DataMember(Order = 3, IsRequired = true)]
    public int Rank { get; set; }
    
    [DataMember(Order = 4, IsRequired = true)]
    public StatsDto Stats { get; set; }
}