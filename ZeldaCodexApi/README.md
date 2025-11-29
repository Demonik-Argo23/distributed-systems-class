# Sistema Distribuido Completo

Sistema de gestión integral de armas y personajes de Zelda implementado con arquitectura de microservicios. Integra tres tipos de comunicación: REST, SOAP y gRPC, con autenticación OAuth2 JWT, caché distribuido Redis y dos bases de datos (PostgreSQL y MongoDB).

## Descripción del Sistema

Proyecto final de Sistemas Distribuidos que demuestra la implementación de tres paradigmas de comunicación RPC:

1. **REST API (ZeldaCodexApi)**: API Gateway principal con OAuth2 JWT
2. **SOAP Service (ZeldaApi)**: Servicio de gestión de armas con PostgreSQL
3. **gRPC Service (ZeldaCharactersApi)**: Servicio de personajes en Python con MongoDB

El sistema implementa:
- Autenticación y autorización con OAuth2 (ORY Hydra)
- Caché distribuido con Redis
- Integración de 3 tipos de RPC (Unary, Client Streaming, Server Streaming)
- Bases de datos relacionales (PostgreSQL) y NoSQL (MongoDB)
- Arquitectura de microservicios con Docker Compose


## Requisitos

- Docker Desktop instalado y ejecutándose
- Docker Compose V2 o superior
- Puertos disponibles: 4444, 4445, 5432, 6379, 8081, 8082, 27017, 50051
- Postman o herramienta similar para pruebas de API

## Inicio Rápido

### Paso 1: Levantar todos los servicios

```bash
cd ZeldaCodexApi
docker-compose up --build
```

**Esto levantará automáticamente 7 servicios:**
- ORY Hydra (OAuth2 Server)
- ZeldaCodexApi (REST API Gateway)
- ZeldaApi (SOAP Service)  
- ZeldaCharactersApi (gRPC Service)
- PostgreSQL (Base de datos de armas)
- MongoDB (Base de datos de personajes)
- Redis (Cache)

### Paso 2: Configurar clientes OAuth2 (después de 2-3 minutos)

Espera a que Hydra esté listo y ejecuta:

**Opción A - Script automatizado (recomendado):**
```bash
# En sistemas Unix/Linux/Mac
./setup-oauth-clients.sh

# En Windows con Git Bash o WSL
bash setup-oauth-clients.sh
```

**Opción B - Comandos manuales:**
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

## Servicios y Puertos

### Descripción Detallada de Servicios

#### ORY Hydra - Servidor OAuth2 (Puertos 4444/4445)
- Servidor OAuth2 que genera tokens JWT
- Puerto 4444: API pública para obtener tokens
- Puerto 4445: API administrativa para gestionar clientes
- Configurado para estrategia de tokens JWT

#### ZeldaCodexApi - REST API Gateway (Puerto 8082)
- Implementa API REST con patrón HATEOAS y autenticación OAuth2 JWT
- Actúa como gateway hacia los servicios SOAP y gRPC
- Integra cache Redis para optimización de rendimiento
- Maneja validaciones de entrada y autorización por scopes
- Proporciona operaciones CRUD completas protegidas
- **Endpoints**: `/api/v1/weapons`, `/api/v1/characters`

#### ZeldaApi - Servicio SOAP Backend (Puerto 8081)  
- Servicio SOAP que expone operaciones de gestión de armas
- Maneja persistencia en base de datos PostgreSQL
- Implementa namespace: http://zelda.com/weapons
- Ejecuta migraciones automáticas con Flyway
- Incluye 50 registros de armas predefinidas

#### ZeldaCharactersApi - Servicio gRPC (Puerto 50051)
- Servicio gRPC en Python para gestión de personajes
- Implementa 3 tipos de RPC: Unary, Client Streaming, Server Streaming
- Base de datos MongoDB con validaciones integradas
- Incluye 6 personajes predefinidos
- **Documentación detallada**: Ver `ZeldaCharactersApi/README.md`

#### PostgreSQL Database (Puerto 5432)
- Base de datos relacional para armas
- Contiene tabla weapons con datos iniciales
- Configuración: zelda_weapons_db/zelda_user/zelda_pass
- Volumen persistente para conservar datos entre reinicios

#### MongoDB Database (Puerto 27017)
- Base de datos NoSQL para personajes
- Colección characters con índices optimizados
- Configuración: zelda_characters
- Datos iniciales: 6 personajes de Breath of the Wild

