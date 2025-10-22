using System.Runtime.Serialization;

namespace CodexApi.Infrastructure.Soap.Dtos;


[DataContract(Name = "PagedResult", Namespace = "http://Warframe-api/Warframe-service")]
public class PagedResultDto<T>
{

    [DataMember(Order = 1)]
    public IList<T> Items { get; set; } = new List<T>();

    [DataMember(Order = 2)]
    public int PageNumber { get; set; }

    [DataMember(Order = 3)]
    public int PageSize { get; set; }

    [DataMember(Order = 4)]
    public int TotalCount { get; set; }

    [DataMember(Order = 5)]
    public int TotalPages { get; set; }

    [DataMember(Order = 6)]
    public bool HasNextPage { get; set; }

    [DataMember(Order = 7)]
    public bool HasPreviousPage { get; set; }
}