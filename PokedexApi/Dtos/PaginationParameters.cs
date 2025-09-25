namespace PokedexApi.Dtos;

public class PaginationParameters
{
    public string? Name { get; set; }
    public string? Type { get; set; }
    public int PageSize { get; set; } = 10;
    public int PageNumber { get; set; } = 1;
    public string OrderBy { get; set; } = "name";
    public string OrderDirection { get; set; } = "asc";
}