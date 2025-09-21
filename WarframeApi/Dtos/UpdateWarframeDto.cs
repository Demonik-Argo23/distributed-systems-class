using System.Runtime.Serialization;

namespace WarframeApi.Dtos;

[DataContract(Name = "UpdateWarframeDto", Namespace = "http://Warframe-api/Warframe-service")]
public class UpdateWarframeDto
{
    [DataMember(Name = "Id", Order = 1)]
    public Guid Id { get; set; }

    [DataMember(Name = "Name", Order = 2)]
    public string Name { get; set; }

    [DataMember(Name = "Rank", Order = 3)]
    public int Rank { get; set; }

    [DataMember(Name = "Role", Order = 4)]
    public string Role { get; set; }

    [DataMember(Name = "Stats", Order = 5)]
    public StatsDto Stats { get; set; }
}

