using System.Runtime.Serialization;

namespace CodexApi.Infrastructure.Soap.Dtos;

[DataContract(Name = "UpdateWarframeResponse", Namespace = "http://Warframe-api/Warframe-service")]
public class UpdateWarframeResponseDto
{
    [DataMember(Order = 1)]
    public bool Success { get; set; }
    
    [DataMember(Order = 2)]
    public string Message { get; set; } = string.Empty;
}