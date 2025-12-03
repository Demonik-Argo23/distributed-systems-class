# Sistema de Cache Redis - ZeldaCodexApi

## Descripción

El sistema implementa Redis para mejorar el performance de las operaciones de consulta (GET), reduciendo la latencia y la carga sobre el servicio SOAP backend.

## Arquitectura de Cache

```
Cliente REST → ZeldaCodexApi → Cache Redis → ZeldaWeaponsApi (SOAP) → PostgreSQL
                    ↓              ↓
                Cache HIT     Cache MISS
                    ↓              ↓
               Respuesta      Consulta SOAP
               Inmediata      + Guardar Cache
```

## Configuración

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
      interval: 30s
      timeout: 10s
      retries: 5
    command: redis-server --appendonly yes
```

### Spring Boot Configuration (RedisConfig)
```java
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(
            new RedisStandaloneConfiguration("localhost", 6379)
        );
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Configurar serialización JSON
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        return template;
    }

    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))  // TTL: 10 minutos
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfig)
            .build();
    }
}
```

### application.properties
```properties
# Redis Configuration
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0

# Cache Configuration
spring.cache.type=redis
spring.cache.redis.time-to-live=600000  # 10 minutos
spring.cache.redis.cache-null-values=false

# Para Docker
spring.profiles.active=${SPRING_PROFILES_ACTIVE:default}
```

## Estrategia de Cache

### Operaciones Cacheadas

| Operación | Cache Key | TTL | Estrategia |
|-----------|-----------|-----|------------|
| `GET /weapons` | `weapons:all` | 10 min | Cache-Aside |
| `GET /weapons/{id}` | `weapon:{id}` | 10 min | Cache-Aside |
| `GET /weapons?page={p}&size={s}` | `weapons:page:{p}:size:{s}` | 10 min | Cache-Aside |

### Invalidación de Cache

| Operación | Invalidación |
|-----------|--------------|
| `POST /weapons` | Elimina `weapons:all` y claves de paginación |
| `PUT /weapons/{id}` | Elimina `weapon:{id}`, `weapons:all` y paginación |
| `PATCH /weapons/{id}` | Elimina `weapon:{id}`, `weapons:all` y paginación |
| `DELETE /weapons/{id}` | Elimina `weapon:{id}`, `weapons:all` y paginación |

## Implementación

### Anotaciones de Cache en Service
```java
@Service
@Transactional
public class WeaponService implements IWeaponService {

