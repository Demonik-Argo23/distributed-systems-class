# 🎯 Guía Visual de Ejecución - Sin Maven Local

## 📦 Lo que TIENES

- ✅ Docker instalado
- ✅ Python instalado (para generar código gRPC localmente, opcional)
- ✅ Código fuente completo

## ❌ Lo que NO necesitas

- ❌ Maven instalado localmente
- ❌ Java JDK local (opcional, solo para desarrollo)
- ❌ Compilar manualmente

## 🚀 Flujo de Ejecución

```
┌─────────────────────────────────────────────┐
│  1. TÚ ejecutas: start-all.bat              │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│  2. Docker Compose lee docker-compose.yml   │
│     y construye las imágenes                │
└──────────────────┬──────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
┌──────────────────┐  ┌──────────────────────┐
│  BUILD JAVA      │  │  BUILD PYTHON        │
│  (Dockerfile)    │  │  (Dockerfile)        │
│                  │  │                      │
│  1. Instala Maven│  │  1. Instala pip      │
│  2. Descarga deps│  │  2. Instala grpcio   │
│  3. Copia .proto │  │  3. Copia .proto     │
│  4. GENERA gRPC  │  │  4. GENERA gRPC      │
│  5. Compila Java │  │  5. Lista para correr│
│  6. Crea JAR     │  │                      │
└──────┬───────────┘  └──────┬───────────────┘
       │                     │
       ▼                     ▼
┌──────────────────┐  ┌─────────────────────┐
│  Imagen Java     │  │  Imagen Python      │
│  Con JAR listo   │  │  Con código gRPC    │
└──────┬───────────┘  └──────┬──────────────┘
       │                     │
       └──────────┬──────────┘
                  │
                  ▼
         ┌────────────────────┐
         │  Servicios Running │
         │                    │
         │  ✅ PostgreSQL     │
         │  ✅ MongoDB        │
         │  ✅ Redis          │
         │  ✅ Hydra          │
         │  ✅ SOAP API       │
         │  ✅ gRPC Service   │
         │  ✅ REST API       │
         └────────────────────┘
```

## 🎬 Paso a Paso

### Paso 1: Ir a la carpeta correcta
```cmd
cd ZeldaCodexApi
```

### Paso 2: Ejecutar el script
```cmd
start-all.bat
```

### Paso 3: Esperar (2-5 minutos primera vez)

Docker hará TODO automáticamente:

```
⏳ Descargando imágenes base...
⏳ Instalando Maven dentro del contenedor...
⏳ Descargando dependencias Java...
⏳ Generando código gRPC desde .proto...
⏳ Compilando código Java...
⏳ Construyendo servicio Python...
⏳ Generando código gRPC Python desde .proto...
⏳ Iniciando bases de datos...
⏳ Iniciando servicios...
✅ LISTO!
```

### Paso 4: Verificar
```cmd
docker ps
```

Debes ver 7 contenedores corriendo.

### Paso 5: Probar
```bash
curl "http://localhost:8082/api/v1/characters/by-game/Breath%20of%20the%20Wild"
```

## 🔄 Qué Hace Docker Internamente

### Para Java (ZeldaCodexApi)

```dockerfile
# Dockerfile hace esto:

1. FROM maven:3.9-eclipse-temurin-17
   → Imagen con Maven YA instalado

2. COPY pom.xml
   → Copia configuración de dependencias

3. RUN mvn dependency:go-offline
   → Descarga TODAS las dependencias (incluyendo gRPC)

4. COPY src
   → Copia código fuente + archivo .proto

5. RUN mvn clean package
   → Maven ejecuta el plugin protobuf
   → GENERA: characters_pb2.java, CharacterServiceGrpc.java
   → COMPILA: Todo el código Java
   → CREA: archivo .jar

6. FROM openjdk:17-jdk-slim
   → Nueva imagen más pequeña

7. COPY --from=build /app/target/*.jar
   → Copia solo el JAR final

8. ENTRYPOINT ["java", "-jar", "app.jar"]
   → Listo para ejecutar
```

### Para Python (ZeldaCharactersApi)

