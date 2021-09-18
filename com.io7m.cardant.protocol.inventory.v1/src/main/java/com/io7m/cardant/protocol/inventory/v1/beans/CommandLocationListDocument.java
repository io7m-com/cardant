/*
 * An XML document type.
 * Localname: CommandLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandLocationListDocument extends CommandDocument
{
  DocumentFactory<CommandLocationListDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandlocationlistef3bdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandLocationList" element
   */
  CommandLocationListType getCommandLocationList();

  /**
   * Sets the "CommandLocationList" element
   */
  void setCommandLocationList(CommandLocationListType commandLocationList);

  /**
   * Appends and returns a new empty "CommandLocationList" element
   */
  CommandLocationListType addNewCommandLocationList();
}
