/*
 * XML Type:  ResponseTagsPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML ResponseTagsPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseTagsPutType extends ResponseType
{
  DocumentFactory<ResponseTagsPutType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responsetagsputtype029atype");
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
