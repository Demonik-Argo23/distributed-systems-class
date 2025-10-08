using System.Runtime.Serialization;

namespace CodexApi.Infrastructure.Soap.Dtos;


[DataContract(Name = "PaginationRequest", Namespace = "http://Warframe-api/Warframe-service")]
public class PaginationRequestDto
{

    [DataMember(Order = 1)]
    public int PageNumber { get; set; } = 1;
    
    [DataMember(Order = 2)]
    public int PageSize { get; set; } = 10;
}