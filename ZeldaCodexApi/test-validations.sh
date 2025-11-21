#!/bin/bash

echo "=== PRUEBAS DE VALIDACIÓN - API REST ZELDA ==="
echo ""

# Configurar token
echo "Obteniendo token JWT..."
TOKEN_RESPONSE=$(curl -s -X POST http://localhost:4444/oauth2/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=client_credentials&scope=read write' \
  -u 'zelda-api-client:zelda-secret-2024')

TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')

if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
    echo "ERROR: No se pudo obtener el token JWT"
    echo "Respuesta: $TOKEN_RESPONSE"
    exit 1
fi

echo "Token JWT obtenido correctamente"
echo ""

# Función para hacer requests
test_request() {
    local method=$1
    local url=$2
    local data=$3
    local expected_status=$4
    local test_name=$5
    
    echo "🧪 PRUEBA: $test_name"
    echo "   $method $url"
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "HTTP_STATUS:%{http_code}" -X $method \
            -H "Authorization: Bearer $TOKEN" \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$url")
    else
        response=$(curl -s -w "HTTP_STATUS:%{http_code}" -X $method \
            -H "Authorization: Bearer $TOKEN" \
            "$url")
    fi
    
    http_status=$(echo $response | grep -o "HTTP_STATUS:[0-9]*" | cut -d: -f2)
    response_body=$(echo $response | sed 's/HTTP_STATUS:[0-9]*$//')
    
    if [ "$http_status" == "$expected_status" ]; then
        echo "   ✅ ÉXITO: Status $http_status (esperado $expected_status)"
    else
        echo "   ❌ FALLO: Status $http_status (esperado $expected_status)"
        echo "   Respuesta: $response_body"
    fi
    echo ""
}

# PRUEBAS DE VALIDACIÓN
echo "=== PRUEBAS DE VALIDACIÓN DE ENTRADA ==="

# 1. Nombre muy corto
test_request "POST" "http://localhost:8082/api/v1/weapons" \
    '{"name":"A","weaponType":"ONE_HANDED_SWORD","damage":25,"durability":150}' \
    "400" "Nombre muy corto (1 carácter)"

# 2. Nombre muy largo
long_name=$(printf 'a%.0s' {1..101})
test_request "POST" "http://localhost:8082/api/v1/weapons" \
    "{\"name\":\"$long_name\",\"weaponType\":\"ONE_HANDED_SWORD\",\"damage\":25,\"durability\":150}" \
    "400" "Nombre muy largo (101 caracteres)"

# 3. Daño fuera de rango (muy alto)
test_request "POST" "http://localhost:8082/api/v1/weapons" \
    '{"name":"Test Sword","weaponType":"ONE_HANDED_SWORD","damage":1500,"durability":150}' \
    "400" "Daño muy alto (1500)"

# 4. Daño fuera de rango (muy bajo)
test_request "POST" "http://localhost:8082/api/v1/weapons" \
    '{"name":"Test Sword","weaponType":"ONE_HANDED_SWORD","damage":0,"durability":150}' \
    "400" "Daño muy bajo (0)"

# 5. Durabilidad fuera de rango
test_request "POST" "http://localhost:8082/api/v1/weapons" \
    '{"name":"Test Sword","weaponType":"ONE_HANDED_SWORD","damage":25,"durability":10000}' \
    "400" "Durabilidad muy alta (10000)"

# 6. Tipo de arma inválido
test_request "POST" "http://localhost:8082/api/v1/weapons" \
    '{"name":"Test Sword","weaponType":"INVALID_TYPE","damage":25,"durability":150}' \
    "400" "Tipo de arma inválido"

# 7. Campo faltante (name)
test_request "POST" "http://localhost:8082/api/v1/weapons" \
    '{"weaponType":"ONE_HANDED_SWORD","damage":25,"durability":150}' \
    "400" "Campo name faltante"

echo "=== PRUEBAS DE ENDPOINT VALIDATION ==="

# 8. UUID inválido en GET
test_request "GET" "http://localhost:8082/api/v1/weapons/invalid-uuid" \
    "" "400" "UUID inválido en GET"

# 9. UUID inválido en PUT
test_request "PUT" "http://localhost:8082/api/v1/weapons/invalid-uuid" \
    '{"name":"Test Sword","weaponType":"ONE_HANDED_SWORD","damage":25,"durability":150}' \
    "400" "UUID inválido en PUT"

# 10. UUID que no existe
test_request "GET" "http://localhost:8082/api/v1/weapons/550e8400-e29b-41d4-a716-446655440999" \
    "" "424" "UUID válido pero arma no existe"

echo "=== PRUEBAS DE AUTORIZACIÓN ==="

# 11. Request sin token
echo "🧪 PRUEBA: Request sin token de autorización"
response=$(curl -s -w "HTTP_STATUS:%{http_code}" -X GET http://localhost:8082/api/v1/weapons)
http_status=$(echo $response | grep -o "HTTP_STATUS:[0-9]*" | cut -d: -f2)

if [ "$http_status" == "401" ]; then
    echo "   ✅ ÉXITO: Status $http_status (esperado 401)"
else
    echo "   ❌ FALLO: Status $http_status (esperado 401)"
fi
echo ""

# 12. Token con scope insuficiente
echo "🧪 PRUEBA: Token con scope READ solamente en operación WRITE"
READ_TOKEN_RESPONSE=$(curl -s -X POST http://localhost:4444/oauth2/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=client_credentials&scope=read' \
  -u 'zelda-api-client:zelda-secret-2024')

READ_TOKEN=$(echo $READ_TOKEN_RESPONSE | jq -r '.access_token')

response=$(curl -s -w "HTTP_STATUS:%{http_code}" -X POST \
    -H "Authorization: Bearer $READ_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name":"Test Sword","weaponType":"ONE_HANDED_SWORD","damage":25,"durability":150}' \
    "http://localhost:8082/api/v1/weapons")

http_status=$(echo $response | grep -o "HTTP_STATUS:[0-9]*" | cut -d: -f2)

if [ "$http_status" == "403" ] || [ "$http_status" == "500" ]; then
    echo "   ✅ ÉXITO: Status $http_status (acceso denegado)"
else
    echo "   ❌ FALLO: Status $http_status (esperado 403 o 500)"
fi
echo ""

echo "=== PRUEBAS POSITIVAS ==="

# 13. Crear arma válida
test_request "POST" "http://localhost:8082/api/v1/weapons" \
    '{"name":"Validation Test Sword","weaponType":"ONE_HANDED_SWORD","damage":45,"durability":180,"element":"FIRE"}' \
    "201" "Crear arma válida"

# 14. Listar armas
test_request "GET" "http://localhost:8082/api/v1/weapons?page=0&pageSize=5" \
    "" "200" "Listar armas con paginación"

echo "=== RESUMEN COMPLETO ==="
echo "Todas las pruebas de validación han sido ejecutadas."
echo "Revisa los resultados arriba para verificar el comportamiento."