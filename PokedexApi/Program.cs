using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using PokedexApi.Gateways;
using PokedexApi.Services;
using Grpc.Net.Client;
using TrainerService = PokedexApi.Services.TrainerService;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddControllers();
builder.Services.AddScoped<IPokemonService, PokemonService>();
builder.Services.AddScoped<IPokemonGateway, PokemonGateway>();
builder.Services.AddScoped<ITrainerGateway, TrainerGateway>();
builder.Services.AddScoped<ITrainerService, TrainerService>();

builder.Services.AddSingleton(services =>
{
    var trainerApiEndpoint = builder.Configuration.GetValue<string>("TrainerApiEndpoints") ?? "http://localhost:9080";
    
    // Configure channel for gRPC with HTTP/2 support
    var channel = GrpcChannel.ForAddress(trainerApiEndpoint, new GrpcChannelOptions
    {
        HttpHandler = new HttpClientHandler()
        {
            ServerCertificateCustomValidationCallback = HttpClientHandler.DangerousAcceptAnyServerCertificateValidator
        }
    });
    
    return new PokedexApi.TrainerService.TrainerServiceClient(channel);
});

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme).AddJwtBearer(options =>
{
    options.Authority = builder.Configuration.GetValue<string>("Authentication:Authority");
    options.TokenValidationParameters = new TokenValidationParameters

    {
        ValidateIssuer = true,
        ValidIssuer = builder.Configuration.GetValue<string>("Authentication:Issuer"),
        ValidateActor = false,
        ValidateLifetime = true,
        ValidateAudience = true,
        ValidAudience = "pokedex-api",
        ValidateIssuerSigningKey = true
    };
    options.RequireHttpsMetadata = false;
});
builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("Read", policy => policy.RequireClaim("http://schemas.microsoft.com/identity/claims/scope",
                    "read"));
    options.AddPolicy("Write", policy => policy.RequireClaim("http://schemas.microsoft.com/identity/claims/scope",
                    "write"));
});

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();

app.UseAuthentication();
app.UseAuthorization();

app.UseHttpsRedirection();
app.MapControllers();

app.Run();