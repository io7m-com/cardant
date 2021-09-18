/*
 * An XML document type.
 * Localname: CommandItemUpdate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemUpdate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemUpdateDocument extends CommandDocument
{
  DocumentFactory<CommandItemUpdateDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemupdate3412doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemUpdate" element
   */
  CommandItemUpdateType getCommandItemUpdate();

  /**
   * Sets the "CommandItemUpdate" element
   */
  void setCommandItemUpdate(CommandItemUpdateType commandItemUpdate);

  /**
   * Appends and returns a new empty "CommandItemUpdate" element
   */
  CommandItemUpdateType addNewCommandItemUpdate();
}
