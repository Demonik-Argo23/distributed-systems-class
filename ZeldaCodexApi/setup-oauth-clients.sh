#!/bin/bash

# Script para configurar clientes OAuth2 en ORY Hydra
# Este script debe ejecutarse después de que Hydra esté corriendo

echo "🔐 Configurando clientes OAuth2 en ORY Hydra..."

# Esperar a que Hydra esté listo
echo "⏳ Esperando a que Hydra esté disponible..."
until curl -s http://localhost:4445/health/ready; do
  echo "Esperando Hydra..."
  sleep 2
done

echo "✅ Hydra está listo"

# Cliente para aplicaciones (con scope read y write)
echo "📱 Creando cliente para aplicaciones..."
ZELDA_API_CLIENT=$(curl -s -X POST \
  http://localhost:4445/admin/clients \
  -H "Content-Type: application/json" \
  -d '{
    "client_id": "zelda-api-client",
    "client_name": "Zelda Codex API Client",
    "client_secret": "zelda-api-secret-2024",
    "grant_types": ["client_credentials", "authorization_code", "refresh_token"],
    "response_types": ["code", "token"],
    "scope": "read write",
    "token_endpoint_auth_method": "client_secret_basic",
    "redirect_uris": ["http://localhost:8082/auth/callback", "http://127.0.0.1:8082/auth/callback"]
  }')

echo "✅ Cliente de aplicación creado: $ZELDA_API_CLIENT"

# Cliente para solo lectura
echo "📖 Creando cliente de solo lectura..."
READONLY_CLIENT=$(curl -s -X POST \
  http://localhost:4445/admin/clients \
  -H "Content-Type: application/json" \
  -d '{
    "client_id": "zelda-readonly-client", 
    "client_name": "Zelda Codex Read-Only Client",
    "client_secret": "readonly-secret-2024",
    "grant_types": ["client_credentials"],
    "response_types": ["token"],
    "scope": "read",
    "token_endpoint_auth_method": "client_secret_basic"
  }')

echo "✅ Cliente de solo lectura creado: $READONLY_CLIENT"

# Cliente administrativo con todos los permisos
echo "👑 Creando cliente administrativo..."
ADMIN_CLIENT=$(curl -s -X POST \
  http://localhost:4445/admin/clients \
  -H "Content-Type: application/json" \
  -d '{
    "client_id": "zelda-admin-client",
    "client_name": "Zelda Codex Admin Client", 
    "client_secret": "admin-secret-2024",
    "grant_types": ["client_credentials", "authorization_code", "refresh_token"],
    "response_types": ["code", "token"],
    "scope": "read write admin",
    "token_endpoint_auth_method": "client_secret_basic",
    "redirect_uris": ["http://localhost:8082/auth/callback"]
  }')

echo "✅ Cliente administrativo creado: $ADMIN_CLIENT"

echo ""
echo "🎉 Configuración OAuth2 completada!"
echo ""
echo "📋 Resumen de clientes creados:"
echo "1. zelda-api-client (read, write) - Para aplicaciones generales"
echo "2. zelda-readonly-client (read) - Para consultas solamente"  
echo "3. zelda-admin-client (read, write, admin) - Para administración"
echo ""
echo "🔗 URLs importantes:"
echo "- Hydra Admin: http://localhost:4445/"
echo "- Hydra Public: http://localhost:4444/"
echo "- Token Endpoint: http://localhost:4444/oauth2/token"
echo "- Introspect Endpoint: http://localhost:4444/oauth2/introspect"
echo ""
echo "💡 Ejemplo de obtención de token:"
echo "curl -X POST http://localhost:4444/oauth2/token \\"
echo "  -H 'Content-Type: application/x-www-form-urlencoded' \\"
echo "  -d 'grant_type=client_credentials&scope=read' \\"
echo "  -u 'zelda-readonly-client:readonly-secret-2024'"
echo ""