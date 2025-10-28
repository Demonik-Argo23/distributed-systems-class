using MongoDB.Driver;
using TennoApi.Services;
using TennoApi.Infrastructure;
using TennoApi.Repositories;

var builder = WebApplication.CreateBuilder(args);

//mondongo
builder.Services.Configure<MongoDBSettings>(
    builder.Configuration.GetSection("MongoDB"));

builder.Services.AddSingleton<IMongoDatabase>(sp =>
{
    var settings = builder.Configuration.GetSection("MongoDB").Get<MongoDBSettings>();
    var client = new MongoClient(settings!.ConnectionString);
    return client.GetDatabase(settings.DatabaseName);
});

builder.Services.AddScoped<ITennoRepository, TennoRepository>();

builder.Services.AddGrpc();

var app = builder.Build();

app.MapGrpcService<TennoService>();
app.MapGet("/", () => "Communication with gRPC endpoints must be made through a gRPC client. To learn how to create a client, visit: https://go.microsoft.com/fwlink/?linkid=2086909");

app.Run();