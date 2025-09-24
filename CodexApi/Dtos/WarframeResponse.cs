namespace CodexApi.Dtos;

public class WarframeResponse
{
    public Guid Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string Rol { get; set; } = string.Empty;
    public int Rank { get; set; }
    public StatsResponse Stats { get; set; } = new();
}