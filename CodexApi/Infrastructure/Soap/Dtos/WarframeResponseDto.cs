using System.Runtime.Serialization;

namespace CodexApi.Infrastructure.Soap.Dtos;

[DataContract(Name = "WarframeResponse", Namespace = "http://Warframe-api/Warframe-service")]
public class WarframeResponseDto
{
    [DataMember(Order = 1)]
    public Guid Id { get; set; }
    [DataMember(Order = 2)]
    public string Name { get; set; } = string.Empty;
    [DataMember(Order = 3)]
    public string Rol { get; set; } = string.Empty;
    [DataMember(Order = 4)]
    public int Rank { get; set; }
    [DataMember(Order = 5)]
    public int Hp { get; set; }
    [DataMember(Order = 6)]
    public int Shield { get; set; }
    [DataMember(Order = 7)]
    public int Armor { get; set; }
    [DataMember(Order = 8)]
    public int Energy { get; set; }
}