/*
 * An XML document type.
 * Localname: CommandItemMetadataPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemMetadataPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemMetadataPutDocument extends CommandDocument
{
  DocumentFactory<CommandItemMetadataPutDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemmetadataputa045doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemMetadataPut" element
   */
  CommandItemMetadataPutType getCommandItemMetadataPut();

  /**
   * Sets the "CommandItemMetadataPut" element
   */
  void setCommandItemMetadataPut(CommandItemMetadataPutType commandItemMetadataPut);

  /**
   * Appends and returns a new empty "CommandItemMetadataPut" element
   */
  CommandItemMetadataPutType addNewCommandItemMetadataPut();
}
