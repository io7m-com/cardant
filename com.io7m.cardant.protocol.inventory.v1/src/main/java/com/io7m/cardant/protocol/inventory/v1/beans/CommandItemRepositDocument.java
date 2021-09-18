/*
 * An XML document type.
 * Localname: CommandItemReposit
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemReposit(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemRepositDocument extends CommandDocument
{
  DocumentFactory<CommandItemRepositDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemreposit0d79doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemReposit" element
   */
  CommandItemRepositType getCommandItemReposit();

  /**
   * Sets the "CommandItemReposit" element
   */
  void setCommandItemReposit(CommandItemRepositType commandItemReposit);

  /**
   * Appends and returns a new empty "CommandItemReposit" element
   */
  CommandItemRepositType addNewCommandItemReposit();
}
