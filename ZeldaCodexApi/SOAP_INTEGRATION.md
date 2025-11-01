# Integración SOAP - ZeldaCodexApi

## Descripción

ZeldaCodexApi funciona como gateway REST que consume el servicio SOAP ZeldaWeaponsApi para todas las operaciones de armas. Esta arquitectura separa la presentación REST de la lógica de negocio SOAP.

## Arquitectura

```
Cliente REST → ZeldaCodexApi (Gateway) → ZeldaWeaponsApi (SOAP) → PostgreSQL
```

## Mapeo de Operaciones

| REST | HTTP | Endpoint | SOAP Operation | Descripción |
|------|------|----------|----------------|-------------|
| Obtener arma | GET | `/weapons/{id}` | `getWeapon` | Consulta una arma específica |
| Listar armas | GET | `/weapons` | `getAllWeapons` | Lista paginada de armas |
| Crear arma | POST | `/weapons` | `createWeapon` | Crea nueva arma |
| Actualizar | PUT | `/weapons/{id}` | `updateWeapon` | Actualización completa |
| Actualizar parcial | PATCH | `/weapons/{id}` | `updateWeapon` | Actualización parcial |
| Eliminar | DELETE | `/weapons/{id}` | `deleteWeapon` | Elimina arma |

## Configuración SOAP

### WebServiceTemplate
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

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.zelda.codex.soap");
        return marshaller;
    }
}
```

### Generación de Clases SOAP
```xml
<plugin>
    <groupId>org.jvnet.jaxb2.maven2</groupId>
    <artifactId>maven-jaxb2-plugin</artifactId>
    <version>0.15.3</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <schemaLanguage>WSDL</schemaLanguage>
        <generatePackage>com.zelda.codex.soap</generatePackage>
        <schemas>
            <schema>
                <url>http://localhost:8081/ws/weapons.wsdl</url>
            </schema>
        </schemas>
    </configuration>
</plugin>
```

## Mapeo de Datos

### WeaponMapper
```java
@Component
public class WeaponMapper {
    
    public Weapon soapToModel(com.zelda.codex.soap.Weapon soapWeapon) {
        if (soapWeapon == null) return null;
        
        return Weapon.builder()
            .id(UUID.fromString(soapWeapon.getId()))
            .name(soapWeapon.getName())
            .weaponType(WeaponType.valueOf(soapWeapon.getWeaponType().name()))
            .damage(soapWeapon.getDamage())
            .durability(soapWeapon.getDurability())
            .element(mapSoapElement(soapWeapon.getElement()))
            .build();
    }
    
    public com.zelda.codex.soap.WeaponInput modelToSoapInput(CreateWeaponRequest request) {
        com.zelda.codex.soap.WeaponInput soapInput = new com.zelda.codex.soap.WeaponInput();
        soapInput.setName(request.getName());
        soapInput.setWeaponType(com.zelda.codex.soap.WeaponType.valueOf(request.getWeaponType().name()));
        soapInput.setDamage(request.getDamage());
        soapInput.setDurability(request.getDurability());
        soapInput.setElement(mapModelElement(request.getElement()));
        return soapInput;
    }
}
```

## Cliente SOAP (WeaponGateway)

### Implementación
```java
@Component
public class WeaponGateway implements IWeaponGateway {
    
    private final WebServiceTemplate webServiceTemplate;
    private final WeaponMapper weaponMapper;
    
    @Override
    public List<Weapon> getAllWeapons() {
        GetAllWeaponsRequest request = new GetAllWeaponsRequest();
        GetAllWeaponsResponse response = (GetAllWeaponsResponse) 
            webServiceTemplate.marshalSendAndReceive(request);
        
        return response.getWeapons().stream()
            .map(weaponMapper::soapToModel)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Weapon> getWeaponById(UUID id) {
        try {
            GetWeaponRequest request = new GetWeaponRequest();
            request.setId(id.toString());
            
            GetWeaponResponse response = (GetWeaponResponse) 
                webServiceTemplate.marshalSendAndReceive(request);
                
            return Optional.of(weaponMapper.soapToModel(response.getWeapon()));
            
        } catch (SoapFaultClientException ex) {
            if (ex.getMessage().contains("Weapon not found")) {
                return Optional.empty();
            }
            throw new SoapServiceException("Error getting weapon by ID: " + id, ex);
        }
    }
    
    @Override
    public Weapon createWeapon(CreateWeaponRequest createRequest) {
        CreateWeaponRequest soapRequest = new CreateWeaponRequest();
        soapRequest.setWeaponInput(weaponMapper.modelToSoapInput(createRequest));
        
        CreateWeaponResponse response = (CreateWeaponResponse) 
            webServiceTemplate.marshalSendAndReceive(soapRequest);
            
        return weaponMapper.soapToModel(response.getWeapon());
    }
}
```

## Manejo de Errores

### Excepciones Personalizadas
```java
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class SoapServiceException extends RuntimeException {
    public SoapServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WeaponNotFoundException extends RuntimeException {
    public WeaponNotFoundException(String message) {
        super(message);
    }
}
```

### Manejo Global de Errores
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(SoapServiceException.class)
    public ResponseEntity<ErrorResponse> handleSoapServiceException(SoapServiceException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse("SOAP service error", ex.getMessage()));
    }
    
    @ExceptionHandler(WebServiceIOException.class)
    public ResponseEntity<ErrorResponse> handleWebServiceIOException(WebServiceIOException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse("SOAP service unavailable", "Unable to connect to SOAP service"));
    }
}
```

## Configuración

### application.properties
```properties
# SOAP Service Configuration
zelda.weapons.soap.url=http://localhost:8081/ws
zelda.weapons.soap.timeout.connection=5000
zelda.weapons.soap.timeout.read=10000

# SOAP Logging (for debugging)
logging.level.org.springframework.ws=DEBUG
logging.level.com.zelda.codex.gateways=DEBUG
```

### Docker Compose Integration
```yaml
services:
  zelda-codex-api:
    build: .
    ports:
      - "8082:8082"
    depends_on:
      - zelda-weapons-api
    environment:
      - SOAP_SERVICE_URL=http://zelda-weapons-api:8081/ws
      
  zelda-weapons-api:
    image: demonik23/zelda-weapons-api:latest
    ports:
      - "8081:8081"
    depends_on:
      - postgres
```

## Testing

### Validar Conexión SOAP
```bash
# Verificar WSDL disponible
curl http://localhost:8081/ws/weapons.wsdl

# Probar endpoint SOAP directamente
curl -X POST http://localhost:8081/ws \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0"?>
      <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
        <soap:Body>
          <getAllWeaponsRequest xmlns="http://zelda.weapons.soap"/>
        </soap:Body>
      </soap:Envelope>'
```

### Logs de Debug
```bash
# Ver logs de integración SOAP
docker-compose logs -f zelda-codex-api | grep -i soap

# Ver logs del servicio SOAP
docker-compose logs -f zelda-weapons-api
```

## Troubleshooting

**Error: Connection refused**
- Verificar que ZeldaWeaponsApi esté ejecutándose
- Verificar configuración de URL en application.properties

**Error: SOAP Fault**
- Revisar logs para detalles del error SOAP
- Verificar formato de datos enviados

**Error: Timeout**
- Incrementar timeouts en configuración
- Verificar performance del servicio SOAP