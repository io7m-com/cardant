/*
 * XML Type:  ResponseErrorDetailType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ResponseErrorDetailType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseErrorDetailType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "responseerrordetailtypeaf37type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "message" attribute
     */
    java.lang.String getMessage();

    /**
     * Gets (as xml) the "message" attribute
     */
    org.apache.xmlbeans.XmlString xgetMessage();

    /**
     * Sets the "message" attribute
     */
    void setMessage(java.lang.String message);

    /**
     * Sets (as xml) the "message" attribute
     */
    void xsetMessage(org.apache.xmlbeans.XmlString message);
}
