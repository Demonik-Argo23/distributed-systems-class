# Zelda Weapons Management System

Sistema de gestión de armas de Zelda implementado con arquitectura de microservicios. Incluye API REST Gateway, servicio SOAP backend, cache distribuido Redis y base de datos PostgreSQL.

## Requisitos del Sistema

- Docker Desktop instalado y ejecutándose
- Docker Compose V2 o superior
- Puertos disponibles: 8081, 8082, 5432, 6379

## Inicio del Sistema

Para levantar todos los servicios ejecutar:

```bash
cd ZeldaCodexApi
docker-compose up --build
```

El sistema estará completamente operativo en aproximadamente 2-3 minutos. Los servicios se inicializan automáticamente con datos de prueba.

## Arquitectura de Microservicios

### ZeldaCodexApi - API REST Gateway (Puerto 8082)
- Implementa API REST con patrón HATEOAS
- Actúa como gateway hacia el servicio SOAP backend
- Integra cache Redis para optimización de rendimiento
- Maneja validaciones de entrada y respuestas estructuradas
- Proporciona operaciones CRUD completas

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
| ZeldaCodexApi | 8082 | API REST Gateway |
| ZeldaApi | 8081 | Servicio SOAP Backend |
| PostgreSQL | 5432 | Base de datos principal |
| Redis | 6379 | Cache distribuido |

## Endpoints de la API REST

### Base URL: http://localhost:8082/api/v1/weapons

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /weapons | Listar todas las armas (paginado) |
| GET | /weapons/{id} | Obtener arma por ID |
| POST | /weapons | Crear nueva arma |
| PUT | /weapons/{id} | Actualizar arma completa |
| PATCH | /weapons/{id} | Actualización parcial |
| DELETE | /weapons/{id} | Eliminar arma |

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
```

### Obtener arma específica
```bash
GET http://localhost:8082/api/v1/weapons/550e8400-e29b-41d4-a716-446655440001
```

### Crear nueva arma
```bash
POST http://localhost:8082/api/v1/weapons
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
Content-Type: application/json

{
    "damage": 35
}
```

### Eliminar arma
```bash
DELETE http://localhost:8082/api/v1/weapons/{id}
```

## Verificación del Sistema

### Comprobar estado de servicios
```bash
docker-compose ps
```

### Verificar base de datos
```bash
docker exec -it zelda-postgres psql -U zelda_user -d zelda_weapons_db -c "SELECT COUNT(*) FROM weapons;"
```

## Datos Iniciales

El sistema incluye 50 armas predefinidas con IDs fijos para facilitar las pruebas:
- Master Sword (ID: 550e8400-e29b-41d4-a716-446655440001)
- Royal Claymore (ID: 550e8400-e29b-41d4-a716-446655440002)
- Ancient Spear (ID: 550e8400-e29b-41d4-a716-446655440003)
- Y 47 armas adicionales con diferentes tipos y elementos

## Tecnologías Implementadas

- Java 17 + Spring Boot 3.1.0
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