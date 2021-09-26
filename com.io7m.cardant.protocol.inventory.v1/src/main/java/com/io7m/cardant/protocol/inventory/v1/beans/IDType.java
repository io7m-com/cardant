/*
 * XML Type:  IDType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML IDType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface IDType extends org.apache.xmlbeans.XmlObject {
    AbstractDocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.IDType> Factory = new AbstractDocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "idtype2362type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "value" attribute
     */
    java.lang.String getValue();

    /**
     * Gets (as xml) the "value" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetValue();

    /**
     * Sets the "value" attribute
     */
    void setValue(java.lang.String value);

    /**
     * Sets (as xml) the "value" attribute
     */
    void xsetValue(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType value);
}
