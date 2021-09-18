/*
 * XML Type:  CommandItemRepositType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML CommandItemRepositType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemRepositType extends CommandType
{
  DocumentFactory<CommandItemRepositType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemreposittypea8eftype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemReposit" element
   */
  ItemRepositType getItemReposit();

  /**
   * Sets the "ItemReposit" element
   */
  void setItemReposit(ItemRepositType itemReposit);

  /**
   * Appends and returns a new empty "ItemReposit" element
   */
  ItemRepositType addNewItemReposit();
}
