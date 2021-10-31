/*
 * An XML document type.
 * Localname: UserID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.UserIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one UserID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface UserIDDocument extends com.io7m.cardant.protocol.inventory.v1.beans.IDDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.UserIDDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "useridce21doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "UserID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UserIDType getUserID();

    /**
     * Sets the "UserID" element
     */
    void setUserID(com.io7m.cardant.protocol.inventory.v1.beans.UserIDType userID);

    /**
     * Appends and returns a new empty "UserID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UserIDType addNewUserID();
}
