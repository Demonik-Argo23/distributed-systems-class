package com.zelda.codex.gateways;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zelda.codex.exceptions.SoapServiceException;
import com.zelda.codex.exceptions.SoapServiceUnavailableException;
import com.zelda.codex.exceptions.SoapValidationException;
import com.zelda.codex.exceptions.WeaponAlreadyExistsException;
import com.zelda.codex.exceptions.WeaponNotFoundException;
import com.zelda.codex.mappers.WeaponMapper;
import com.zelda.codex.models.Element;
import com.zelda.codex.models.Weapon;
import com.zelda.codex.models.WeaponType;
import com.zelda.codex.soap.CreateWeaponRequest;
import com.zelda.codex.soap.CreateWeaponResponse;
import com.zelda.codex.soap.DeleteWeaponRequest;
import com.zelda.codex.soap.DeleteWeaponResponse;
import com.zelda.codex.soap.GetWeaponRequest;
import com.zelda.codex.soap.GetWeaponResponse;
import com.zelda.codex.soap.UpdateWeaponRequest;
import com.zelda.codex.soap.UpdateWeaponResponse;
import com.zelda.codex.soap.WeaponInput;

@Component
public class WeaponGateway implements IWeaponGateway {

    private static final Logger logger = LoggerFactory.getLogger(WeaponGateway.class);

    @Value("${zelda.weapons.soap.url:http://localhost:8081/ws}")
    private String soapServiceUrl;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private WeaponMapper weaponMapper;

    @Override
    public Weapon getWeaponById(UUID id) {
        try {
            logger.info("Obteniendo arma con ID {} del servicio SOAP", id);
            
            GetWeaponRequest request = new GetWeaponRequest();
            request.setId(id.toString());
            
            GetWeaponResponse response = (GetWeaponResponse) webServiceTemplate.marshalSendAndReceive(
                soapServiceUrl, request);
            
            if (response == null || response.getWeapon() == null) {
                throw new WeaponNotFoundException(id);
            }
            
            return weaponMapper.soapToModel(response.getWeapon());
            
        } catch (SoapFaultClientException ex) {
            logger.error("Error SOAP al obtener arma por ID {}: {}", id, ex.getFaultStringOrReason());
            if (ex.getFaultStringOrReason().contains("NOT_FOUND")) {
                throw new WeaponNotFoundException(id);
            }
            throw new SoapValidationException("Error de validación en el servicio SOAP: " + ex.getFaultStringOrReason());
        } catch (WeaponNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            if (ex.getCause() instanceof ConnectException || ex.getCause() instanceof SocketTimeoutException) {
                logger.error("Servicio SOAP no disponible: {}", ex.getMessage());
                throw new SoapServiceUnavailableException("El servicio SOAP no está disponible");
            }
            logger.error("Error genérico en servicio SOAP: {}", ex.getMessage());
            throw new SoapServiceException("Error en el servicio SOAP: " + ex.getMessage());
        }
    }

    @Override
    public Weapon createWeapon(Weapon weapon) {
        try {
            logger.info("Creando nueva arma en el servicio SOAP: {}", weapon.getName());
            
            CreateWeaponRequest request = new CreateWeaponRequest();
            WeaponInput weaponInput = weaponMapper.modelToSoapInput(weapon);
            request.setWeaponInput(weaponInput);
            
            CreateWeaponResponse response = (CreateWeaponResponse) webServiceTemplate.marshalSendAndReceive(
                soapServiceUrl, request);
            
            if (response == null || response.getWeapon() == null) {
                throw new SoapServiceException("Respuesta vacía del servicio SOAP al crear arma");
            }
            
            return weaponMapper.soapToModel(response.getWeapon());
            
        } catch (SoapFaultClientException ex) {
            logger.error("Error SOAP al crear arma: {}", ex.getFaultStringOrReason());
            if (ex.getFaultStringOrReason().contains("ALREADY_EXISTS")) {
                throw new WeaponAlreadyExistsException(weapon.getName());
            }
            throw new SoapValidationException("Error de validación en el servicio SOAP: " + ex.getFaultStringOrReason());
        } catch (Exception ex) {
            if (ex.getCause() instanceof ConnectException || ex.getCause() instanceof SocketTimeoutException) {
                logger.error("Servicio SOAP no disponible: {}", ex.getMessage());
                throw new SoapServiceUnavailableException("El servicio SOAP no está disponible");
            }
            logger.error("Error genérico en servicio SOAP: {}", ex.getMessage());
            throw new SoapServiceException("Error en el servicio SOAP: " + ex.getMessage());
        }
    }