#### Redis Cache (Puerto 6379)
- Cache distribuido con TTL de 10 minutos
- Optimiza consultas frecuentes de lectura de armas
- Configurado con persistencia en disco
- Se invalida automáticamente en operaciones de escritura

### Tabla Resumen

| Servicio | Puerto | Descripción | Tecnología |
|----------|--------|-------------|------------|
| Hydra OAuth2 | 4444/4445 | Servidor de autenticación JWT | Go |
| ZeldaCodexApi | 8082 | REST API Gateway | Java 17 + Spring Boot |
| ZeldaApi | 8081 | SOAP Service | Java 17 + JAXB |
| ZeldaCharactersApi | 50051 | gRPC Service | Python 3.11 |
| PostgreSQL | 5432 | BD Relacional (armas) | PostgreSQL 15 |
| MongoDB | 27017 | BD NoSQL (personajes) | MongoDB 7.0 |
| Redis | 6379 | Cache distribuido | Redis 7 |

## Autenticación OAuth2

Todos los endpoints de escritura y lectura de datos requieren autenticación mediante tokens JWT emitidos por ORY Hydra.

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

## API REST - Weapons

### Base URL: `http://localhost:8082/api/v1/weapons`

API REST que actúa como cliente del servicio SOAP backend. Implementa caché Redis para optimización.

| Método | Endpoint | Descripción | Scope Requerido |
|--------|----------|-------------|-----------------|
| GET | /weapons | Listar todas las armas (paginado) | read |
| GET | /weapons/{id} | Obtener arma por ID | read |
| POST | /weapons | Crear nueva arma | write |
| PUT | /weapons/{id} | Actualizar arma completa | write |
| PATCH | /weapons/{id} | Actualización parcial | write |
| DELETE | /weapons/{id} | Eliminar arma | write |

### Modelo de Datos - Weapon

```json
{
    "id": "UUID",
    "name": "string (2-100 caracteres)",
    "weaponType": "ONE_HANDED_SWORD | TWO_HANDED_SWORD | SPEAR | BOW | SHIELD",
    "damage": "integer (1-999)",
    "durability": "integer (1-9999)",
    "element": "FIRE | ICE | LIGHTNING | NONE"
}
```

## API REST - Characters (gRPC Client)

### Base URL: `http://localhost:8082/api/v1/characters`

API REST que actúa como cliente del servicio gRPC backend. Los endpoints REST consumen los tres tipos de RPC.

| Método | Endpoint | Descripción | Tipo gRPC | Scope |
|--------|----------|-------------|-----------|-------|
| GET | /characters/{id} | Obtener personaje | Unary | read |
| POST | /characters | Crear personaje | Unary | write |
| POST | /characters/batch | Crear múltiples | Client Streaming | write |
| GET | /characters/by-game/{game} | Listar por juego | Server Streaming | read |
| PUT | /characters/{id} | Actualizar personaje | Unary | write |
| DELETE | /characters/{id} | Eliminar personaje | Unary | write |
| GET | /characters/by-weapon/{weaponId} | Listar por arma | Unary | read |

### Modelo de Datos - Character

```json
{
    "id": "string (ObjectId de MongoDB)",
    "name": "string (2-100 caracteres)",
    "email": "string (formato válido, único)",
    "game": "string",
    "race": "string (opcional)",
    "health": "integer (1-999)",
    "stamina": "integer (1-999)",
    "attack": "integer (1-999)",
    "defense": "integer (1-999)",
    "weapons": ["array de weapon IDs"],
    "createdAt": "ISO Date",
    "updatedAt": "ISO Date"
}
```

### Validaciones gRPC

| Campo | Validación | Error |
|-------|------------|-------|
| name, email, game | Obligatorio, min 2 caracteres | `INVALID_ARGUMENT` |
| email | Formato RFC 5322 | `INVALID_ARGUMENT` |
| health, stamina, attack, defense | Rango 1-999 | `INVALID_ARGUMENT` |
| email | Único en MongoDB | `ALREADY_EXISTS` |
| id | Debe existir | `NOT_FOUND` |

## Ejemplos de Uso

### Weapons (SOAP)

#### Listar todas las armas
```bash
GET http://localhost:8082/api/v1/weapons
Authorization: Bearer [token_jwt]
```

#### Obtener arma específica
```bash
GET http://localhost:8082/api/v1/weapons/550e8400-e29b-41d4-a716-446655440001
Authorization: Bearer [token_jwt]
```

#### Crear nueva arma
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

