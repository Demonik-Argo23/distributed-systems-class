using System.Runtime.Serialization;

namespace CodexApi.Infrastructure.Soap.Dtos;

[DataContract(Name = "StatsDto", Namespace = "http://Warframe-api/Warframe-service")]
public class StatsDto
{
    public StatsDto()
    {
    }

    [DataMember(Order = 1)]
    public int Hp { get; set; }
    
    [DataMember(Order = 2)]
    public int Shield { get; set; }
    
    [DataMember(Order = 3)]
    public int Armor { get; set; }
    
    [DataMember(Order = 4)]
    public int Energy { get; set; }
}