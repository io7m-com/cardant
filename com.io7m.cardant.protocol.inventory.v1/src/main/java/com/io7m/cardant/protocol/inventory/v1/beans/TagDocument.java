/*
 * An XML document type.
 * Localname: Tag
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Tag(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface TagDocument extends XmlObject
{
  DocumentFactory<TagDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "tag45ffdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Tag" element
   */
  TagType getTag();

  /**
   * Sets the "Tag" element
   */
  void setTag(TagType tag);

  /**
   * Appends and returns a new empty "Tag" element
   */
  TagType addNewTag();
}
