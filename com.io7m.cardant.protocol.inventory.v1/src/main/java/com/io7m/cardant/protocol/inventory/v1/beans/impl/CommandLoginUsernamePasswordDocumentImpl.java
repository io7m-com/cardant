/*
 * An XML document type.
 * Localname: CommandLoginUsernamePassword
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandLoginUsernamePassword(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandLoginUsernamePasswordDocumentImpl extends CommandDocumentImpl implements
  CommandLoginUsernamePasswordDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName(
      "urn:com.io7m.cardant.inventory:1",
      "CommandLoginUsernamePassword"),
  };

  public CommandLoginUsernamePasswordDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandLoginUsernamePassword" element
   */
  @Override
  public CommandLoginUsernamePasswordType getCommandLoginUsernamePassword()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandLoginUsernamePasswordType target = null;
      target = (CommandLoginUsernamePasswordType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandLoginUsernamePassword" element
   */
  @Override
  public void setCommandLoginUsernamePassword(final CommandLoginUsernamePasswordType commandLoginUsernamePassword)
  {
    this.generatedSetterHelperImpl(
      commandLoginUsernamePassword,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandLoginUsernamePassword" element
   */
  @Override
  public CommandLoginUsernamePasswordType addNewCommandLoginUsernamePassword()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandLoginUsernamePasswordType target = null;
      target = (CommandLoginUsernamePasswordType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
