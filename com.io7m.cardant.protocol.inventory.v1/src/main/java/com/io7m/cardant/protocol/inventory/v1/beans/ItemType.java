/*
 * XML Type:  ItemType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ItemType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "itemtype2a8atype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemMetadatas" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType getItemMetadatas();

    /**
     * Sets the "ItemMetadatas" element
     */
    void setItemMetadatas(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType itemMetadatas);

    /**
     * Appends and returns a new empty "ItemMetadatas" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType addNewItemMetadatas();

    /**
     * Gets the "Tags" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TagsType getTags();

    /**
     * Sets the "Tags" element
     */
    void setTags(com.io7m.cardant.protocol.inventory.v1.beans.TagsType tags);

    /**
     * Appends and returns a new empty "Tags" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TagsType addNewTags();

    /**
     * Gets the "ItemAttachments" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType getItemAttachments();

    /**
     * Sets the "ItemAttachments" element
     */
    void setItemAttachments(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType itemAttachments);

    /**
     * Appends and returns a new empty "ItemAttachments" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType addNewItemAttachments();

    /**
     * Gets the "id" attribute
     */
    java.lang.String getId();

    /**
     * Gets (as xml) the "id" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetId();

    /**
     * Sets the "id" attribute
     */
    void setId(java.lang.String id);

    /**
     * Sets (as xml) the "id" attribute
     */
    void xsetId(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType id);

    /**
     * Gets the "name" attribute
     */
    java.lang.String getName();

    /**
     * Gets (as xml) the "name" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType xgetName();

    /**
     * Sets the "name" attribute
     */
    void setName(java.lang.String name);

    /**
     * Sets (as xml) the "name" attribute
     */
    void xsetName(com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType name);

    /**
     * Gets the "countTotal" attribute
     */
    java.math.BigInteger getCountTotal();

    /**
     * Gets (as xml) the "countTotal" attribute
     */
    org.apache.xmlbeans.XmlUnsignedLong xgetCountTotal();

    /**
     * Sets the "countTotal" attribute
     */
    void setCountTotal(java.math.BigInteger countTotal);

    /**
     * Sets (as xml) the "countTotal" attribute
     */
    void xsetCountTotal(org.apache.xmlbeans.XmlUnsignedLong countTotal);

    /**
     * Gets the "countHere" attribute
     */
    java.math.BigInteger getCountHere();

    /**
     * Gets (as xml) the "countHere" attribute
     */
    org.apache.xmlbeans.XmlUnsignedLong xgetCountHere();

    /**
     * Sets the "countHere" attribute
     */
    void setCountHere(java.math.BigInteger countHere);

    /**
     * Sets (as xml) the "countHere" attribute
     */
    void xsetCountHere(org.apache.xmlbeans.XmlUnsignedLong countHere);
}
