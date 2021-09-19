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
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "itemtype2a8atype");
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
     * Gets the "count" attribute
     */
    java.math.BigInteger getCount();

    /**
     * Gets (as xml) the "count" attribute
     */
    org.apache.xmlbeans.XmlInteger xgetCount();

    /**
     * Sets the "count" attribute
     */
    void setCount(java.math.BigInteger count);

    /**
     * Sets (as xml) the "count" attribute
     */
    void xsetCount(org.apache.xmlbeans.XmlInteger count);
}
