/*
 * XML Type:  ItemRepositRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ItemRepositRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemRepositRemoveType extends com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositRemoveType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "itemrepositremovetype9a92type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "item" attribute
     */
    java.lang.String getItem();

    /**
     * Gets (as xml) the "item" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetItem();

    /**
     * Sets the "item" attribute
     */
    void setItem(java.lang.String item);

    /**
     * Sets (as xml) the "item" attribute
     */
    void xsetItem(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType item);

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

    /**
     * Gets the "count" attribute
     */
    java.math.BigInteger getCount();

    /**
     * Gets (as xml) the "count" attribute
     */
    org.apache.xmlbeans.XmlUnsignedLong xgetCount();

    /**
     * Sets the "count" attribute
     */
    void setCount(java.math.BigInteger count);

    /**
     * Sets (as xml) the "count" attribute
     */
    void xsetCount(org.apache.xmlbeans.XmlUnsignedLong count);
}
