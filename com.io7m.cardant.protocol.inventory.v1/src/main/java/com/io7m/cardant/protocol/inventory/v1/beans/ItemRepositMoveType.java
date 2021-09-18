/*
 * XML Type:  ItemRepositMoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ItemRepositMoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemRepositMoveType extends com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "itemrepositmovetype2f65type");
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
     * Gets the "fromLocation" attribute
     */
    java.lang.String getFromLocation();

    /**
     * Gets (as xml) the "fromLocation" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetFromLocation();

    /**
     * Sets the "fromLocation" attribute
     */
    void setFromLocation(java.lang.String fromLocation);

    /**
     * Sets (as xml) the "fromLocation" attribute
     */
    void xsetFromLocation(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType fromLocation);

    /**
     * Gets the "toLocation" attribute
     */
    java.lang.String getToLocation();

    /**
     * Gets (as xml) the "toLocation" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetToLocation();

    /**
     * Sets the "toLocation" attribute
     */
    void setToLocation(java.lang.String toLocation);

    /**
     * Sets (as xml) the "toLocation" attribute
     */
    void xsetToLocation(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType toLocation);

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
