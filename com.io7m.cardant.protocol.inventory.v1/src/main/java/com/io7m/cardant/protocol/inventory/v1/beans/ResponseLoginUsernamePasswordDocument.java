/*
 * An XML document type.
 * Localname: ResponseLoginUsernamePassword
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseLoginUsernamePassword(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseLoginUsernamePasswordDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "responseloginusernamepassword4ae0doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseLoginUsernamePassword" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType getResponseLoginUsernamePassword();

    /**
     * Sets the "ResponseLoginUsernamePassword" element
     */
    void setResponseLoginUsernamePassword(com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType responseLoginUsernamePassword);

    /**
     * Appends and returns a new empty "ResponseLoginUsernamePassword" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType addNewResponseLoginUsernamePassword();
}
