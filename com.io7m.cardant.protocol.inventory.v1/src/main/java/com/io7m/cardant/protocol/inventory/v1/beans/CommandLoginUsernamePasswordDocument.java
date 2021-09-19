/*
 * An XML document type.
 * Localname: CommandLoginUsernamePassword
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one CommandLoginUsernamePassword(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandLoginUsernamePasswordDocument extends CommandDocument
{
  DocumentFactory<CommandLoginUsernamePasswordDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandloginusernamepassword3d98doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "CommandLoginUsernamePassword" element
   */
  CommandLoginUsernamePasswordType getCommandLoginUsernamePassword();

  /**
   * Sets the "CommandLoginUsernamePassword" element
   */
  void setCommandLoginUsernamePassword(CommandLoginUsernamePasswordType commandLoginUsernamePassword);

  /**
   * Appends and returns a new empty "CommandLoginUsernamePassword" element
   */
  CommandLoginUsernamePasswordType addNewCommandLoginUsernamePassword();
}
