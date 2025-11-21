#!/bin/bash

echo "Configurando clientes OAuth2 en ORY Hydra..."

# Esperar a que Hydra esté disponible
echo "Esperando a que Hydra esté disponible..."
until curl -s http://localhost:4445/health/ready > /dev/null 2>&1; do
    echo "Hydra no está listo, esperando..."
    sleep 2
done

echo "Hydra está listo! Creando clientes OAuth2..."

# Cliente principal para la API
echo "Creando cliente principal para la API..."
ZELDA_CLIENT=$(curl -s -X POST \
  http://localhost:4445/admin/clients \
  -H "Content-Type: application/json" \
  -d '{
    "client_id": "zelda-api-client",
    "client_name": "Zelda Codex API Client",
    "client_secret": "zelda-secret-2024",
    "grant_types": ["client_credentials"],
    "response_types": ["token"],
    "scope": "read write",
    "token_endpoint_auth_method": "client_secret_basic",
    "access_token_strategy": "jwt"
  }')

echo "Cliente principal creado: $ZELDA_CLIENT"

# Cliente adicional para testing
echo "Creando cliente adicional para testing..."
ZELDA_TEST_CLIENT=$(curl -s -X POST \
  http://localhost:4445/admin/clients \
  -H "Content-Type: application/json" \
  -d '{
    "client_id": "zelda-codex-client",
    "client_name": "Zelda Codex Test Client",
    "client_secret": "zelda-codex-secret",
    "grant_types": ["client_credentials"],
    "response_types": ["token"],
    "scope": "weapons:read weapons:write",
    "token_endpoint_auth_method": "client_secret_basic",
    "access_token_strategy": "jwt"
  }')

echo "Cliente de testing creado: $ZELDA_TEST_CLIENT"

echo ""
echo "✅ Configuración OAuth2 completada!"
echo ""
echo "📋 Datos para Postman:"
echo "Cliente Principal:"
echo "- Client ID: zelda-api-client"
echo "- Client Secret: zelda-secret-2024"
echo "- Token URL: http://localhost:4444/oauth2/token"
echo "- Grant Type: client_credentials"
echo "- Scopes: read write"
echo ""
echo "Cliente de Testing:"
echo "- Client ID: zelda-codex-client"
echo "- Client Secret: zelda-codex-secret"
echo "- Token URL: http://localhost:4444/oauth2/token"
echo "- Grant Type: client_credentials"
echo "- Scopes: weapons:read weapons:write"
echo ""
echo "💡 Ejemplo de obtención de token:"
echo "curl -X POST http://localhost:4444/oauth2/token \\"
echo "  -H 'Content-Type: application/x-www-form-urlencoded' \\"
echo "  -d 'grant_type=client_credentials&scope=read write' \\"
echo "  -u 'zelda-api-client:zelda-secret-2024'"
echo ""
echo "🔍 Para verificar que los tokens son JWT (empiezan con 'eyJ'):"
echo "curl -X POST http://localhost:4444/oauth2/token \\"
echo "  -H 'Content-Type: application/x-www-form-urlencoded' \\"
echo "  -d 'grant_type=client_credentials&scope=read' \\"
echo "  -u 'zelda-api-client:zelda-secret-2024' | jq -r '.access_token' | head -c 10"