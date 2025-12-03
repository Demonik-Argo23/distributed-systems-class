Write-Host "Configurando clientes OAuth2 en ORY Hydra..." -ForegroundColor Cyan

Write-Host "Esperando a que Hydra esté disponible..." -ForegroundColor Yellow
do {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:4445/health/ready" -Method Get -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            break
        }
    }
    catch {
        Write-Host "Hydra no está listo, esperando..." -ForegroundColor Yellow
        Start-Sleep -Seconds 2
    }
} while ($true)

Write-Host "Hydra está listo! Creando clientes OAuth2..." -ForegroundColor Green

Write-Host "Creando cliente principal para la API..." -ForegroundColor Cyan
$zeldaClientBody = @{
    client_id = "zelda-api-client"
    client_name = "Zelda Codex API Client"
    client_secret = "zelda-secret-2024"
    grant_types = @("client_credentials")
    response_types = @("token")
    scope = "read write"
    token_endpoint_auth_method = "client_secret_basic"
    access_token_strategy = "jwt"
} | ConvertTo-Json

try {
    $zeldaClient = Invoke-RestMethod -Uri "http://localhost:4445/admin/clients" `
        -Method Post `
        -ContentType "application/json" `
        -Body $zeldaClientBody
    Write-Host "Cliente principal creado exitosamente!" -ForegroundColor Green
}
catch {
    Write-Host "Error al crear cliente principal: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "Creando cliente adicional para testing..." -ForegroundColor Cyan
$zeldaTestClientBody = @{
    client_id = "zelda-codex-client"
    client_name = "Zelda Codex Test Client"
    client_secret = "zelda-codex-secret"
    grant_types = @("client_credentials")
    response_types = @("token")
    scope = "weapons:read weapons:write"
    token_endpoint_auth_method = "client_secret_basic"
    access_token_strategy = "jwt"
} | ConvertTo-Json

try {
    $zeldaTestClient = Invoke-RestMethod -Uri "http://localhost:4445/admin/clients" `
        -Method Post `
        -ContentType "application/json" `
        -Body $zeldaTestClientBody
    Write-Host "Cliente de testing creado exitosamente!" -ForegroundColor Green
}
catch {
    Write-Host "Error al crear cliente de testing: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "✅ Configuración OAuth2 completada!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Datos para Postman:" -ForegroundColor Cyan
Write-Host "Cliente Principal:" -ForegroundColor Yellow
Write-Host "- Client ID: zelda-api-client"
Write-Host "- Client Secret: zelda-secret-2024"
Write-Host "- Token URL: http://localhost:4444/oauth2/token"
Write-Host "- Grant Type: client_credentials"
Write-Host "- Scopes: read write"
Write-Host ""
Write-Host "Cliente de Testing:" -ForegroundColor Yellow
Write-Host "- Client ID: zelda-codex-client"
Write-Host "- Client Secret: zelda-codex-secret"
Write-Host "- Token URL: http://localhost:4444/oauth2/token"
Write-Host "- Grant Type: client_credentials"
Write-Host "- Scopes: weapons:read weapons:write"
