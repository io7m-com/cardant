/*
 * XML Type:  ResponseTagsDeleteType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ResponseTagsDeleteType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseTagsDeleteType extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responsetagsdeletetype4658type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


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
}
