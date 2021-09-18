/*
 * An XML document type.
 * Localname: Tags
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Tags(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface TagsDocument extends XmlObject
{
  DocumentFactory<TagsDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "tags384edoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Tags" element
   */
  TagsType getTags();

  /**
   * Sets the "Tags" element
   */
  void setTags(TagsType tags);

  /**
   * Appends and returns a new empty "Tags" element
   */
  TagsType addNewTags();
}
