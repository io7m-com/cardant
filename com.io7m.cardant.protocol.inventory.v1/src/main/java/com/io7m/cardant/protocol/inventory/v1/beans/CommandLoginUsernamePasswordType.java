/*
 * XML Type:  CommandLoginUsernamePasswordType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandLoginUsernamePasswordType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandLoginUsernamePasswordType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "commandloginusernamepasswordtypedd8etype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "user" attribute
     */
    java.lang.String getUser();

    /**
     * Gets (as xml) the "user" attribute
     */
    org.apache.xmlbeans.XmlToken xgetUser();

    /**
     * Sets the "user" attribute
     */
    void setUser(java.lang.String user);

    /**
     * Sets (as xml) the "user" attribute
     */
    void xsetUser(org.apache.xmlbeans.XmlToken user);

    /**
     * Gets the "password" attribute
     */
    java.lang.String getPassword();

    /**
     * Gets (as xml) the "password" attribute
     */
    org.apache.xmlbeans.XmlString xgetPassword();

    /**
     * Sets the "password" attribute
     */
    void setPassword(java.lang.String password);

    /**
     * Sets (as xml) the "password" attribute
     */
    void xsetPassword(org.apache.xmlbeans.XmlString password);
}
