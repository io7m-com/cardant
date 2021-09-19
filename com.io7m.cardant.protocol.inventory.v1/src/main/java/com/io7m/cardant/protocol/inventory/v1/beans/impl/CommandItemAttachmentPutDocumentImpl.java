/*
 * An XML document type.
 * Localname: CommandItemAttachmentPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemAttachmentPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemAttachmentPutDocumentImpl extends CommandDocumentImpl implements
  CommandItemAttachmentPutDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentPut"),
  };

  public CommandItemAttachmentPutDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemAttachmentPut" element
   */
  @Override
  public CommandItemAttachmentPutType getCommandItemAttachmentPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemAttachmentPutType target = null;
      target = (CommandItemAttachmentPutType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemAttachmentPut" element
   */
  @Override
  public void setCommandItemAttachmentPut(final CommandItemAttachmentPutType commandItemAttachmentPut)
  {
    this.generatedSetterHelperImpl(
      commandItemAttachmentPut,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemAttachmentPut" element
   */
  @Override
  public CommandItemAttachmentPutType addNewCommandItemAttachmentPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemAttachmentPutType target = null;
      target = (CommandItemAttachmentPutType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
