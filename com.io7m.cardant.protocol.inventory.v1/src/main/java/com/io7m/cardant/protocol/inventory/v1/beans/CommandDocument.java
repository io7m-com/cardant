/*
 * An XML document type.
 * Localname: Command
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Command(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandDocument extends MessageDocument
{
  DocumentFactory<CommandDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandebeedoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Command" element
   */
  CommandType getCommand();

  /**
   * Sets the "Command" element
   */
  void setCommand(CommandType command);

  /**
   * Appends and returns a new empty "Command" element
   */
  CommandType addNewCommand();
}
