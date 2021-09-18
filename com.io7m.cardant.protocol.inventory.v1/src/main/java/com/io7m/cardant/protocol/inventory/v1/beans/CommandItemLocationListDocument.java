/*
 * An XML document type.
 * Localname: CommandItemLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemLocationListDocument extends CommandDocument
{
  DocumentFactory<CommandItemLocationListDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemlocationlist8fe8doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemLocationList" element
   */
  CommandItemLocationListType getCommandItemLocationList();

  /**
   * Sets the "CommandItemLocationList" element
   */
  void setCommandItemLocationList(CommandItemLocationListType commandItemLocationList);

  /**
   * Appends and returns a new empty "CommandItemLocationList" element
   */
  CommandItemLocationListType addNewCommandItemLocationList();
}
