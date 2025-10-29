//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.10.28 a las 06:27:11 PM CST 
//


package com.zelda.weapons.ws;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para weaponType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="weaponType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Bat"/&gt;
 *     &lt;enumeration value="Boomerang"/&gt;
 *     &lt;enumeration value="Club"/&gt;
 *     &lt;enumeration value="Hammer"/&gt;
 *     &lt;enumeration value="Rod"/&gt;
 *     &lt;enumeration value="Spear"/&gt;
 *     &lt;enumeration value="OneHandedSword"/&gt;
 *     &lt;enumeration value="TwoHandedSword"/&gt;
 *     &lt;enumeration value="Other"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "weaponType")
@XmlEnum
public enum WeaponType {

    @XmlEnumValue("Bat")
    BAT("Bat"),
    @XmlEnumValue("Boomerang")
    BOOMERANG("Boomerang"),
    @XmlEnumValue("Club")
    CLUB("Club"),
    @XmlEnumValue("Hammer")
    HAMMER("Hammer"),
    @XmlEnumValue("Rod")
    ROD("Rod"),
    @XmlEnumValue("Spear")
    SPEAR("Spear"),
    @XmlEnumValue("OneHandedSword")
    ONE_HANDED_SWORD("OneHandedSword"),
    @XmlEnumValue("TwoHandedSword")
    TWO_HANDED_SWORD("TwoHandedSword"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    WeaponType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WeaponType fromValue(String v) {
        for (WeaponType c: WeaponType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
