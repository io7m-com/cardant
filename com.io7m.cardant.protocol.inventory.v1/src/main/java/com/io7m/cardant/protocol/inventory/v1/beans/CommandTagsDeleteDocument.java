/*
 * An XML document type.
 * Localname: CommandTagsDelete
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandTagsDelete(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandTagsDeleteDocument extends CommandDocument
{
  DocumentFactory<CommandTagsDeleteDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandtagsdelete57aadoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandTagsDelete" element
   */
  CommandTagsDeleteType getCommandTagsDelete();

  /**
   * Sets the "CommandTagsDelete" element
   */
  void setCommandTagsDelete(CommandTagsDeleteType commandTagsDelete);

  /**
   * Appends and returns a new empty "CommandTagsDelete" element
   */
  CommandTagsDeleteType addNewCommandTagsDelete();
}
