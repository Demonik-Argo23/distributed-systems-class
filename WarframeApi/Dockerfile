FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
WORKDIR /app 
ENV ASPNETCORE_URLS=http://+:8080


USER app
FROM --platform=$BUILDPLATFORM mcr.microsoft.com/dotnet/sdk:9.0 AS build
ARG configuration=Release
WORKDIR /src
COPY ["WarframeApi.csproj", "./"]
RUN dotnet restore "WarframeApi.csproj"
COPY . .
WORKDIR "/src/."
RUN dotnet build "WarframeApi.csproj" -c ${configuration} -o /app/build

FROM build AS publish
ARG configuration=Release
RUN dotnet publish "WarframeApi.csproj" -c ${configuration} -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "WarframeApi.dll"]