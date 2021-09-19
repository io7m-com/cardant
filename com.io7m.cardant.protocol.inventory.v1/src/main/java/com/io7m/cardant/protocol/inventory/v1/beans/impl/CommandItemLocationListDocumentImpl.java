/*
 * An XML document type.
 * Localname: CommandItemLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemLocationListDocumentImpl extends CommandDocumentImpl implements
  CommandItemLocationListDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemLocationList"),
  };

  public CommandItemLocationListDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemLocationList" element
   */
  @Override
  public CommandItemLocationListType getCommandItemLocationList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemLocationListType target = null;
      target = (CommandItemLocationListType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemLocationList" element
   */
  @Override
  public void setCommandItemLocationList(final CommandItemLocationListType commandItemLocationList)
  {
    this.generatedSetterHelperImpl(
      commandItemLocationList,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemLocationList" element
   */
  @Override
  public CommandItemLocationListType addNewCommandItemLocationList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemLocationListType target = null;
      target = (CommandItemLocationListType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
