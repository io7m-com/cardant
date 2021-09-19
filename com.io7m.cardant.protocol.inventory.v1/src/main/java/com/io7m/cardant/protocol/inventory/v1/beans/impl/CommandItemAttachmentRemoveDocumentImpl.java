/*
 * An XML document type.
 * Localname: CommandItemAttachmentRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemAttachmentRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemAttachmentRemoveDocumentImpl extends CommandDocumentImpl implements
  CommandItemAttachmentRemoveDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName(
      "urn:com.io7m.cardant.inventory:1",
      "CommandItemAttachmentRemove"),
  };

  public CommandItemAttachmentRemoveDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemAttachmentRemove" element
   */
  @Override
  public CommandItemAttachmentRemoveType getCommandItemAttachmentRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemAttachmentRemoveType target = null;
      target = (CommandItemAttachmentRemoveType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemAttachmentRemove" element
   */
  @Override
  public void setCommandItemAttachmentRemove(final CommandItemAttachmentRemoveType commandItemAttachmentRemove)
  {
    this.generatedSetterHelperImpl(
      commandItemAttachmentRemove,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemAttachmentRemove" element
   */
  @Override
  public CommandItemAttachmentRemoveType addNewCommandItemAttachmentRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemAttachmentRemoveType target = null;
      target = (CommandItemAttachmentRemoveType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
