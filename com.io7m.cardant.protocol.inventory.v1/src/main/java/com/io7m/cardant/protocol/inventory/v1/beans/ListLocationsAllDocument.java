/*
 * An XML document type.
 * Localname: ListLocationsAll
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ListLocationsAll(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ListLocationsAllDocument extends ListLocationsBehaviourDocument
{
  DocumentFactory<ListLocationsAllDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "listlocationsall4dc6doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ListLocationsAll" element
   */
  ListLocationsAllType getListLocationsAll();

  /**
   * Sets the "ListLocationsAll" element
   */
  void setListLocationsAll(ListLocationsAllType listLocationsAll);

  /**
   * Appends and returns a new empty "ListLocationsAll" element
   */
  ListLocationsAllType addNewListLocationsAll();
}
