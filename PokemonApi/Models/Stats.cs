
namespace PokemonApi.Models;

public class Stats
{
    public int Attack { get; set; }
    public int Defense { get; set; }
    public int Speed { get; set; }
    public int Hp { get; set; }

    // Removed invalid implicit operator
}