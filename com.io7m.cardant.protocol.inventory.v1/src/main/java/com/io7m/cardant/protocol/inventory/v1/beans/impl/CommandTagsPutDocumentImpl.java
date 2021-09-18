/*
 * An XML document type.
 * Localname: CommandTagsPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandTagsPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandTagsPutDocumentImpl extends CommandDocumentImpl implements
  CommandTagsPutDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsPut"),
  };

  public CommandTagsPutDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandTagsPut" element
   */
  @Override
  public CommandTagsPutType getCommandTagsPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandTagsPutType target = null;
      target = (CommandTagsPutType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandTagsPut" element
   */
  @Override
  public void setCommandTagsPut(final CommandTagsPutType commandTagsPut)
  {
    this.generatedSetterHelperImpl(
      commandTagsPut,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandTagsPut" element
   */
  @Override
  public CommandTagsPutType addNewCommandTagsPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandTagsPutType target = null;
      target = (CommandTagsPutType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
