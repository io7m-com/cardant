/*
 * An XML document type.
 * Localname: CommandItemGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemGetDocumentImpl extends CommandDocumentImpl implements
  CommandItemGetDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemGet"),
  };

  public CommandItemGetDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemGet" element
   */
  @Override
  public CommandItemGetType getCommandItemGet()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemGetType target = null;
      target = (CommandItemGetType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemGet" element
   */
  @Override
  public void setCommandItemGet(final CommandItemGetType commandItemGet)
  {
    this.generatedSetterHelperImpl(
      commandItemGet,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemGet" element
   */
  @Override
  public CommandItemGetType addNewCommandItemGet()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemGetType target = null;
      target = (CommandItemGetType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
