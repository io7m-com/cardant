/*
 * An XML document type.
 * Localname: CommandLocationPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandLocationPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandLocationPutDocumentImpl extends CommandDocumentImpl implements
  CommandLocationPutDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationPut"),
  };

  public CommandLocationPutDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandLocationPut" element
   */
  @Override
  public CommandLocationPutType getCommandLocationPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandLocationPutType target = null;
      target = (CommandLocationPutType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandLocationPut" element
   */
  @Override
  public void setCommandLocationPut(final CommandLocationPutType commandLocationPut)
  {
    this.generatedSetterHelperImpl(
      commandLocationPut,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandLocationPut" element
   */
  @Override
  public CommandLocationPutType addNewCommandLocationPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandLocationPutType target = null;
      target = (CommandLocationPutType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
