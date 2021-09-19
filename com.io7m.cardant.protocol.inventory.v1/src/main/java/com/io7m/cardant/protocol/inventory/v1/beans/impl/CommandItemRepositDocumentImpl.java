/*
 * An XML document type.
 * Localname: CommandItemReposit
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemReposit(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemRepositDocumentImpl extends CommandDocumentImpl implements
  CommandItemRepositDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemReposit"),
  };

  public CommandItemRepositDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemReposit" element
   */
  @Override
  public CommandItemRepositType getCommandItemReposit()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemRepositType target = null;
      target = (CommandItemRepositType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemReposit" element
   */
  @Override
  public void setCommandItemReposit(final CommandItemRepositType commandItemReposit)
  {
    this.generatedSetterHelperImpl(
      commandItemReposit,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemReposit" element
   */
  @Override
  public CommandItemRepositType addNewCommandItemReposit()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemRepositType target = null;
      target = (CommandItemRepositType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
