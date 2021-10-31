/*
 * An XML document type.
 * Localname: LocationID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one LocationID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface LocationIDDocument extends com.io7m.cardant.protocol.inventory.v1.beans.IDDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.LocationIDDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "locationidc677doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "LocationID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType getLocationID();

    /**
     * Sets the "LocationID" element
     */
    void setLocationID(com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType locationID);

    /**
     * Appends and returns a new empty "LocationID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType addNewLocationID();
}
