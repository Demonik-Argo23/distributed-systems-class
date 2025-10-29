package com.zelda.codex.gateways;

import com.zelda.codex.exceptions.WeaponNotFoundException;
import com.zelda.codex.models.Weapon;
import com.zelda.codex.models.Element;
import com.zelda.codex.models.WeaponType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.namespace.QName;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WeaponGateway implements IWeaponGateway {

    @Value("${zelda.weapons.soap.url:http://localhost:8081/ws}")
    private String soapServiceUrl;

    private final WebServiceTemplate webServiceTemplate;

    public WeaponGateway() {
        this.webServiceTemplate = new WebServiceTemplate();
    }

    @Override
    public Weapon getWeaponById(UUID id) {
        try {
            webServiceTemplate.setDefaultUri(soapServiceUrl);
            
            // Crear request SOAP (esto se actualizará cuando tengamos las clases generadas)
            // GetWeaponRequest request = new GetWeaponRequest();
            // request.setId(id.toString());
            
            // GetWeaponResponse response = (GetWeaponResponse) webServiceTemplate.marshalSendAndReceive(request);
            
            // Por ahora, simulamos la respuesta para que compile
            return mockWeaponResponse(id);
            
        } catch (Exception ex) {
            throw new WeaponNotFoundException(id);
        }
    }

    @Override
    public Weapon createWeapon(Weapon weapon) {
        try {
            webServiceTemplate.setDefaultUri(soapServiceUrl);
            
            // Crear request SOAP (esto se actualizará cuando tengamos las clases generadas)
            // CreateWeaponRequest request = new CreateWeaponRequest();
            // ... mapear weapon a SOAP request
            
            // CreateWeaponResponse response = (CreateWeaponResponse) webServiceTemplate.marshalSendAndReceive(request);
            
            // Por ahora, simulamos la respuesta
            weapon.setId(UUID.randomUUID());
            return weapon;
            
        } catch (Exception ex) {
            throw new RuntimeException("Error creando arma: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean deleteWeapon(UUID id) {
        try {
            webServiceTemplate.setDefaultUri(soapServiceUrl);
            
            // Crear request SOAP (esto se actualizará cuando tengamos las clases generadas)
            // DeleteWeaponRequest request = new DeleteWeaponRequest();
            // request.setId(id.toString());
            
            // DeleteWeaponResponse response = (DeleteWeaponResponse) webServiceTemplate.marshalSendAndReceive(request);
            // return response.isSuccess();
            
            // Por ahora, simulamos éxito
            return true;
            
        } catch (Exception ex) {
            throw new WeaponNotFoundException(id);
        }
    }

    @Override
    public Page<Weapon> getAllWeapons(Pageable pageable, Map<String, String> filters) {
        try {
            webServiceTemplate.setDefaultUri(soapServiceUrl);
            
            // Crear request SOAP (esto se actualizará cuando tengamos las clases generadas)
            // GetAllWeaponsRequest request = new GetAllWeaponsRequest();
            // request.setPage(pageable.getPageNumber());
            // request.setSize(pageable.getPageSize());
            // ... configurar filtros y ordenamiento
            
            // GetAllWeaponsResponse response = (GetAllWeaponsResponse) webServiceTemplate.marshalSendAndReceive(request);
            
            // Por ahora, simulamos respuesta paginada
            List<Weapon> mockWeapons = generateMockWeapons(pageable, filters);
            long total = 50; // Simular total de armas
            
            return new PageImpl<>(mockWeapons, pageable, total);
            
        } catch (Exception ex) {
            throw new RuntimeException("Error obteniendo armas: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Weapon replaceWeapon(UUID id, Weapon weapon) {
        try {
            webServiceTemplate.setDefaultUri(soapServiceUrl);
            
            // Crear request SOAP (esto se actualizará cuando tengamos las clases generadas)
            // ReplaceWeaponRequest request = new ReplaceWeaponRequest();
            // request.setId(id.toString());
            // ... mapear weapon a SOAP request
            
            // ReplaceWeaponResponse response = (ReplaceWeaponResponse) webServiceTemplate.marshalSendAndReceive(request);
            
            // Por ahora, simulamos la respuesta
            weapon.setId(id);
            return weapon;
            
        } catch (Exception ex) {
            throw new WeaponNotFoundException(id);
        }
    }

    @Override
    public Weapon updateWeapon(UUID id, Map<String, Object> updates) {
        try {
            webServiceTemplate.setDefaultUri(soapServiceUrl);
            
            // Crear request SOAP (esto se actualizará cuando tengamos las clases generadas)
            // UpdateWeaponRequest request = new UpdateWeaponRequest();
            // request.setId(id.toString());
            // ... mapear updates a SOAP request
            
            // UpdateWeaponResponse response = (UpdateWeaponResponse) webServiceTemplate.marshalSendAndReceive(request);
            
            // Por ahora, simulamos la actualización
            Weapon existingWeapon = mockWeaponResponse(id);
            applyUpdatesToWeapon(existingWeapon, updates);
            return existingWeapon;
            
        } catch (Exception ex) {
            throw new WeaponNotFoundException(id);
        }
    }

    // Método temporal para simular respuesta mientras no tenemos las clases SOAP generadas
    private Weapon mockWeaponResponse(UUID id) {
        Weapon weapon = new Weapon();
        weapon.setId(id);
        weapon.setName("Master Sword");
        weapon.setWeaponType(WeaponType.ONE_HANDED_SWORD);
        weapon.setDamage(30);
        weapon.setDurability(200);
        weapon.setElement(null);
        return weapon;
    }

    // Método temporal para generar armas mock con paginación
    private List<Weapon> generateMockWeapons(Pageable pageable, Map<String, String> filters) {
        List<Weapon> allWeapons = Arrays.asList(
            createMockWeapon(UUID.randomUUID(), "Master Sword", WeaponType.ONE_HANDED_SWORD, 30, 200),
            createMockWeapon(UUID.randomUUID(), "Royal Claymore", WeaponType.TWO_HANDED_SWORD, 52, 40),
            createMockWeapon(UUID.randomUUID(), "Ancient Spear", WeaponType.SPEAR, 30, 25),
            createMockWeapon(UUID.randomUUID(), "Traveler's Sword", WeaponType.ONE_HANDED_SWORD, 5, 22),
            createMockWeapon(UUID.randomUUID(), "Bokoblin Club", WeaponType.CLUB, 4, 12),
            createMockWeapon(UUID.randomUUID(), "Korok Leaf", WeaponType.OTHER, 1, 25),
            createMockWeapon(UUID.randomUUID(), "Throwing Spear", WeaponType.SPEAR, 8, 15),
            createMockWeapon(UUID.randomUUID(), "Iron Sledgehammer", WeaponType.HAMMER, 12, 40)
        );

        // Aplicar filtros si existen
        List<Weapon> filteredWeapons = allWeapons;
        if (filters != null && filters.containsKey("weaponType")) {
            String typeFilter = filters.get("weaponType").toUpperCase();
            filteredWeapons = allWeapons.stream()
                .filter(weapon -> weapon.getWeaponType().name().equals(typeFilter))
                .collect(Collectors.toList());
        }

        // Aplicar paginación
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredWeapons.size());
        
        if (start >= filteredWeapons.size()) {
            return new ArrayList<>();
        }
        
        return filteredWeapons.subList(start, end);
    }

    private Weapon createMockWeapon(UUID id, String name, WeaponType type, Integer damage, Integer durability) {
        Weapon weapon = new Weapon();
        weapon.setId(id);
        weapon.setName(name);
        weapon.setWeaponType(type);
        weapon.setDamage(damage);
        weapon.setDurability(durability);
        weapon.setElement(null);
        return weapon;
    }

    private void applyUpdatesToWeapon(Weapon weapon, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key.toLowerCase()) {
                case "name":
                    weapon.setName((String) value);
                    break;
                case "damage":
                    weapon.setDamage(Integer.valueOf(value.toString()));
                    break;
                case "durability":
                    weapon.setDurability(Integer.valueOf(value.toString()));
                    break;
                case "weapontype":
                    weapon.setWeaponType(WeaponType.valueOf(value.toString().toUpperCase()));
                    break;
                case "element":
                    if (value != null) {
                        weapon.setElement(Element.valueOf(value.toString().toUpperCase()));
                    } else {
                        weapon.setElement(null);
                    }
                    break;
            }
        });
    }
}