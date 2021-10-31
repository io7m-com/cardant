/*
 * XML Type:  ListLocationExactType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ListLocationExactType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ListLocationExactType extends com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "listlocationexacttypeff23type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "location" attribute
     */
    java.lang.String getLocation();

    /**
     * Gets (as xml) the "location" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetLocation();

    /**
     * Sets the "location" attribute
     */
    void setLocation(java.lang.String location);

    /**
     * Sets (as xml) the "location" attribute
     */
    void xsetLocation(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType location);
}
