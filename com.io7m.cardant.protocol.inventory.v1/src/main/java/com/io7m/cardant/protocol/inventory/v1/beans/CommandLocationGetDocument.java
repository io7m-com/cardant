/*
 * An XML document type.
 * Localname: CommandLocationGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandLocationGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandLocationGetDocument extends CommandDocument
{
  DocumentFactory<CommandLocationGetDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandlocationget6cb1doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandLocationGet" element
   */
  CommandLocationGetType getCommandLocationGet();

  /**
   * Sets the "CommandLocationGet" element
   */
  void setCommandLocationGet(CommandLocationGetType commandLocationGet);

  /**
   * Appends and returns a new empty "CommandLocationGet" element
   */
  CommandLocationGetType addNewCommandLocationGet();
}
