# Zelda Weapons Management System

Sistema de gestión de armas de Zelda implementado con arquitectura de microservicios. Incluye API REST Gateway con autenticación OAuth2 JWT, servicio SOAP backend, cache distribuido Redis y base de datos PostgreSQL.

## Requisitos del Sistema

- Docker Desktop instalado y ejecutándose
- Docker Compose V2 o superior
- Puertos disponibles: 4444, 4445, 5432, 6379, 8081, 8082
- Postman o herramienta similar para pruebas de API

## Inicio del Sistema

### Paso 1: Levantar los servicios
```bash
cd ZeldaCodexApi
docker-compose up --build
```

### Paso 2: Configurar clientes OAuth2 (después de 2-3 minutos)

**Opción A - Script automatizado (recomendado):**
```bash
# En sistemas Unix/Linux/Mac
./setup-oauth-clients-fixed.sh

# En Windows con Git Bash o WSL
bash setup-oauth-clients-fixed.sh
```

**Opción B - Comandos manuales (si el script falla):**
```bash
# Esperar a que Hydra esté listo
curl http://localhost:4445/health/ready

# Crear cliente principal
curl -X POST http://localhost:4445/admin/clients \
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
  }'

# Crear cliente de testing (opcional)
curl -X POST http://localhost:4445/admin/clients \
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
  }'
```

**Opción C - PowerShell (Windows):**
```powershell
# Crear cliente principal
$body = @{
    client_id = "zelda-api-client"
    client_name = "Zelda Codex API Client"
    client_secret = "zelda-secret-2024"
    grant_types = @("client_credentials")
    response_types = @("token")
    scope = "read write"
    token_endpoint_auth_method = "client_secret_basic"
    access_token_strategy = "jwt"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:4445/admin/clients" -Method POST -Headers @{"Content-Type"="application/json"} -Body $body
```

El sistema estará completamente operativo después del Paso 2. Los servicios se inicializan automáticamente con datos de prueba y configuración OAuth2.

## Arquitectura de Microservicios

### ORY Hydra - Servidor OAuth2 (Puertos 4444/4445)
- Servidor OAuth2 que genera tokens JWT
- Puerto 4444: API pública para obtener tokens
- Puerto 4445: API administrativa para gestionar clientes
- Configurado para estrategia de tokens JWT

### ZeldaCodexApi - API REST Gateway (Puerto 8082)
- Implementa API REST con patrón HATEOAS y autenticación OAuth2 JWT
- Actúa como gateway hacia el servicio SOAP backend
- Integra cache Redis para optimización de rendimiento
- Maneja validaciones de entrada y autorización por scopes
- Proporciona operaciones CRUD completas protegidas

### ZeldaApi - Servicio SOAP Backend (Puerto 8081)  
- Servicio SOAP que expone operaciones de gestión de armas
- Maneja persistencia en base de datos PostgreSQL
- Implementa namespace: http://zelda.com/weapons
- Ejecuta migraciones automáticas con Flyway
- Incluye 50 registros de armas predefinidas

### PostgreSQL Database (Puerto 5432)
- Base de datos principal del sistema
- Contiene tabla weapons con datos iniciales
- Configuración: zelda_weapons_db/zelda_user/zelda_pass
- Volumen persistente para conservar datos entre reinicios

### Redis Cache (Puerto 6379)
- Cache distribuido con TTL de 10 minutos
- Optimiza consultas frecuentes de lectura
- Configurado con persistencia en disco
- Se invalida automáticamente en operaciones de escritura

## Servicios y Puertos

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| Hydra OAuth2 | 4444/4445 | Servidor de autenticación JWT |
| ZeldaCodexApi | 8082 | API REST Gateway |
| ZeldaApi | 8081 | Servicio SOAP Backend |
| PostgreSQL | 5432 | Base de datos principal |
| Redis | 6379 | Cache distribuido |

## Autenticación OAuth2

### Configuración de Cliente OAuth2
- Client ID: `zelda-api-client`
- Client Secret: `zelda-secret-2024`
- Grant Type: `client_credentials`
- Scopes disponibles: `read`, `write`
- Token URL: `http://localhost:4444/oauth2/token`

### Obtener Token JWT en Postman

1. **Crear request POST**:
   - URL: `http://localhost:4444/oauth2/token`
   - Method: `POST`

2. **Configurar Authorization**:
   - Type: `Basic Auth`
   - Username: `zelda-api-client`
   - Password: `zelda-secret-2024`

3. **Configurar Body**:
   - Type: `x-www-form-urlencoded`
   - Key: `grant_type`, Value: `client_credentials`
   - Key: `scope`, Value: `read write`

4. **Enviar request** - Debería retornar token JWT que empieza con `eyJ`

5. **Usar token** en requests posteriores:
   - Authorization Type: `Bearer Token`
   - Token: `[token_obtenido]`

## Endpoints de la API REST

### Base URL: http://localhost:8082/api/v1/weapons

| Método | Endpoint | Descripción | Scope Requerido |
|--------|----------|-------------|-----------------|
| GET | /weapons | Listar todas las armas (paginado) | read |
| GET | /weapons/{id} | Obtener arma por ID | read |
| POST | /weapons | Crear nueva arma | write |
| PUT | /weapons/{id} | Actualizar arma completa | write |
| PATCH | /weapons/{id} | Actualización parcial | write |
| DELETE | /weapons/{id} | Eliminar arma | write |

