/*
 * An XML document type.
 * Localname: CommandTagList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandTagList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandTagListDocumentImpl extends CommandDocumentImpl implements
  CommandTagListDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandTagList"),
  };

  public CommandTagListDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandTagList" element
   */
  @Override
  public CommandTagListType getCommandTagList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandTagListType target = null;
      target = (CommandTagListType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandTagList" element
   */
  @Override
  public void setCommandTagList(final CommandTagListType commandTagList)
  {
    this.generatedSetterHelperImpl(
      commandTagList,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandTagList" element
   */
  @Override
  public CommandTagListType addNewCommandTagList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandTagListType target = null;
      target = (CommandTagListType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
