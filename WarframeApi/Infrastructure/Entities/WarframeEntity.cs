namespace WarframeApi.Infrastructure.Entities;

public class WarframeEntity
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    public string Rol { get; set; }
    public int Rank { get; set; }
    public int Hp { get; set; }
    public int Shield { get; set; }
    public int Armor { get; set; }
    public int Energy { get; set; }

}