using System.Runtime.Serialization;

namespace CodexApi.Infrastructure.Soap.Dtos;

[DataContract(Name = "DeleteWarframeResponse", Namespace = "http://Warframe-api/Warframe-service")]
public class DeleteWarframeResponseDto
{
    [DataMember(Order = 1)]
    public bool Success { get; set; }
    
    [DataMember(Order = 2)]
    public string Message { get; set; } = string.Empty;
}