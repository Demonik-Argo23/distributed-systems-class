namespace CodexApi.Models;

public class Warframe
{
    public Guid Id { get; set; }
    public string Name { get; set; } = null!;
    public string Rol { get; set; } = null!;
    public int Rank { get; set; }
    public Stats Stats { get; set; } = new();
}