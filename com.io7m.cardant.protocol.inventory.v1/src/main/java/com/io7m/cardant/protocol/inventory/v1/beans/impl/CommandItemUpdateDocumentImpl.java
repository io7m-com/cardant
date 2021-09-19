/*
 * An XML document type.
 * Localname: CommandItemUpdate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemUpdate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemUpdateDocumentImpl extends CommandDocumentImpl implements
  CommandItemUpdateDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemUpdate"),
  };

  public CommandItemUpdateDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemUpdate" element
   */
  @Override
  public CommandItemUpdateType getCommandItemUpdate()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemUpdateType target = null;
      target = (CommandItemUpdateType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemUpdate" element
   */
  @Override
  public void setCommandItemUpdate(final CommandItemUpdateType commandItemUpdate)
  {
    this.generatedSetterHelperImpl(
      commandItemUpdate,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemUpdate" element
   */
  @Override
  public CommandItemUpdateType addNewCommandItemUpdate()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemUpdateType target = null;
      target = (CommandItemUpdateType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
