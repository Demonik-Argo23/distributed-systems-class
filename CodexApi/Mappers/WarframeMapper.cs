using CodexApi.Dtos;
using CodexApi.Models;

namespace CodexApi.Mappers;

public static class WarframeMapper
{
    public static WarframeResponse ToResponse(this Warframe warframe)
    {
        return new WarframeResponse
        {
            Id = warframe.Id,
            Name = warframe.Name,
            Rol = warframe.Rol,
            Rank = warframe.Rank,
            Stats = warframe.Stats.ToStatsResponse()
        };
    }

    public static StatsResponse ToStatsResponse(this Stats stats)
    {
        return new StatsResponse
        {
            Hp = stats.Hp,
            Shield = stats.Shield,
            Armor = stats.Armor,
            Energy = stats.Energy
        };
    }
}