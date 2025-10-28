using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace TennoApi.Models;

public class TennoStats
{
    public int TotalWarframes { get; set; }
    public int TotalWeapons { get; set; }
    public int TotalCompanions { get; set; }
    public int MissionCompleted { get; set; }
}