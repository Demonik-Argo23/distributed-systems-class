using System.Runtime.Serialization;

namespace WarframeApi.Dtos;

[DataContract(Name = "DeleteWarframeResponseDto", Namespace = "http://Warframe-api/DeleteWarframeResponseDto")]
public class DeleteWarframeResponseDto
{
    [DataMember(Name = "Success", Order = 1)]
    public bool Success { get; set; }
}