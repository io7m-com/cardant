/*
 * An XML document type.
 * Localname: CommandItemCreate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandItemCreate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemCreateDocument extends CommandDocument
{
  DocumentFactory<CommandItemCreateDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemcreatede3fdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandItemCreate" element
   */
  CommandItemCreateType getCommandItemCreate();

  /**
   * Sets the "CommandItemCreate" element
   */
  void setCommandItemCreate(CommandItemCreateType commandItemCreate);

  /**
   * Appends and returns a new empty "CommandItemCreate" element
   */
  CommandItemCreateType addNewCommandItemCreate();
}
