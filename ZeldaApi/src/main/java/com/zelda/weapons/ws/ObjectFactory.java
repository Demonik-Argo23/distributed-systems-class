//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.27 a las 07:54:25 PM CST 
//


package com.zelda.weapons.ws;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.zelda.weapons.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zelda.weapons.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetWeaponRequest }
     * 
     */
    public GetWeaponRequest createGetWeaponRequest() {
        return new GetWeaponRequest();
    }

    /**
     * Create an instance of {@link GetWeaponResponse }
     * 
     */
    public GetWeaponResponse createGetWeaponResponse() {
        return new GetWeaponResponse();
    }

    /**
     * Create an instance of {@link Weapon }
     * 
     */
    public Weapon createWeapon() {
        return new Weapon();
    }

    /**
     * Create an instance of {@link CreateWeaponRequest }
     * 
     */
    public CreateWeaponRequest createCreateWeaponRequest() {
        return new CreateWeaponRequest();
    }

    /**
     * Create an instance of {@link WeaponInput }
     * 
     */
    public WeaponInput createWeaponInput() {
        return new WeaponInput();
    }

    /**
     * Create an instance of {@link CreateWeaponResponse }
     * 
     */
    public CreateWeaponResponse createCreateWeaponResponse() {
        return new CreateWeaponResponse();
    }

    /**
     * Create an instance of {@link DeleteWeaponRequest }
     * 
     */
    public DeleteWeaponRequest createDeleteWeaponRequest() {
        return new DeleteWeaponRequest();
    }

    /**
     * Create an instance of {@link DeleteWeaponResponse }
     * 
     */
    public DeleteWeaponResponse createDeleteWeaponResponse() {
        return new DeleteWeaponResponse();
    }

}
