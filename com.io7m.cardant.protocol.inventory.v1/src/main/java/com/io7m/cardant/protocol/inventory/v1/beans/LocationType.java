/*
 * XML Type:  LocationType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML LocationType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface LocationType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.LocationType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "locationtypeffa8type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "id" attribute
     */
    java.lang.String getId();

    /**
     * Gets (as xml) the "id" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetId();

    /**
     * Sets the "id" attribute
     */
    void setId(java.lang.String id);

    /**
     * Sets (as xml) the "id" attribute
     */
    void xsetId(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType id);

    /**
     * Gets the "parent" attribute
     */
    java.lang.String getParent();

    /**
     * Gets (as xml) the "parent" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetParent();

    /**
     * True if has "parent" attribute
     */
    boolean isSetParent();

    /**
     * Sets the "parent" attribute
     */
    void setParent(java.lang.String parent);

    /**
     * Sets (as xml) the "parent" attribute
     */
    void xsetParent(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType parent);

    /**
     * Unsets the "parent" attribute
     */
    void unsetParent();

    /**
     * Gets the "name" attribute
     */
    java.lang.String getName();

    /**
     * Gets (as xml) the "name" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.LocationNameType xgetName();

    /**
     * Sets the "name" attribute
     */
    void setName(java.lang.String name);

    /**
     * Sets (as xml) the "name" attribute
     */
    void xsetName(com.io7m.cardant.protocol.inventory.v1.beans.LocationNameType name);

    /**
     * Gets the "description" attribute
     */
    java.lang.String getDescription();

    /**
     * Gets (as xml) the "description" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.LocationDescriptionType xgetDescription();

    /**
     * Sets the "description" attribute
     */
    void setDescription(java.lang.String description);

    /**
     * Sets (as xml) the "description" attribute
     */
    void xsetDescription(com.io7m.cardant.protocol.inventory.v1.beans.LocationDescriptionType description);
}
