using CodexApi.Gateways;
using CodexApi.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new() { Title = "CodexApi", Version = "v1" });
    c.IncludeXmlComments(Path.Combine(AppContext.BaseDirectory, "CodexApi.xml"));
});
builder.Services.AddControllers();

builder.Services.AddScoped<IWarframeService, WarframeService>();
builder.Services.AddScoped<IWarframeGateway, WarframeGateway>();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "CodexApi v1");
});

app.UseHttpsRedirection();
app.MapControllers();

app.Run();