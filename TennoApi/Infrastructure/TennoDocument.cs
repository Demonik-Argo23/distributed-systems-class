using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace TennoApi.Infrastructure.Documents;

public class TennoDocument
{
    [BsonId]
    [BsonRepresentation(BsonType.ObjectId)]
    public string Id { get; set; } = string.Empty;

    [BsonElement("name")]
    public string Name { get; set; } = string.Empty;

    [BsonElement("clan")]
    public string Clan { get; set; } = string.Empty;

    [BsonElement("mastery_rank")]
    public int MasteryRank { get; set; }

    [BsonElement("focus_school")]
    public string FocusSchool { get; set; } = string.Empty;

    [BsonElement("stats")]
    public TennoStatsDocument Stats { get; set; } = new();

    [BsonElement("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}

public class TennoStatsDocument
{
    [BsonElement("total_warframes")]
    public int TotalWarframes { get; set; }

    [BsonElement("total_weapons")]
    public int TotalWeapons { get; set; }

    [BsonElement("total_companions")]
    public int TotalCompanions { get; set; }

    [BsonElement("mission_completed")]
    public int MissionCompleted { get; set; }
}