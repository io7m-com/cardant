/*
 * XML Type:  TagsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML TagsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface TagsType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.TagsType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "tagstypebd44type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "Tag" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.TagType> getTagList();

    /**
     * Gets array of all "Tag" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TagType[] getTagArray();

    /**
     * Gets ith "Tag" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TagType getTagArray(int i);

    /**
     * Returns number of "Tag" element
     */
    int sizeOfTagArray();

    /**
     * Sets array of all "Tag" element
     */
    void setTagArray(com.io7m.cardant.protocol.inventory.v1.beans.TagType[] tagArray);

    /**
     * Sets ith "Tag" element
     */
    void setTagArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.TagType tag);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Tag" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TagType insertNewTag(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Tag" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.TagType addNewTag();

    /**
     * Removes the ith "Tag" element
     */
    void removeTag(int i);
}