    @Override
    public boolean deleteWeapon(UUID id) {
        try {
            logger.info("Eliminando arma con ID {} en el servicio SOAP", id);
            
            DeleteWeaponRequest request = new DeleteWeaponRequest();
            request.setId(id.toString());
            
            DeleteWeaponResponse response = (DeleteWeaponResponse) webServiceTemplate.marshalSendAndReceive(
                soapServiceUrl, request);
            
            return response != null && response.isSuccess();
            
        } catch (SoapFaultClientException ex) {
            logger.error("Error SOAP al eliminar arma con ID {}: {}", id, ex.getFaultStringOrReason());
            if (ex.getFaultStringOrReason().contains("NOT_FOUND")) {
                throw new WeaponNotFoundException(id);
            }
            throw new SoapValidationException("Error de validación en el servicio SOAP: " + ex.getFaultStringOrReason());
        } catch (Exception ex) {
            if (ex.getCause() instanceof ConnectException || ex.getCause() instanceof SocketTimeoutException) {
                logger.error("Servicio SOAP no disponible: {}", ex.getMessage());
                throw new SoapServiceUnavailableException("El servicio SOAP no está disponible");
            }
            logger.error("Error genérico en servicio SOAP: {}", ex.getMessage());
            throw new SoapServiceException("Error en el servicio SOAP: " + ex.getMessage());
        }
    }

