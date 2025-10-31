# Sistema de Cache Redis - ZeldaCodexApi

## 📋 Descripción General

El sistema de cache implementado utiliza **Redis** para mejorar significativamente el performance de las operaciones de consulta (GET), reduciendo la latencia y la carga sobre el servicio SOAP backend.

## 🏗️ Arquitectura de Cache

```
Cliente REST → ZeldaCodexApi → Cache Redis → ZeldaWeaponsApi (SOAP) → PostgreSQL
                    ↓              ↓
                Cache HIT     Cache MISS
                    ↓              ↓
               Respuesta      Consulta SOAP
               Inmediata      + Guardar Cache
```

## 🔧 Configuración

### Docker Compose
```yaml
services:
  redis:
    image: redis:7-alpine
    container_name: zelda-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
    command: redis-server --appendonly yes
```

### Spring Boot Configuration
```properties
# Redis Cache Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms
spring.cache.type=redis
spring.cache.redis.time-to-live=600000  # 10 minutos

# Docker Profile
spring.data.redis.host=redis  # Para contenedores
```

## 📊 Estrategia de Cache

### Operaciones Cacheadas

| Operación | Cache Key | TTL | Descripción |
|-----------|-----------|-----|-------------|
| `GET /weapons/{id}` | `weapons::{id}` | 10 min | Cache individual por ID |
| `GET /weapons?page=0&size=10` | `weaponsList::{page}_{size}_{filters}` | 10 min | Cache de listados paginados |

### Invalidación de Cache

| Operación | Invalidación | Motivo |
|-----------|-------------|---------|
| `POST /weapons` | `weaponsList::*` + `weapons::{newId}` | Nueva arma afecta listados |
| `PUT /weapons/{id}` | `weapons::{id}` + `weaponsList::*` | Arma modificada |
| `PATCH /weapons/{id}` | `weapons::{id}` + `weaponsList::*` | Arma modificada |
| `DELETE /weapons/{id}` | `weapons::{id}` + `weaponsList::*` | Arma eliminada |

## 🚀 Implementación

### WeaponService - Anotaciones de Cache

```java
@Service
public class WeaponService {
    
    // Cache para consulta individual
    @Cacheable(value = "weapons", key = "#id")
    public Weapon getWeaponById(UUID id) {
        logger.info("Buscando arma con ID {} - Cache MISS", id);
        return weaponGateway.getWeaponById(id);
    }
    
    // Cache para listados paginados
    @Cacheable(value = "weaponsList", 
               key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #filters.toString()")
    public Page<Weapon> getAllWeapons(Pageable pageable, Map<String, String> filters) {
        logger.info("Obteniendo lista - Cache MISS");
        return weaponGateway.getAllWeapons(pageable, filters);
    }
    
    // Invalidación en operaciones de escritura
    @Caching(evict = {
        @CacheEvict(value = "weapons", key = "#id"),
        @CacheEvict(value = "weaponsList", allEntries = true)
    })
    public Weapon updateWeapon(UUID id, Weapon weapon) {
        logger.info("Actualizando arma - Invalidando cache");
        return weaponGateway.updateWeapon(id, weapon);
    }
}
```

### RedisConfig - Configuración Avanzada

```java
@Configuration
@EnableCaching
public class RedisConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .transactionAware()
            .build();
    }
}
```

## 📈 Métricas y Monitoreo

### Logs de Cache

```bash
# Cache HIT (consulta desde Redis)
2025-10-30 15:30:45 [INFO ] WeaponService - Cache HIT para arma ID: 123e4567...

# Cache MISS (consulta SOAP + guardar en Redis) 
2025-10-30 15:30:47 [INFO ] WeaponService - Buscando arma con ID 456f7890 - Cache MISS
2025-10-30 15:30:48 [DEBUG] WeaponService - Arma Master Sword encontrada y guardada en cache
```

