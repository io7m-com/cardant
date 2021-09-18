/*
 * An XML document type.
 * Localname: CommandItemList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemListDocumentImpl extends CommandDocumentImpl implements
  CommandItemListDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemList"),
  };

  public CommandItemListDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemList" element
   */
  @Override
  public CommandItemListType getCommandItemList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemListType target = null;
      target = (CommandItemListType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemList" element
   */
  @Override
  public void setCommandItemList(final CommandItemListType commandItemList)
  {
    this.generatedSetterHelperImpl(
      commandItemList,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemList" element
   */
  @Override
  public CommandItemListType addNewCommandItemList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemListType target = null;
      target = (CommandItemListType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
