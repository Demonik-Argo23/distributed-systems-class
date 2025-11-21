//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.01 a las 12:14:56 AM CST 
//


package com.zelda.weapons.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para weapon complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="weapon"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="weaponType" type="{http://zelda.com/weapons}weaponType"/&gt;
 *         &lt;element name="damage" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="durability" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="element" type="{http://zelda.com/weapons}element"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "weapon", propOrder = {
    "id",
    "name",
    "weaponType",
    "damage",
    "durability",
    "element"
})
public class Weapon {

    protected String id;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected WeaponType weaponType;
    protected int damage;
    protected int durability;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Element element;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Define el valor de la propiedad name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtiene el valor de la propiedad weaponType.
     * 
     * @return
     *     possible object is
     *     {@link WeaponType }
     *     
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /**
     * Define el valor de la propiedad weaponType.
     * 
     * @param value
     *     allowed object is
     *     {@link WeaponType }
     *     
     */
    public void setWeaponType(WeaponType value) {
        this.weaponType = value;
    }

    /**
     * Obtiene el valor de la propiedad damage.
     * 
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Define el valor de la propiedad damage.
     * 
     */
    public void setDamage(int value) {
        this.damage = value;
    }

    /**
     * Obtiene el valor de la propiedad durability.
     * 
     */
    public int getDurability() {
        return durability;
    }

    /**
     * Define el valor de la propiedad durability.
     * 
     */
    public void setDurability(int value) {
        this.durability = value;
    }

    /**
     * Obtiene el valor de la propiedad element.
     * 
     * @return
     *     possible object is
     *     {@link Element }
     *     
     */
    public Element getElement() {
        return element;
    }

    /**
     * Define el valor de la propiedad element.
     * 
     * @param value
     *     allowed object is
     *     {@link Element }
     *     
     */
    public void setElement(Element value) {
        this.element = value;
    }

}
