package com.zelda.weapons.endpoint;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.zelda.weapons.mapper.WeaponMapper;
import com.zelda.weapons.model.Weapon;
import com.zelda.weapons.service.WeaponService;
import com.zelda.weapons.validator.WeaponAlreadyExistsException;
import com.zelda.weapons.ws.CreateWeaponRequest;
import com.zelda.weapons.ws.CreateWeaponResponse;
import com.zelda.weapons.ws.DeleteWeaponRequest;
import com.zelda.weapons.ws.DeleteWeaponResponse;
import com.zelda.weapons.ws.GetWeaponRequest;
import com.zelda.weapons.ws.GetWeaponResponse;

@Endpoint
public class WeaponEndpoint {

    private static final String NAMESPACE_URI = "http://zelda.com/weapons";

    private final WeaponService weaponService;
    private final WeaponMapper weaponMapper;

    @Autowired
    public WeaponEndpoint(WeaponService weaponService, WeaponMapper weaponMapper) {
        this.weaponService = weaponService;
        this.weaponMapper = weaponMapper;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getWeaponRequest")
    @ResponsePayload
    public GetWeaponResponse getWeapon(@RequestPayload GetWeaponRequest request) {
        UUID weaponId = UUID.fromString(request.getId());
        Weapon weaponEntity = weaponService.getWeaponById(weaponId);
        com.zelda.weapons.ws.Weapon weaponSoap = weaponMapper.entityToSoap(weaponEntity);
        
        GetWeaponResponse response = new GetWeaponResponse();
        response.setWeapon(weaponSoap);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createWeaponRequest")
    @ResponsePayload
    public CreateWeaponResponse createWeapon(@RequestPayload CreateWeaponRequest request) {
        try {
            Weapon weaponEntity = weaponMapper.soapInputToEntity(request.getWeaponInput());
            Weapon createdWeapon = weaponService.createWeapon(weaponEntity);
            com.zelda.weapons.ws.Weapon weaponSoap = weaponMapper.entityToSoap(createdWeapon);
            
            CreateWeaponResponse response = new CreateWeaponResponse();
            response.setWeapon(weaponSoap);
            return response;
            
        } catch (WeaponAlreadyExistsException ex) {
            // El REST API busca este texto específico para identificar armas duplicadas
            throw new RuntimeException("ALREADY_EXISTS: " + ex.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteWeaponRequest")
    @ResponsePayload
    public DeleteWeaponResponse deleteWeapon(@RequestPayload DeleteWeaponRequest request) {
        try {
            UUID weaponId = UUID.fromString(request.getId());
            weaponService.deleteWeapon(weaponId);
            
            DeleteWeaponResponse response = new DeleteWeaponResponse();
            response.setSuccess(true);
            response.setMessage("Arma eliminada exitosamente");
            return response;
            
        } catch (Exception e) {
            DeleteWeaponResponse response = new DeleteWeaponResponse();
            response.setSuccess(false);
            response.setMessage("Error al eliminar arma: " + e.getMessage());
            return response;
        }
    }
}