/*
 * An XML document type.
 * Localname: CommandItemMetadataPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemMetadataPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemMetadataPutDocumentImpl extends CommandDocumentImpl implements
  CommandItemMetadataPutDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataPut"),
  };

  public CommandItemMetadataPutDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemMetadataPut" element
   */
  @Override
  public CommandItemMetadataPutType getCommandItemMetadataPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemMetadataPutType target = null;
      target = (CommandItemMetadataPutType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemMetadataPut" element
   */
  @Override
  public void setCommandItemMetadataPut(final CommandItemMetadataPutType commandItemMetadataPut)
  {
    this.generatedSetterHelperImpl(
      commandItemMetadataPut,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemMetadataPut" element
   */
  @Override
  public CommandItemMetadataPutType addNewCommandItemMetadataPut()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemMetadataPutType target = null;
      target = (CommandItemMetadataPutType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
