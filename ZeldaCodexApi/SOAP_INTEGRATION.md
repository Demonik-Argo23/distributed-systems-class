# Integración SOAP - ZeldaCodexApi con ZeldaWeaponsApi

## 📋 Descripción General

La API REST **ZeldaCodexApi** funciona como un gateway que consume el servicio SOAP **ZeldaWeaponsApi** para todas las operaciones relacionadas con armas. Esta arquitectura permite separar la lógica de presentación REST de la lógica de negocio SOAP.

## 🔄 Flujo de Integración

### Arquitectura de Componentes

```
Cliente REST → ZeldaCodexApi (Gateway) → ZeldaWeaponsApi (SOAP) → PostgreSQL
```

### Mapeo de Operaciones

| Operación REST | Método HTTP | Endpoint REST | Operación SOAP | Descripción |
|----------------|-------------|---------------|----------------|-------------|
| Obtener arma | `GET` | `/weapons/{id}` | `getWeapon` | Consulta una arma específica |
| Listar armas | `GET` | `/weapons` | `getAllWeapons` | Obtiene lista paginada |
| Crear arma | `POST` | `/weapons` | `createWeapon` | Crea nueva arma |
| Actualizar arma | `PUT` | `/weapons/{id}` | `updateWeapon` | Actualización completa |
| Actualizar parcial | `PATCH` | `/weapons/{id}` | `updateWeapon` | Actualización parcial |
| Eliminar arma | `DELETE` | `/weapons/{id}` | `deleteWeapon` | Elimina arma |

## 🔧 Configuración de Componentes

### 1. WebServiceTemplate (SoapConfig)

```java
@Configuration
public class SoapConfig {
    
    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        template.setDefaultUri("${zelda.weapons.soap.url}");
        template.setCheckConnectionForFault(true);
        return template;
    }
}
```

### 2. WeaponMapper - Conversión de Datos

**SOAP → Modelo Interno:**
```java
public Weapon soapToModel(com.zelda.codex.soap.Weapon soapWeapon) {
    // Convierte objeto SOAP a modelo de dominio
    // Mapea enums: WeaponType, Element
    // Convierte IDs string a UUID
}
```

**Modelo Interno → SOAP:**
```java
public WeaponInput modelToSoapInput(Weapon weapon) {
    // Convierte modelo de dominio a objeto SOAP
    // Mapea enums inversos
    // Prepara para envío SOAP
}
```

### 3. WeaponGateway - Cliente SOAP

Implementa todas las operaciones con manejo robusto de errores:

```java
@Component
public class WeaponGateway implements IWeaponGateway {
    
    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    @Autowired
    private WeaponMapper weaponMapper;
    
    // Implementación de operaciones CRUD con SOAP
}
```

## 🚨 Manejo de Errores SOAP

### Mapeo de Excepciones

| Error SOAP | Excepción Interna | Código HTTP | Descripción |
|------------|-------------------|-------------|-------------|
| `SoapFaultClientException` | `SoapValidationException` | `424` | Error de validación SOAP |
| `ConnectException` | `SoapServiceUnavailableException` | `502` | Servicio no disponible |
| `SocketTimeoutException` | `SoapServiceUnavailableException` | `502` | Timeout de conexión |
| SOAP Fault "NOT_FOUND" | `WeaponNotFoundException` | `404` | Recurso no encontrado |
| SOAP Fault "ALREADY_EXISTS" | `WeaponAlreadyExistsException` | `409` | Recurso ya existe |
| Otros errores SOAP | `SoapServiceException` | `502` | Error genérico del gateway |

### Flujo de Manejo de Errores

1. **WeaponGateway** captura excepciones SOAP
2. **Mapea** a excepciones de dominio específicas
3. **GlobalExceptionHandler** intercepta y convierte a respuestas HTTP
4. **Cliente** recibe respuesta JSON estructurada con `ErrorResponse`

## 🐳 Configuración Docker

### docker-compose.yml

