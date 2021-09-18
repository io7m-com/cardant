/*
 * An XML document type.
 * Localname: CommandTagList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandTagList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandTagListDocument extends CommandDocument
{
  DocumentFactory<CommandTagListDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandtaglist089adoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandTagList" element
   */
  CommandTagListType getCommandTagList();

  /**
   * Sets the "CommandTagList" element
   */
  void setCommandTagList(CommandTagListType commandTagList);

  /**
   * Appends and returns a new empty "CommandTagList" element
   */
  CommandTagListType addNewCommandTagList();
}
