/*
 * An XML document type.
 * Localname: CommandItemMetadataRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemMetadataRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemMetadataRemoveDocumentImpl extends CommandDocumentImpl implements
  CommandItemMetadataRemoveDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataRemove"),
  };

  public CommandItemMetadataRemoveDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemMetadataRemove" element
   */
  @Override
  public CommandItemMetadataRemoveType getCommandItemMetadataRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemMetadataRemoveType target = null;
      target = (CommandItemMetadataRemoveType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemMetadataRemove" element
   */
  @Override
  public void setCommandItemMetadataRemove(final CommandItemMetadataRemoveType commandItemMetadataRemove)
  {
    this.generatedSetterHelperImpl(
      commandItemMetadataRemove,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemMetadataRemove" element
   */
  @Override
  public CommandItemMetadataRemoveType addNewCommandItemMetadataRemove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemMetadataRemoveType target = null;
      target = (CommandItemMetadataRemoveType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