```dockerfile
# Dockerfile hace esto:

1. FROM python:3.11-slim
   → Imagen con Python YA instalado

2. COPY requirements.txt
   → Copia lista de dependencias

3. RUN pip install -r requirements.txt
   → Instala grpcio, grpcio-tools, pymongo

4. COPY proto/
   → Copia archivo .proto

5. RUN python -m grpc_tools.protoc -I./proto \
       --python_out=./src --grpc_python_out=./src \
       ./proto/characters.proto
   → GENERA: characters_pb2.py, characters_pb2_grpc.py

6. COPY src/
   → Copia código Python

7. CMD ["python", "src/server.py"]
   → Listo para ejecutar
```

## 🐛 Sobre los Errores que Ves

### Errores en VS Code (JAVA)

```
❌ Cannot resolve symbol 'CharacterServiceGrpc'
❌ Cannot find package 'com.zelda.codex.grpc'
```

**¿Por qué?** 
- El código gRPC NO existe en tu disco local
- Solo existe DENTRO del contenedor Docker

**¿Problema?**
- ❌ NO es un problema
- ✅ El código compila y funciona en Docker
- ✅ VS Code no puede ver dentro del contenedor

**Solución:**
- Ejecutar `build-with-docker.bat` si quieres ver el código generado localmente
- O ignorar los errores (funcionará igual)

### Errores en VS Code (PYTHON)

```
❌ Import "characters_pb2" could not be resolved
❌ Import "characters_pb2_grpc" could not be resolved
```

**¿Por qué?**
- Los archivos NO existen aún
- Se generan al ejecutar setup.bat o Docker

**Solución:**
- Ejecutar `setup.bat` para generarlos localmente
- O ejecutar Docker (los genera automáticamente)

## ✅ Verificación Final

### ¿Cómo sé que todo funciona?

```cmd
# 1. Ver contenedores
docker ps

# Debes ver:
# zelda-codex-api          → Java REST API compilada ✅
# zelda-characters-grpc    → Python gRPC con código generado ✅
# zelda-weapons-api        → Java SOAP API ✅

# 2. Probar REST API
curl http://localhost:8082/swagger-ui.html

# 3. Probar gRPC a través de REST
curl "http://localhost:8082/api/v1/characters/by-game/Breath%20of%20the%20Wild"

# Si ves JSON con personajes → TODO FUNCIONA ✅
```

## 📊 Comparación: Maven Local vs Docker

| Aspecto | Con Maven Local | Con Docker |
|---------|-----------------|------------|
| Instalación | Descargar Maven, configurar JAVA_HOME | Solo Docker |
| Compilar | `mvn clean package` | `docker-compose build` |
| Ejecutar | `mvn spring-boot:run` | `docker-compose up` |
| Dependencias | Se descargan a ~/.m2 | Se descargan en el contenedor |
| Código gRPC | Se genera en target/ | Se genera en el contenedor |
| Problemas | Conflictos de versiones Java/Maven | Aislado, siempre funciona |
| Limpieza | Difícil de limpiar | `docker-compose down` |

## 🎯 Comandos de Uso Diario

```bash
# Iniciar todo
cd ZeldaCodexApi
start-all.bat

# Ver logs
docker-compose logs -f

# Reiniciar un servicio
docker-compose restart zelda-codex-api

# Detener todo
docker-compose down

# Detener y limpiar
docker-compose down -v

# Reconstruir si cambias código
docker-compose build zelda-codex-api
docker-compose up -d zelda-codex-api
```

## 💡 Tips Finales

1. **Primera vez**: Tarda 2-5 minutos (descarga dependencias)
2. **Siguientes veces**: Tarda 30 segundos (usa cache)
3. **Cambios en código**: Necesitas reconstruir con `docker-compose build`
4. **Errores en VS Code**: Ignóralos, Docker se encarga
5. **Ver código generado**: Opcional, usa `build-with-docker.bat`

---

**Resumen**: Ejecutas `start-all.bat`, esperas 2-5 minutos, y TODO funciona. Docker compila e inicia automáticamente.
