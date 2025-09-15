# Zelda Weapons API - SOAP Web Service

API SOAP para gestionar armas de *The Legend of Zelda: Breath of the Wild* desarrollada con Spring Boot, JPA/Hibernate y PostgreSQL.

## Operaciones Disponibles

- **createWeapon** - Crear nueva arma
- **getWeapon** - Obtener arma por ID
- **deleteWeapon** - Eliminar arma por ID

## Tecnologías

- **Java 17**
- **Spring Boot 3.1.0**
- **Spring Web Services (SOAP)**
- **JPA/Hibernate**
- **PostgreSQL 15**
- **Flyway** (migraciones)
- **Docker & Docker Compose**
- **JAXB** (XML binding)

## Prerequisitos

- **Docker** instalado
- **Puerto 8081** disponible (API)
- **Puerto 5432** disponible (PostgreSQL)

## Instrucciones de Ejecución

### 1. Clonar el repositorio
```bash
git clone <https://github.com/Demonik-Argo23/distributed-systems-class.git>
cd ZeldaApi
```

### 2. Ejecutar con Docker
```bash
# Construir e iniciar servicios (PostgreSQL + API)
docker-compose up -d

# Verificar que los servicios están corriendo
docker-compose ps
```

### 3. Verificar funcionamiento
- **WSDL**: http://localhost:8081/ws/weapons.wsdl

## Pruebas con Insomnia

### Importar WSDL:
1. Ve a **Application → Preferences → Data**
2. **Import Data** → **From URL**
3. Pegar: `http://localhost:8081/ws/weapons.wsdl`
4. Esto importará automáticamente las 3 operaciones

### Enums disponibles para valores de las armas:
- **WeaponType**: Bat, Boomerang, Club, Hammer, Rod, Spear, OneHandedSword, TwoHandedSword, Other
- **Element**: Fire, Ice, Electric

## Ejemplos de Uso

### Crear Arma (createWeapon)
```xml
POST http://localhost:8081/ws/weapons.wsdl
Content-Type: text/xml; charset=utf-8
SOAPAction: ""

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
 <soapenv:Header>
 </soapenv:Header>
 <soapenv:Body>
  <sch:createWeaponRequest xmlns:sch="http://weapons.zelda.com/ws">
   <sch:weapon>
    <sch:name>Zora Spear</sch:name>
    <sch:type>Spear</sch:type>
    <sch:damage>12</sch:damage>
    <sch:durability>50</sch:durability>
    <sch:element></sch:element>
   </sch:weapon>
  </sch:createWeaponRequest>
 </soapenv:Body>
</soapenv:Envelope>
```

### Obtener Arma (getWeapon)
```xml
POST http://localhost:8081/ws/weapons.wsdl
Content-Type: text/xml; charset=utf-8
SOAPAction: ""

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
 <soapenv:Header>
 </soapenv:Header>
 <soapenv:Body>
  <sch:getWeaponRequest xmlns:sch="http://weapons.zelda.com/ws">
   <sch:id>1</sch:id>
  </sch:getWeaponRequest>
 </soapenv:Body>
</soapenv:Envelope>
```

### Eliminar Arma (deleteWeapon)
```xml
POST http://localhost:8081/ws/weapons.wsdl
Content-Type: text/xml; charset=utf-8
SOAPAction: ""

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
 <soapenv:Header>
 </soapenv:Header>
 <soapenv:Body>
  <sch:deleteWeaponRequest xmlns:sch="http://weapons.zelda.com/ws">
   <sch:id>1</sch:id>
  </sch:deleteWeaponRequest>
 </soapenv:Body>
</soapenv:Envelope>
```

### Error: Clases JAXB no encontradas
```bash
# Regenerar clases desde XSD
docker-compose run --rm app mvn jaxb2:xjc
```

## Notas

- Las **clases JAXB** se generan automáticamente desde `weapons.xsd` usando Maven
- Las **migraciones** de base de datos se ejecutan automáticamente con Flyway
- El **WSDL** se genera dinámicamente desde el XSD
- La aplicación incluye **validaciones** y **manejo de excepciones** completo

## Para pruebas

**Algunos tipos de Arma en Zelda:**
- Master Sword (OneHandedSword)
- Biggoron's Sword (TwoHandedSword) 
- Fire Rod (Rod)
- Boomerang (Boomerang)
- Sledgehammer (Hammer)

**Elementos disponibles:**
- Fire (fuego)
- Ice (hielo) 
- Electric (eléctrico)
-**El elemento puede ser null para armas no elementales**
---
