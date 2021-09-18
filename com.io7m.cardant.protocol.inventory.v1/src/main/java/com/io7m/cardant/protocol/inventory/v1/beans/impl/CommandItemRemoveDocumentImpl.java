/*
 * An XML document type.
 * Localname: CommandItemRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemRemoveDocumentImpl extends CommandDocumentImpl implements
  CommandItemRemoveDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemRemove"),
  };

  public CommandItemRemoveDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemRemove" element
   */
  @Override
  public CommandItemRemoveType getCommandItemRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemRemoveType target = null;
      target = (CommandItemRemoveType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemRemove" element
   */
  @Override
  public void setCommandItemRemove(final CommandItemRemoveType commandItemRemove)
  {
    this.generatedSetterHelperImpl(
      commandItemRemove,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemRemove" element
   */
  @Override
  public CommandItemRemoveType addNewCommandItemRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemRemoveType target = null;
      target = (CommandItemRemoveType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
