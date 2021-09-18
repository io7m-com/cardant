/*
 * An XML document type.
 * Localname: CommandItemMetadataRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemMetadataRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemMetadataRemoveDocument extends CommandDocument
{
  DocumentFactory<CommandItemMetadataRemoveDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemmetadataremove6288doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemMetadataRemove" element
   */
  CommandItemMetadataRemoveType getCommandItemMetadataRemove();

  /**
   * Sets the "CommandItemMetadataRemove" element
   */
  void setCommandItemMetadataRemove(CommandItemMetadataRemoveType commandItemMetadataRemove);

  /**
   * Appends and returns a new empty "CommandItemMetadataRemove" element
   */
  CommandItemMetadataRemoveType addNewCommandItemMetadataRemove();
}
