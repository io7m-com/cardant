/*
 * An XML document type.
 * Localname: CommandLocationPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandLocationPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandLocationPutDocument extends CommandDocument
{
  DocumentFactory<CommandLocationPutDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandlocationput6418doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandLocationPut" element
   */
  CommandLocationPutType getCommandLocationPut();

  /**
   * Sets the "CommandLocationPut" element
   */
  void setCommandLocationPut(CommandLocationPutType commandLocationPut);

  /**
   * Appends and returns a new empty "CommandLocationPut" element
   */
  CommandLocationPutType addNewCommandLocationPut();
}
