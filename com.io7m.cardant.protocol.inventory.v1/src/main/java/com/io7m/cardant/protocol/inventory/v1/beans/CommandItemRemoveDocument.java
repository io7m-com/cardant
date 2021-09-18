/*
 * An XML document type.
 * Localname: CommandItemRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemRemoveDocument extends CommandDocument
{
  DocumentFactory<CommandItemRemoveDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemremove97b7doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemRemove" element
   */
  CommandItemRemoveType getCommandItemRemove();

  /**
   * Sets the "CommandItemRemove" element
   */
  void setCommandItemRemove(CommandItemRemoveType commandItemRemove);

  /**
   * Appends and returns a new empty "CommandItemRemove" element
   */
  CommandItemRemoveType addNewCommandItemRemove();
}
