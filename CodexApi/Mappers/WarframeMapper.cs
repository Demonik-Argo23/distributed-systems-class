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

    public static Warframe ToModel(this CreateWarframeRequest request)
    {
        return new Warframe
        {
            Id = Guid.NewGuid(),
            Name = request.Name,
            Rol = request.Rol,
            Rank = request.Rank,
            Stats = new Stats
            {
                Hp = request.Stats.Hp,
                Shield = request.Stats.Shield,
                Armor = request.Stats.Armor,
                Energy = request.Stats.Energy
            }
        };
    }

    public static void UpdateFrom(this Warframe warframe, UpdateWarframeRequest request)
    {
        warframe.Name = request.Name;
        warframe.Rol = request.Rol;
        warframe.Rank = request.Rank;
        warframe.Stats.Hp = request.Stats.Hp;
        warframe.Stats.Shield = request.Stats.Shield;
        warframe.Stats.Armor = request.Stats.Armor;
        warframe.Stats.Energy = request.Stats.Energy;
    }

    public static void PatchFrom(this Warframe warframe, PatchWarframeRequest request)
    {
        if (!string.IsNullOrEmpty(request.Name))
            warframe.Name = request.Name;
            
        if (!string.IsNullOrEmpty(request.Rol))
            warframe.Rol = request.Rol;
            
        if (request.Rank.HasValue)
            warframe.Rank = request.Rank.Value;

        if (request.Stats != null)
        {
            if (request.Stats.Hp.HasValue)
                warframe.Stats.Hp = request.Stats.Hp.Value;
                
            if (request.Stats.Shield.HasValue)
                warframe.Stats.Shield = request.Stats.Shield.Value;
                
            if (request.Stats.Armor.HasValue)
                warframe.Stats.Armor = request.Stats.Armor.Value;
                
            if (request.Stats.Energy.HasValue)
                warframe.Stats.Energy = request.Stats.Energy.Value;
        }
    }
}