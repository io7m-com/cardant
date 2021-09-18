/*
 * An XML document type.
 * Localname: TagID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one TagID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface TagIDDocument extends IDDocument
{
  DocumentFactory<TagIDDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "tagid1884doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "TagID" element
   */
  TagIDType getTagID();

  /**
   * Sets the "TagID" element
   */
  void setTagID(TagIDType tagID);

  /**
   * Appends and returns a new empty "TagID" element
   */
  TagIDType addNewTagID();
}
