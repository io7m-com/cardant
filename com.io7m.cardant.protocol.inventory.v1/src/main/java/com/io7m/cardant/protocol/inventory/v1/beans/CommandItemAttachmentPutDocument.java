/*
 * An XML document type.
 * Localname: CommandItemAttachmentPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemAttachmentPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemAttachmentPutDocument extends CommandDocument
{
  DocumentFactory<CommandItemAttachmentPutDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemattachmentput4ef9doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemAttachmentPut" element
   */
  CommandItemAttachmentPutType getCommandItemAttachmentPut();

  /**
   * Sets the "CommandItemAttachmentPut" element
   */
  void setCommandItemAttachmentPut(CommandItemAttachmentPutType commandItemAttachmentPut);

  /**
   * Appends and returns a new empty "CommandItemAttachmentPut" element
   */
  CommandItemAttachmentPutType addNewCommandItemAttachmentPut();
}