#### Actualización parcial (PATCH)
```bash
PATCH http://localhost:8082/api/v1/weapons/{id}
Authorization: Bearer [token_jwt]
Content-Type: application/json

{
    "damage": 35
}
```

#### Eliminar arma
```bash
DELETE http://localhost:8082/api/v1/weapons/{id}
Authorization: Bearer [token_jwt]
```

### Characters (gRPC)

#### Crear personaje (Unary RPC)
```bash
POST http://localhost:8082/api/v1/characters
Authorization: Bearer [token_jwt]
Content-Type: application/json

{
    "name": "Ganondorf",
    "email": "ganondorf@evil.com",
    "game": "Ocarina of Time",
    "race": "Gerudo",
    "health": 500,
    "stamina": 300,
    "attack": 200,
    "defense": 150,
    "weapons": ["550e8400-e29b-41d4-a716-446655440001"]
}
```

#### Obtener personaje (Unary RPC)
```bash
GET http://localhost:8082/api/v1/characters/507f1f77bcf86cd799439011
Authorization: Bearer [token_jwt]
```

#### Crear múltiples personajes (Client Streaming RPC)
```bash
POST http://localhost:8082/api/v1/characters/batch
Authorization: Bearer [token_jwt]
Content-Type: application/json

[
    {
        "name": "Sheik",
        "email": "sheik@hyrule.com",
        "game": "Ocarina of Time",
        "race": "Hylian",
        "health": 110,
        "stamina": 130,
        "attack": 85,
        "defense": 75,
        "weapons": []
    },
    {
        "name": "Impa",
        "email": "impa@sheikah.com",
        "game": "Ocarina of Time",
        "race": "Sheikah",
        "health": 140,
        "stamina": 115,
        "attack": 95,
        "defense": 100,
        "weapons": []
    }
]
```

#### Listar por juego (Server Streaming RPC)
```bash
GET http://localhost:8082/api/v1/characters/by-game/Breath%20of%20the%20Wild
Authorization: Bearer [token_jwt]
```

#### Listar personajes por arma (Unary RPC - Integración)
```bash
GET http://localhost:8082/api/v1/characters/by-weapon/550e8400-e29b-41d4-a716-446655440001
Authorization: Bearer [token_jwt]
```

## Datos Iniciales

### Weapons (PostgreSQL)
El sistema incluye 50 armas predefinidas con IDs fijos:
- Master Sword (ID: `550e8400-e29b-41d4-a716-446655440001`)
- Royal Claymore (ID: `550e8400-e29b-41d4-a716-446655440002`)
- Ancient Spear (ID: `550e8400-e29b-41d4-a716-446655440003`)
- Y 47 armas adicionales de diferentes tipos y elementos

### Characters (MongoDB)
El sistema incluye 6 personajes predefinidos de Breath of the Wild:
- Link
- Zelda
- Daruk
- Mipha
- Revali
- Urbosa

## Tecnologías

### Backend (Java/Spring Boot)
- **Java 17** con Eclipse Temurin
- **Spring Boot 3.1.0** - Framework principal
- **Spring Security OAuth2 Resource Server** - Autenticación JWT
- **Spring Web Services** - Cliente SOAP
- **gRPC Java 1.60.0** - Cliente gRPC
- **Redis** - Caché distribuido
- **JAXB** - Serialización XML para SOAP
- **Protocol Buffers** - Serialización para gRPC

### Backend (Python/gRPC)
- **Python 3.11** 
- **gRPC 1.60.0** - Servidor gRPC
- **PyMongo 4.6.1** - Driver MongoDB
- **Protocol Buffers** - Definición de servicios

### OAuth2 & Security
- **ORY Hydra** - Servidor OAuth2/OpenID Connect
- **JWT** - Tokens de acceso
- **Client Credentials Flow** - Grant type

### Bases de Datos
- **PostgreSQL 15** - Base de datos relacional (armas)
- **MongoDB 7.0** - Base de datos NoSQL (personajes)
- **Redis 7** - Caché en memoria
- **Flyway** - Migraciones de PostgreSQL

### Infraestructura
- **Docker** - Containerización
- **Docker Compose** - Orquestación
- **HATEOAS** - Hipermedia REST
- **OpenAPI 3.0 (Swagger)** - Documentación API


---

**Proyecto desarrollado para la clase de Sistemas Distribuidos**
**Implementa REST, SOAP y gRPC con OAuth2, PostgreSQL, MongoDB y Redis**