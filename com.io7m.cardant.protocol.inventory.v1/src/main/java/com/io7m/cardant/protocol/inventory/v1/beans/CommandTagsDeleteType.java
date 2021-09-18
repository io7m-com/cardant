/*
 * XML Type:  CommandTagsDeleteType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandTagsDeleteType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandTagsDeleteType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "commandtagsdeletetype1080type");
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
