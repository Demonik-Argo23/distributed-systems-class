
using Microsoft.EntityFrameworkCore;
using WarframeApi.Infrastructure.Entities;

namespace WarframeApi.Infrastructure;

public class RelationalDbContext : DbContext
{
    public DbSet<WarframeEntity> Warframes { get; set; }
    public RelationalDbContext(DbContextOptions<RelationalDbContext> db) : base(db)
    {

    }
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        // Configure your entities here

        modelBuilder.Entity<WarframeEntity>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Name).IsRequired().HasMaxLength(100);
            entity.Property(e => e.Rol).IsRequired().HasMaxLength(50);
            entity.Property(e => e.Rank).IsRequired();
            entity.Property(e => e.Hp).IsRequired();
            entity.Property(e => e.Shield).IsRequired();
            entity.Property(e => e.Armor).IsRequired();
            entity.Property(e => e.Energy).IsRequired();
        });
    }
}