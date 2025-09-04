using WarframeApi.Dtos;
using WarframeApi.Models;
using WarframeApi.Infrastructure.Entities;

namespace WarframeApi.Mappers;

public static class WarframeMapper
{
    public static Warframe ToModel(this WarframeEntity warframeEntity)
    {
        if (warframeEntity is null)
        {
            return null;
        }

        return new Warframe
        {
            Id = warframeEntity.Id,
            Name = warframeEntity.Name,
            Rol = warframeEntity.Rol,
            Rank = warframeEntity.Rank,
            Stats = new Stats
            {
                Hp = warframeEntity.Hp,
                Shield = warframeEntity.Shield,
                Armor = warframeEntity.Armor,
                Energy = warframeEntity.Energy
            }
        };
    }

    public static WarframeEntity ToEntity(this Warframe warframe)
    {
        if (warframe is null)
        {
            return null;
        }

        return new WarframeEntity
        {
            Id = warframe.Id,
            Name = warframe.Name,
            Rol = warframe.Rol,
            Rank = warframe.Rank,
            Hp = warframe.Stats.Hp,
            Shield = warframe.Stats.Shield,
            Armor = warframe.Stats.Armor,
            Energy = warframe.Stats.Energy
        };
    }

    public static Warframe ToModel(this CreateWarframeDto createWarframeDto)
    {
        if (createWarframeDto is null)
        {
            return null;
        }

        return new Warframe
        {
            Id = Guid.NewGuid(),
            Name = createWarframeDto.Name,
            Rol = createWarframeDto.Rol,
            Rank = createWarframeDto.Rank,
            Stats = new Stats
            {
                Hp = createWarframeDto.Stats.Hp,
                Shield = createWarframeDto.Stats.Shield,
                Armor = createWarframeDto.Stats.Armor,
                Energy = createWarframeDto.Stats.Energy
            }
        };
    }

    public static WarframeResponseDto ToResponseDto(this Warframe warframe)
    {
        if (warframe is null)
        {
            return null;
        }

        return new WarframeResponseDto
        {
            Id = warframe.Id,
            Name = warframe.Name,
            Rol = warframe.Rol,
            Rank = warframe.Rank,
            Hp = warframe.Stats.Hp,
            Shield = warframe.Stats.Shield,
            Armor = warframe.Stats.Armor,
            Energy = warframe.Stats.Energy
        };
    }
}