### Endpoints Públicos (sin autenticación)
- `GET /swagger-ui.html` - Documentación Swagger
- `GET /actuator/health` - Estado del servicio

## Modelo de Datos

### Entidad Weapon
```json
{
    "id": "UUID",
    "name": "string (2-100 caracteres)",
    "weaponType": "enum",
    "damage": "integer (1-999)",
    "durability": "integer (1-9999)",
    "element": "enum"
}
```

### Tipos de Arma (weaponType)
- ONE_HANDED_SWORD
- TWO_HANDED_SWORD  
- SPEAR
- BOW
- SHIELD

### Elementos (element)
- FIRE
- ICE
- LIGHTNING
- NONE

## Ejemplos de Uso

### Listar todas las armas
```bash
GET http://localhost:8082/api/v1/weapons
Authorization: Bearer [token_jwt]
```

### Obtener arma específica
```bash
GET http://localhost:8082/api/v1/weapons/550e8400-e29b-41d4-a716-446655440001
Authorization: Bearer [token_jwt]
```

### Crear nueva arma
```bash
POST http://localhost:8082/api/v1/weapons
Authorization: Bearer [token_jwt]
Content-Type: application/json

{
    "name": "Test Sword",
    "weaponType": "ONE_HANDED_SWORD",
    "damage": 25,
    "durability": 100,
    "element": "FIRE"
}
```

### Actualización parcial (PATCH)
```bash
PATCH http://localhost:8082/api/v1/weapons/{id}
Authorization: Bearer [token_jwt]
Content-Type: application/json

{
    "damage": 35
}
```

### Eliminar arma
```bash
DELETE http://localhost:8082/api/v1/weapons/{id}
Authorization: Bearer [token_jwt]
```

## Troubleshooting

### Problemas con el Script de OAuth2

**Error: "command not found" o caracteres extraños**
- **Causa**: Caracteres de fin de línea incorrectos (Windows vs Unix)
- **Solución**: Usar `setup-oauth-clients-fixed.sh` o comandos manuales

**Error: "Client already exists"**
- **Causa**: Los clientes ya fueron creados previamente
- **Solución**: Normal, el sistema funcionará correctamente

**Error: "Connection refused" en puerto 4445**
- **Causa**: Hydra no ha terminado de iniciarse
- **Solución**: Esperar 2-3 minutos más y reintentar

**Error de permisos en script**
```bash
chmod +x setup-oauth-clients-fixed.sh
./setup-oauth-clients-fixed.sh
```

### Verificar configuración OAuth2
```bash
# Verificar que el cliente fue creado
curl -X GET http://localhost:4445/admin/clients/zelda-api-client

# Probar obtención de token
curl -X POST http://localhost:4444/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=read write" \
  -u "zelda-api-client:zelda-secret-2024"
```

Si el token retornado empieza con "eyJ", la configuración JWT está correcta.

## Verificación del Sistema

### Comprobar estado de servicios
```bash
docker-compose ps
```
Todos los servicios deben mostrar estado "Up".

### Verificar Hydra OAuth2
```bash
curl http://localhost:4445/health/ready
```
Debe retornar: `{"status":"ok"}`

### Verificar API Gateway
```bash
curl http://localhost:8082/swagger-ui.html
```
Debe retornar página HTML de Swagger.

### Verificar base de datos
```bash
docker exec -it zelda-postgres psql -U zelda_user -d zelda_weapons_db -c "SELECT COUNT(*) FROM weapons;"
```
Debe retornar: `count: 50`

### Prueba completa de autenticación
1. Obtener token JWT de Hydra
2. Usar token en request a API Gateway
3. Verificar que se obtienen datos de armas

## Datos Iniciales

El sistema incluye 50 armas predefinidas con IDs fijos para facilitar las pruebas:
- Master Sword (ID: 550e8400-e29b-41d4-a716-446655440001)
- Royal Claymore (ID: 550e8400-e29b-41d4-a716-446655440002)
- Ancient Spear (ID: 550e8400-e29b-41d4-a716-446655440003)
- Y 47 armas adicionales con diferentes tipos y elementos

## Tecnologías Implementadas

- Java 17 + Spring Boot 3.1.0
- Spring Security OAuth2 Resource Server con JWT
- ORY Hydra como servidor OAuth2/OpenID Connect
- SOAP Web Services con JAXB
- PostgreSQL 15 con Flyway migrations
- Redis 7 para caching distribuido
- Docker y Docker Compose para orquestación
- HATEOAS para navegabilidad de API REST
- Validaciones JSR-303
- Logs estructurados

## Estructura del Proyecto

```
distributed-systems-class/
├── ZeldaCodexApi/           # API REST Gateway
│   ├── src/main/java/       # Código fuente del gateway
│   ├── docker-compose.yml  # Orquestación de todos los servicios
│   ├── Dockerfile          # Imagen del gateway
│   └── README.md           # Documentación
│
└── ZeldaApi/               # SOAP Backend
    ├── src/main/java/      # Código fuente del servicio SOAP
    ├── src/main/resources/
    │   └── db/migration/   # Scripts de migración SQL
    ├── Dockerfile          # Imagen del backend
    └── docker-compose.yml  # Configuración individual
```

El sistema está configurado para funcionar completamente con un único comando docker-compose up --build desde el directorio ZeldaCodexApi.