### Endpoints de Administración

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/cache/info` | GET | Información del cache |
| `/cache/clear` | DELETE | Limpiar todo el cache |
| `/cache/clear/{cacheName}` | DELETE | Limpiar cache específico |
| `/cache/status` | GET | Estado de caches |
| `/cache/stats` | GET | Estadísticas básicas |

## 🧪 Testing del Cache

### 1. Verificar Cache MISS/HIT

```bash
# Primera consulta (Cache MISS)
curl -v http://localhost:8082/api/v1/weapons/123e4567-e89b-12d3-a456-426614174000

# Segunda consulta (Cache HIT - debe ser más rápida)
curl -v http://localhost:8082/api/v1/weapons/123e4567-e89b-12d3-a456-426614174000
```

### 2. Verificar Invalidación

```bash
# 1. Consultar arma (llenar cache)
curl http://localhost:8082/api/v1/weapons/123e4567-e89b-12d3-a456-426614174000

# 2. Actualizar arma (invalidar cache)
curl -X PATCH http://localhost:8082/api/v1/weapons/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -d '{"damage": 35}'

# 3. Consultar de nuevo (Cache MISS por invalidación)
curl http://localhost:8082/api/v1/weapons/123e4567-e89b-12d3-a456-426614174000
```

### 3. Verificar Cache de Listados

```bash
# Primera consulta de listado (Cache MISS)
curl "http://localhost:8082/api/v1/weapons?page=0&size=10"

# Segunda consulta idéntica (Cache HIT)
curl "http://localhost:8082/api/v1/weapons?page=0&size=10"

# Consulta con diferentes parámetros (Cache MISS)
curl "http://localhost:8082/api/v1/weapons?page=1&size=5"
```

## 🔍 Monitoreo Redis

### Comandos Redis CLI

```bash
# Conectar a Redis
docker exec -it zelda-redis redis-cli

# Ver todas las claves
KEYS *

# Ver claves de cache específico
KEYS weapons::*
KEYS weaponsList::*

# Ver contenido de una clave
GET weapons::123e4567-e89b-12d3-a456-426614174000

# Ver TTL de una clave
TTL weapons::123e4567-e89b-12d3-a456-426614174000

# Estadísticas del servidor
INFO memory
INFO stats
```

### Logs en Tiempo Real

```bash
# Ver logs de cache en la aplicación
docker-compose logs -f zelda-codex-api | grep -i cache

# Monitorear Redis
docker exec zelda-redis redis-cli MONITOR
```

## 🎯 Beneficios de Performance

### Antes del Cache (SOAP directo)
- **Latencia promedio**: 200-500ms por consulta
- **Carga SOAP**: 100% de consultas van al backend
- **Escalabilidad**: Limitada por capacidad del servicio SOAP

### Después del Cache (Redis)
- **Latencia Cache HIT**: 5-20ms por consulta  
- **Latencia Cache MISS**: 200-500ms (primera vez) + cache para siguientes
- **Reducción de carga SOAP**: 70-90% menos consultas al backend
- **Escalabilidad**: Mejorada significativamente para consultas repetidas

### Casos de Uso Optimizados
- ✅ **Consultas frecuentes** del mismo arma
- ✅ **Listados paginados** repetidos (ej: primera página)
- ✅ **APIs públicas** con muchos consumidores
- ✅ **Dashboards** que refrescan datos periódicamente

## 🔧 Configuración Avanzada

### TTL Dinámico por Tipo de Cache
```java
@Bean
public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    return RedisCacheManager.builder(connectionFactory)
        .withCacheConfiguration("weapons", 
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)))
        .withCacheConfiguration("weaponsList",
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
        .build();
}
```

### Cache Condicional
```java
@Cacheable(value = "weapons", key = "#id", 
           condition = "#id != null", 
           unless = "#result == null")
public Weapon getWeaponById(UUID id) {
    return weaponGateway.getWeaponById(id);
}
```

---

Este sistema de cache Redis proporciona una mejora significativa en el performance de la API, especialmente importante cuando se consume un servicio SOAP que puede tener mayor latencia que consultas directas a base de datos.