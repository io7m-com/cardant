/*
 * An XML document type.
 * Localname: CommandItemAttachmentRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemAttachmentRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemAttachmentRemoveDocument extends CommandDocument
{
  DocumentFactory<CommandItemAttachmentRemoveDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemattachmentremovec754doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemAttachmentRemove" element
   */
  CommandItemAttachmentRemoveType getCommandItemAttachmentRemove();

  /**
   * Sets the "CommandItemAttachmentRemove" element
   */
  void setCommandItemAttachmentRemove(CommandItemAttachmentRemoveType commandItemAttachmentRemove);

  /**
   * Appends and returns a new empty "CommandItemAttachmentRemove" element
   */
  CommandItemAttachmentRemoveType addNewCommandItemAttachmentRemove();
}