```yaml
services:
  zelda-codex-api:          # Gateway REST (Puerto 8082)
    build: .
    depends_on:
      - zelda-weapons-api
    environment:
      - ZELDA_WEAPONS_SOAP_URL=http://zelda-weapons-api:8081/ws

  zelda-weapons-api:        # Servicio SOAP (Puerto 8081)
    build:
      context: ../ZeldaApi
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/zelda_weapons_db

  postgres:                 # Base de datos (Puerto 5432)
    image: postgres:15-alpine
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U zelda_user -d zelda_weapons_db"]
```

## 📊 Flujo de Datos Detallado

### Ejemplo: Crear Arma

1. **Cliente → REST API:**
   ```json
   POST /api/v1/weapons
   {
     "name": "Master Sword",
     "weaponType": "ONE_HANDED_SWORD",
     "damage": 30,
     "durability": 200,
     "element": "NONE"
   }
   ```

2. **REST API → WeaponService:**
   ```java
   CreateWeaponRequest request = // JSON deserializado
   Weapon weapon = weaponMapper.fromCreateRequest(request);
   ```

3. **WeaponService → WeaponGateway:**
   ```java
   Weapon createdWeapon = weaponGateway.createWeapon(weapon);
   ```

4. **WeaponGateway → SOAP:**
   ```java
   CreateWeaponRequest soapRequest = new CreateWeaponRequest();
   WeaponInput input = weaponMapper.modelToSoapInput(weapon);
   soapRequest.setWeaponInput(input);
   
   CreateWeaponResponse soapResponse = webServiceTemplate
       .marshalSendAndReceive(soapServiceUrl, soapRequest);
   ```

5. **SOAP → PostgreSQL:**
   ```xml
   <soap:Envelope>
     <soap:Body>
       <createWeaponRequest>
         <weaponInput>
           <name>Master Sword</name>
           <weaponType>ONE_HANDED_SWORD</weaponType>
           <damage>30</damage>
           <durability>200</durability>
           <element>NONE</element>
         </weaponInput>
       </createWeaponRequest>
     </soap:Body>
   </soap:Envelope>
   ```

6. **Respuesta PostgreSQL → SOAP → REST:**
   ```java
   Weapon resultWeapon = weaponMapper.soapToModel(soapResponse.getWeapon());
   WeaponResponse response = weaponMapper.toResponse(resultWeapon);
   response.setLinks(hateoasLinkService.generateWeaponLinks(resultWeapon.getId()));
   ```

## 🧪 Testing Local

### Comandos de Inicio

```bash
# 1. Iniciar todos los servicios
docker-compose up --build

# 2. Verificar servicios
curl http://localhost:8082/api/v1/weapons/info  # REST Gateway
curl http://localhost:8081/actuator/health      # SOAP Service
```

### Endpoints de Prueba

```bash
# Crear arma
curl -X POST http://localhost:8082/api/v1/weapons \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Master Sword",
    "weaponType": "ONE_HANDED_SWORD", 
    "damage": 30,
    "durability": 200,
    "element": "NONE"
  }'

# Obtener arma
curl http://localhost:8082/api/v1/weapons/{id}

# Listar armas
curl "http://localhost:8082/api/v1/weapons?page=0&size=10"
```

## 🔍 Configuración de Logs

Para monitorear la integración SOAP:

```properties
# application.properties
logging.level.com.zelda.codex=DEBUG
logging.level.org.springframework.ws=DEBUG
logging.level.com.zelda.codex.gateways.WeaponGateway=TRACE
```

### Logs Clave a Monitorear

- **SOAP Request/Response**: Payloads XML completos
- **Connection Errors**: Timeouts y conexiones fallidas  
- **Mapping Errors**: Errores de conversión SOAP ↔ Modelo
- **Business Logic**: Validaciones y reglas de negocio

## 📈 Métricas y Monitoreo

### Health Checks

```bash
# Gateway Health
curl http://localhost:8082/actuator/health

# SOAP Service Health  
curl http://localhost:8081/actuator/health

# Database Health
docker exec zelda-postgres pg_isready -U zelda_user -d zelda_weapons_db
```

### Documentación API

- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8082/api-docs
- **SOAP WSDL**: http://localhost:8081/ws/weapons.wsdl

---

Esta integración proporciona una arquitectura robusta y escalable que separa claramente las responsabilidades entre el gateway REST y el servicio SOAP, con manejo comprehensivo de errores y documentación completa del flujo de datos.