    @Override
    @Cacheable(value = "weapons", key = "'all'")
    public List<WeaponResponse> getAllWeapons() {
        List<Weapon> weapons = weaponGateway.getAllWeapons();
        return weapons.stream()
            .map(this::toWeaponResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "weapons", key = "#id.toString()")
    public WeaponResponse getWeaponById(UUID id) {
        Optional<Weapon> weapon = weaponGateway.getWeaponById(id);
        if (weapon.isEmpty()) {
            throw new WeaponNotFoundException("Weapon not found with ID: " + id);
        }
        return toWeaponResponse(weapon.get());
    }

    @Override
    @Cacheable(value = "weapons", key = "'page:' + #page + ':size:' + #size")
    public Page<WeaponResponse> getWeaponsPage(int page, int size) {
        // Implementación de paginación con cache
    }

    @Override
    @CacheEvict(value = "weapons", allEntries = true)
    public WeaponResponse createWeapon(CreateWeaponRequest request) {
        Weapon createdWeapon = weaponGateway.createWeapon(request);
        return toWeaponResponse(createdWeapon);
    }

    @Override
    @CacheEvict(value = "weapons", key = "#id.toString()")
    @CacheEvict(value = "weapons", key = "'all'")
    public WeaponResponse updateWeapon(UUID id, ReplaceWeaponRequest request) {
        Weapon updatedWeapon = weaponGateway.updateWeapon(id, request);
        return toWeaponResponse(updatedWeapon);
    }

    @Override
    @CacheEvict(value = "weapons", key = "#id.toString()")
    @CacheEvict(value = "weapons", key = "'all'")
    public void deleteWeapon(UUID id) {
        weaponGateway.deleteWeapon(id);
    }
}
```

### Controller de Cache Info
```java
@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getCacheInfo() {
        Map<String, Object> cacheInfo = new HashMap<>();
        
        // Estadísticas de Redis
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        Properties info = connection.info();
        
        cacheInfo.put("redis_version", info.getProperty("redis_version"));
        cacheInfo.put("used_memory_human", info.getProperty("used_memory_human"));
        cacheInfo.put("connected_clients", info.getProperty("connected_clients"));
        cacheInfo.put("total_commands_processed", info.getProperty("total_commands_processed"));
        
        // Claves de cache
        Set<String> keys = redisTemplate.keys("weapon*");
        cacheInfo.put("cached_weapons", keys != null ? keys.size() : 0);
        cacheInfo.put("cache_keys", keys);
        
        connection.close();
        return ResponseEntity.ok(cacheInfo);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCache() {
        cacheManager.getCacheNames().forEach(cacheName -> 
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        return ResponseEntity.ok("Cache cleared successfully");
    }
}
```

## Monitoreo y Métricas

### Logs de Cache
```properties
# Logging para cache
logging.level.org.springframework.cache=DEBUG
logging.level.org.springframework.data.redis=DEBUG
logging.level.com.zelda.codex.services=DEBUG
```

### Métricas de Performance
```bash
# Conectar a Redis y ver estadísticas
docker exec -it zelda-redis redis-cli

# Ver información de memoria
INFO memory

# Ver claves de cache actuales
KEYS weapon*

# Ver estadísticas de comandos
INFO commandstats

# Monitorear comandos en tiempo real
MONITOR
```

## Testing

### Verificar Funcionamiento del Cache
```bash
# 1. Limpiar cache
curl -X DELETE http://localhost:8082/api/v1/cache/clear

# 2. Primera consulta (cache MISS - lenta)
time curl -H "Authorization: Bearer $READ_TOKEN" \
  http://localhost:8082/api/v1/weapons

# 3. Segunda consulta (cache HIT - rápida)
time curl -H "Authorization: Bearer $READ_TOKEN" \
  http://localhost:8082/api/v1/weapons

# 4. Ver información de cache
curl http://localhost:8082/api/v1/cache/info
```

### Verificar Invalidación
```bash
# 1. Consultar arma (cache)
curl -H "Authorization: Bearer $READ_TOKEN" \
  http://localhost:8082/api/v1/weapons/some-id

# 2. Actualizar arma (invalida cache)
curl -X PUT -H "Authorization: Bearer $WRITE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Sword","weaponType":"ONE_HANDED_SWORD","damage":35,"durability":45}' \
  http://localhost:8082/api/v1/weapons/some-id

# 3. Consultar nuevamente (cache MISS)
curl -H "Authorization: Bearer $READ_TOKEN" \
  http://localhost:8082/api/v1/weapons/some-id
```

## Troubleshooting

### Problemas Comunes

**Error: Unable to connect to Redis**
```bash
# Verificar que Redis esté ejecutándose
docker-compose ps redis
docker exec zelda-redis redis-cli ping
```

**Cache no se está utilizando**
```bash
# Verificar logs de Spring Cache
docker-compose logs zelda-codex-api | grep -i cache

# Verificar configuración de cache
curl http://localhost:8082/api/v1/cache/info
```

**Performance no mejora**
```bash
# Verificar que las claves se están creando
docker exec zelda-redis redis-cli KEYS "*"

# Monitor en tiempo real
docker exec zelda-redis redis-cli MONITOR
```

### Configuración de Desarrollo
```properties
# Para desarrollo local (sin Docker)
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Para debugging
logging.level.org.springframework.cache=TRACE
```