/*
 * XML Type:  RemovedType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.RemovedType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML RemovedType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface RemovedType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.RemovedType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "removedtypececftype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.IDType getID();

    /**
     * Sets the "ID" element
     */
    void setID(com.io7m.cardant.protocol.inventory.v1.beans.IDType id);

    /**
     * Appends and returns a new empty "ID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.IDType addNewID();
}
