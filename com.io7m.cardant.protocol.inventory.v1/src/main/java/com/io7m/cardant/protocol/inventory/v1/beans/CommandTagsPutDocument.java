/*
 * An XML document type.
 * Localname: CommandTagsPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandTagsPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandTagsPutDocument extends CommandDocument
{
  DocumentFactory<CommandTagsPutDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandtagsput3f7cdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandTagsPut" element
   */
  CommandTagsPutType getCommandTagsPut();

  /**
   * Sets the "CommandTagsPut" element
   */
  void setCommandTagsPut(CommandTagsPutType commandTagsPut);

  /**
   * Appends and returns a new empty "CommandTagsPut" element
   */
  CommandTagsPutType addNewCommandTagsPut();
}
