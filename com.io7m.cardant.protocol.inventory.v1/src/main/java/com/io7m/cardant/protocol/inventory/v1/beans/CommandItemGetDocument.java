/*
 * An XML document type.
 * Localname: CommandItemGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemGetDocument extends CommandDocument
{
  DocumentFactory<CommandItemGetDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemget1a0fdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemGet" element
   */
  CommandItemGetType getCommandItemGet();

  /**
   * Sets the "CommandItemGet" element
   */
  void setCommandItemGet(CommandItemGetType commandItemGet);

  /**
   * Appends and returns a new empty "CommandItemGet" element
   */
  CommandItemGetType addNewCommandItemGet();
}
