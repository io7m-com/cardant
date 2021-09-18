/*
 * XML Type:  CommandLocationPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandLocationPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandLocationPutType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "commandlocationputtype980etype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Location" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.LocationType getLocation();

    /**
     * Sets the "Location" element
     */
    void setLocation(com.io7m.cardant.protocol.inventory.v1.beans.LocationType location);

    /**
     * Appends and returns a new empty "Location" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.LocationType addNewLocation();
}
