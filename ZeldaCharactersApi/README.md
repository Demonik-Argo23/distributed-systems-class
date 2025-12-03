# Zelda Characters gRPC Service

Servicio gRPC en Python para la gestión de personajes de Zelda. Este servicio es consumido por la REST API principal (ZeldaCodexApi).

**Nota**: Este servicio se levanta automáticamente al ejecutar el docker-compose principal desde `ZeldaCodexApi`. No requiere ejecución independiente.

## Descripción

Implementación de servicio gRPC en Python que gestiona personajes de Zelda, cumpliendo con los tres tipos de RPC requeridos:

- **Unary RPC**: GetCharacter, CreateCharacter, UpdateCharacter, DeleteCharacter
- **Client Streaming RPC**: CreateCharactersBatch
- **Server Streaming RPC**: ListCharactersByGame

## Stack Tecnológico

- **Python** 3.11
- **gRPC** 1.60.0 (`grpcio`, `grpcio-tools`)
- **MongoDB** 7.0
- **PyMongo** 4.6.1

## Definición del Servicio (.proto)

**Archivo**: `proto/characters.proto`

```protobuf
service CharacterService {
  rpc GetCharacter(GetCharacterRequest) returns (CharacterResponse);
  rpc CreateCharacter(CreateCharacterRequest) returns (CharacterResponse);
  rpc CreateCharactersBatch(stream CreateCharacterRequest) returns (BatchResponse);
  rpc ListCharactersByGame(ListCharactersRequest) returns (stream CharacterResponse);
  rpc UpdateCharacter(UpdateCharacterRequest) returns (CharacterResponse);
  rpc DeleteCharacter(DeleteCharacterRequest) returns (DeleteResponse);
  rpc ListCharactersByWeapon(WeaponFilterRequest) returns (CharacterListResponse);
}
```

## Validaciones

**Archivo**: `src/validators.py`

| Campo | Regla | Código de Error |
|-------|-------|-----------------|
| `name`, `email`, `game` | Obligatorio, min 2 caracteres | `INVALID_ARGUMENT` |
| `email` | Formato válido (RFC 5322) | `INVALID_ARGUMENT` |
| `health`, `stamina`, `attack`, `defense` | Rango 1-999 | `INVALID_ARGUMENT` |
| `email` | Único en la base de datos | `ALREADY_EXISTS` |
| `id` | Debe existir para operaciones de lectura/actualización | `NOT_FOUND` |

## Base de Datos

**MongoDB** - Diferente a PostgreSQL usado en el servicio SOAP de weapons

- **Puerto**: 27017
- **Base de datos**: `zelda_characters`
- **Colección**: `characters`

### Esquema

```javascript
{
  _id: ObjectId | String,
  name: String,
  email: String (unique),
  game: String,
  race: String,
  health: Number (1-999),
  stamina: Number (1-999),
  attack: Number (1-999),
  defense: Number (1-999),
  weapons: [String],
  created_at: Date,
  updated_at: Date
}
```

### Índices

```javascript
db.characters.createIndex({ email: 1 }, { unique: true })
db.characters.createIndex({ game: 1 })
db.characters.createIndex({ weapons: 1 })
```

## Configuración

### Variables de Entorno

```bash
MONGO_URI=mongodb://zelda-characters-mongodb:27017/
MONGO_DB=zelda_characters
GRPC_PORT=50051
```

## Docker

El servicio se construye automáticamente con el Dockerfile:

```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY proto/ ./proto/
RUN python -m grpc_tools.protoc -I./proto --python_out=./src --grpc_python_out=./src ./proto/characters.proto
COPY src/ ./src/
EXPOSE 50051
CMD ["python", "src/server.py"]
```


## Documentación Completa

Para ver la documentación completa del proyecto, incluyendo la integración con REST API y ejemplos de uso, consulta el README principal en `ZeldaCodexApi/README.md`.
