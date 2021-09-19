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
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.UserIDDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "useridce21doctype");
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
