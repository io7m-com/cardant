/*
 * An XML document type.
 * Localname: CommandItemList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemListDocument extends CommandDocument
{
  DocumentFactory<CommandItemListDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemlisted9ddoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemList" element
   */
  CommandItemListType getCommandItemList();

  /**
   * Sets the "CommandItemList" element
   */
  void setCommandItemList(CommandItemListType commandItemList);

  /**
   * Appends and returns a new empty "CommandItemList" element
   */
  CommandItemListType addNewCommandItemList();
}
