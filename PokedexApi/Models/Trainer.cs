namespace PokedexApi.Models
{
    public class Trainer
    {
        public required string Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public int Age { get; set; }
        public DateTime BirthDate { get; set; }
        public DateTime CreatedAt { get; set; }
        public required IEnumerable<Medal> Medals { get; set; }
    }

    public class Medal
    {
        public required string Region { get; set; }
        public MedalType Type { get; set; }
    }
}

public enum MedalType
{
    Unknown = 0,
    Bronze = 1,
    Silver = 2,
    Gold = 3
}