/*
 * XML Type:  CommandItemListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML CommandItemListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemListType extends CommandType
{
  DocumentFactory<CommandItemListType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemlisttype30f3type");
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
   * True if has "ListLocationsAll" element
   */
  boolean isSetListLocationsAll();

  /**
   * Appends and returns a new empty "ListLocationsAll" element
   */
  ListLocationsAllType addNewListLocationsAll();

  /**
   * Unsets the "ListLocationsAll" element
   */
  void unsetListLocationsAll();

  /**
   * Gets the "ListLocationExact" element
   */
  ListLocationExactType getListLocationExact();

  /**
   * Sets the "ListLocationExact" element
   */
  void setListLocationExact(ListLocationExactType listLocationExact);

  /**
   * True if has "ListLocationExact" element
   */
  boolean isSetListLocationExact();

  /**
   * Appends and returns a new empty "ListLocationExact" element
   */
  ListLocationExactType addNewListLocationExact();

  /**
   * Unsets the "ListLocationExact" element
   */
  void unsetListLocationExact();

  /**
   * Gets the "ListLocationWithDescendants" element
   */
  ListLocationWithDescendantsType getListLocationWithDescendants();

  /**
   * Sets the "ListLocationWithDescendants" element
   */
  void setListLocationWithDescendants(ListLocationWithDescendantsType listLocationWithDescendants);

  /**
   * True if has "ListLocationWithDescendants" element
   */
  boolean isSetListLocationWithDescendants();

  /**
   * Appends and returns a new empty "ListLocationWithDescendants" element
   */
  ListLocationWithDescendantsType addNewListLocationWithDescendants();

  /**
   * Unsets the "ListLocationWithDescendants" element
   */
  void unsetListLocationWithDescendants();
}