    @Override
    public Page<Weapon> getAllWeapons(Pageable pageable, Map<String, String> filters) {
        try {
            List<Weapon> mockWeapons = generateMockWeapons(pageable, filters);
            long total = 50;
            
            return new PageImpl<>(mockWeapons, pageable, total);
            
        } catch (Exception ex) {
            throw new RuntimeException("Error obteniendo armas: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Weapon replaceWeapon(UUID id, Weapon weapon) {
        try {
            logger.info("Reemplazando arma con ID {} en el servicio SOAP", id);
            
            UpdateWeaponRequest request = new UpdateWeaponRequest();
            request.setId(id.toString());
            
            WeaponInput weaponInput = weaponMapper.modelToSoapInput(weapon);
            request.setWeaponInput(weaponInput);
            
            UpdateWeaponResponse response = (UpdateWeaponResponse) webServiceTemplate.marshalSendAndReceive(
                soapServiceUrl, request);
            
            if (response == null || response.getWeapon() == null) {
                throw new SoapServiceException("Respuesta vacía del servicio SOAP al actualizar arma");
            }
            
            return weaponMapper.soapToModel(response.getWeapon());
            
        } catch (SoapFaultClientException ex) {
            logger.error("Error SOAP al actualizar arma con ID {}: {}", id, ex.getFaultStringOrReason());
            if (ex.getFaultStringOrReason().contains("NOT_FOUND")) {
                throw new WeaponNotFoundException(id);
            }
            if (ex.getFaultStringOrReason().contains("ALREADY_EXISTS")) {
                throw new WeaponAlreadyExistsException(weapon.getName());
            }
            throw new SoapValidationException("Error de validación en el servicio SOAP: " + ex.getFaultStringOrReason());
        } catch (WeaponNotFoundException | WeaponAlreadyExistsException ex) {
            throw ex;
        } catch (Exception ex) {
            if (ex.getCause() instanceof ConnectException || ex.getCause() instanceof SocketTimeoutException) {
                logger.error("Servicio SOAP no disponible: {}", ex.getMessage());
                throw new SoapServiceUnavailableException("El servicio SOAP no está disponible");
            }
            logger.error("Error genérico en servicio SOAP: {}", ex.getMessage());
            throw new SoapServiceException("Error en el servicio SOAP: " + ex.getMessage());
        }
    }

    @Override
    public Weapon updateWeapon(UUID id, Map<String, Object> updates) {
        try {
            logger.info("Actualizando arma con ID {} via SOAP. Campos a actualizar: {}", id, updates.keySet());
            
            // Obtener el arma existente
            Weapon existingWeapon = getWeaponById(id);
            logger.debug("Arma existente obtenida: {}", existingWeapon.getName());
            
            // Crear una copia y aplicar las actualizaciones parciales
            Weapon updatedWeapon = createWeaponCopy(existingWeapon);
            applyUpdatesToWeapon(updatedWeapon, updates);
            logger.debug("Actualizaciones aplicadas. Nueva versión: {}", updatedWeapon.getName());
            
            // Usar el endpoint de actualización SOAP que mantiene el ID
            UpdateWeaponRequest request = new UpdateWeaponRequest();
            request.setId(id.toString());
            
            WeaponInput weaponInput = weaponMapper.modelToSoapInput(updatedWeapon);
            request.setWeaponInput(weaponInput);
            
            UpdateWeaponResponse response = (UpdateWeaponResponse) webServiceTemplate.marshalSendAndReceive(
                soapServiceUrl, request);
            
            if (response == null || response.getWeapon() == null) {
                throw new SoapServiceException("Respuesta vacía del servicio SOAP al actualizar arma");
            }
            
            Weapon result = weaponMapper.soapToModel(response.getWeapon());
            logger.info("Arma actualizada exitosamente con ID {}", result.getId());
            
            return result;
            
        } catch (SoapFaultClientException ex) {
            logger.error("Error SOAP al actualizar arma con ID {}: {}", id, ex.getFaultStringOrReason());
            if (ex.getFaultStringOrReason().contains("NOT_FOUND")) {
                throw new WeaponNotFoundException(id);
            }
            if (ex.getFaultStringOrReason().contains("ALREADY_EXISTS")) {
                throw new WeaponAlreadyExistsException(updates.getOrDefault("name", "").toString());
            }
            throw new SoapValidationException("Error de validación en el servicio SOAP: " + ex.getFaultStringOrReason());
        } catch (WeaponNotFoundException | WeaponAlreadyExistsException ex) {
            throw ex;
        } catch (Exception ex) {
            if (ex.getCause() instanceof ConnectException || ex.getCause() instanceof SocketTimeoutException) {
                logger.error("Servicio SOAP no disponible: {}", ex.getMessage());
                throw new SoapServiceUnavailableException("El servicio SOAP no está disponible");
            }
            logger.error("Error al actualizar arma con ID {}: {}", id, ex.getMessage());
            throw new SoapServiceException("Error al actualizar arma: " + ex.getMessage());
        }
    }

    private List<Weapon> generateMockWeapons(Pageable pageable, Map<String, String> filters) {
        List<Weapon> allWeapons = Arrays.asList(
            createMockWeapon(UUID.randomUUID(), "Master Sword", WeaponType.ONE_HANDED_SWORD, 30, 200),
            createMockWeapon(UUID.randomUUID(), "Royal Claymore", WeaponType.TWO_HANDED_SWORD, 52, 40),
            createMockWeapon(UUID.randomUUID(), "Ancient Spear", WeaponType.SPEAR, 30, 25),
            createMockWeapon(UUID.randomUUID(), "Traveler's Sword", WeaponType.ONE_HANDED_SWORD, 5, 22),
            createMockWeapon(UUID.randomUUID(), "Savage Lynel Spear", WeaponType.SPEAR, 30, 25),
            createMockWeapon(UUID.randomUUID(), "Royal Guard's Spear", WeaponType.SPEAR, 32, 15),
            createMockWeapon(UUID.randomUUID(), "Silver Lynel Spear", WeaponType.SPEAR, 26, 25),
            createMockWeapon(UUID.randomUUID(), "Great Eagle Bow", WeaponType.BOW, 28, 60),
            createMockWeapon(UUID.randomUUID(), "Hylian Shield", WeaponType.SHIELD, 90, 800)
        );

        List<Weapon> filteredWeapons = allWeapons;
        if (filters != null && filters.containsKey("weaponType")) {
            String typeFilter = filters.get("weaponType").toUpperCase();
            filteredWeapons = allWeapons.stream()
                .filter(weapon -> weapon.getWeaponType().name().equals(typeFilter))
                .collect(Collectors.toList());
        }

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

    /**
     * Crea una copia completa de un arma para actualizaciones
     */
    private Weapon createWeaponCopy(Weapon original) {
        Weapon copy = new Weapon();
        copy.setId(original.getId());
        copy.setName(original.getName());
        copy.setWeaponType(original.getWeaponType());
        copy.setDamage(original.getDamage());
        copy.setDurability(original.getDurability());
        copy.setElement(original.getElement());
        return copy;
    }

    /**
     * Aplica actualizaciones parciales a un arma (implementación de PATCH)
     */
    private void applyUpdatesToWeapon(Weapon weapon, Map<String, Object> updates) {
        logger.debug("Aplicando {} actualizaciones al arma {}", updates.size(), weapon.getId());
        
        updates.forEach((key, value) -> {
            try {
                switch (key.toLowerCase()) {
                    case "name":
                        if (value != null) {
                            weapon.setName(value.toString());
                            logger.debug("Actualizado nombre: {}", value);
                        }
                        break;
                    case "damage":
                        if (value != null) {
                            weapon.setDamage(Integer.valueOf(value.toString()));
                            logger.debug("Actualizado daño: {}", value);
                        }
                        break;
                    case "durability":
                        if (value != null) {
                            weapon.setDurability(Integer.valueOf(value.toString()));
                            logger.debug("Actualizada durabilidad: {}", value);
                        }
                        break;
                    case "weapontype":
                        if (value != null) {
                            weapon.setWeaponType(WeaponType.valueOf(value.toString().toUpperCase()));
                            logger.debug("Actualizado tipo de arma: {}", value);
                        }
                        break;
                    case "element":
                        if (value != null && !value.toString().trim().isEmpty()) {
                            weapon.setElement(Element.valueOf(value.toString().toUpperCase()));
                            logger.debug("Actualizado elemento: {}", value);
                        } else {
                            weapon.setElement(Element.NONE);
                            logger.debug("Elemento establecido a NONE");
                        }
                        break;
                    default:
                        logger.warn("Campo desconocido para actualización: {}", key);
                        break;
                }
            } catch (Exception ex) {
                logger.error("Error al actualizar campo '{}' con valor '{}': {}", key, value, ex.getMessage());
                throw new SoapValidationException("Error de validación en campo '" + key + "': " + ex.getMessage());
            }
        });
    }
}