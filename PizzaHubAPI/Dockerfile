# Etapa 1: Build
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src

# Copiar csproj y restaurar dependencias
COPY ["PizzaHubAPI.csproj", "./"]
RUN dotnet restore "PizzaHubAPI.csproj"

# Copiar todo el código y compilar
COPY . .
RUN dotnet build "PizzaHubAPI.csproj" -c Release -o /app/build

# Publicar la aplicación
RUN dotnet publish "PizzaHubAPI.csproj" -c Release -o /app/publish

# Etapa 2: Runtime
FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS runtime
WORKDIR /app

# Copiar archivos publicados
COPY --from=build /app/publish .

# Exponer puerto (Render usa variable PORT)
EXPOSE 5000
ENV ASPNETCORE_URLS=http://+:5000

# Comando para iniciar la aplicación
ENTRYPOINT ["dotnet", "PizzaHubAPI.dll"]
