using Microsoft.EntityFrameworkCore;
using WarframeApi.Infrastructure;
using WarframeApi.Services;
using WarframeApi.Repositories;
using SoapCore;


var builder = WebApplication.CreateBuilder(args);
builder.Services.AddSoapCore();

builder.Services.AddScoped<IWarframeRepository, WarframeRepository>();
builder.Services.AddScoped<IWarframeService, WarframeService>();

builder.Services.AddDbContext<RelationalDbContext>(options =>
options.UseMySql(builder.Configuration.GetConnectionString("DefaultConnection"),
    ServerVersion.AutoDetect(builder.Configuration.GetConnectionString("DefaultConnection"))));

var app = builder.Build();
    app.UseSoapEndpoint<IWarframeService>("/WarframeService.svc", new SoapEncoderOptions());
    app.Run();