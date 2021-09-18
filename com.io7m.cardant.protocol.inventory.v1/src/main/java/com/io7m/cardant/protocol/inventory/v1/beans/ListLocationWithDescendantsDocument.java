/*
 * An XML document type.
 * Localname: ListLocationWithDescendants
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ListLocationWithDescendants(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ListLocationWithDescendantsDocument extends ListLocationsBehaviourDocument
{
  DocumentFactory<ListLocationWithDescendantsDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "listlocationwithdescendantsf37cdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ListLocationWithDescendants" element
   */
  ListLocationWithDescendantsType getListLocationWithDescendants();

  /**
   * Sets the "ListLocationWithDescendants" element
   */
  void setListLocationWithDescendants(ListLocationWithDescendantsType listLocationWithDescendants);

  /**
   * Appends and returns a new empty "ListLocationWithDescendants" element
   */
  ListLocationWithDescendantsType addNewListLocationWithDescendants();
}
