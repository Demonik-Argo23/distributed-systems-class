using Google.Protobuf.WellKnownTypes;
using TennoApi.Infrastructure.Documents;

namespace TennoApi.Mappers;

public static class TennoMapper
{
    public static TennoResponse ToResponse(this TennoDocument document)
    {
        return new TennoResponse
        {
            Id = document.Id,
            Name = document.Name,
            Clan = document.Clan,
            MasteryRank = document.MasteryRank,
            FocusSchool = document.FocusSchool,
            Stats = new TennoStats
            {
                TotalWarframes = document.Stats.TotalWarframes,
                TotalWeapons = document.Stats.TotalWeapons,
                TotalCompanions = document.Stats.TotalCompanions,
                MissionCompleted = document.Stats.MissionCompleted
            },
            CreatedAt = Timestamp.FromDateTime(document.CreatedAt)
        };
    }

    public static TennoDocument ToDocument(this CreateTennoRequest request)
    {
        return new TennoDocument
        {
            Name = request.Name,
            Clan = request.Clan,
            MasteryRank = request.MasteryRank,
            FocusSchool = request.FocusSchool,
            Stats = new TennoStatsDocument
            {
                TotalWarframes = request.Stats.TotalWarframes,
                TotalWeapons = request.Stats.TotalWeapons,
                TotalCompanions = request.Stats.TotalCompanions,
                MissionCompleted = request.Stats.MissionCompleted
            },
            CreatedAt = DateTime.UtcNow
        };
    }
}