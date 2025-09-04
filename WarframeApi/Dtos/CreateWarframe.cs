using System.Runtime.Serialization;

namespace WarframeApi.Dtos;

[DataContract(Name = "CreateWarframeDto", Namespace = "http://Warframe-api/Warframe-service")]
public class CreateWarframeDto
{
    [DataMember(Order = 1, IsRequired = true)]
    public string Name { get; set; } = string.Empty;
    
    [DataMember(Order = 2, IsRequired = true)]
    public string Rol { get; set; } = string.Empty;
    
    [DataMember(Order = 3, IsRequired = true)]
    public int Rank { get; set; }
    
    [DataMember(Order = 4, IsRequired = true)]
    public required StatsDto Stats { get; set; }
}