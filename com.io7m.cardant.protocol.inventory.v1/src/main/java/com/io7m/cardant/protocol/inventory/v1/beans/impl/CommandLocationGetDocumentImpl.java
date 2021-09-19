/*
 * An XML document type.
 * Localname: CommandLocationGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandLocationGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandLocationGetDocumentImpl extends CommandDocumentImpl implements
  CommandLocationGetDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationGet"),
  };

  public CommandLocationGetDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandLocationGet" element
   */
  @Override
  public CommandLocationGetType getCommandLocationGet()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandLocationGetType target = null;
      target = (CommandLocationGetType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandLocationGet" element
   */
  @Override
  public void setCommandLocationGet(final CommandLocationGetType commandLocationGet)
  {
    this.generatedSetterHelperImpl(
      commandLocationGet,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandLocationGet" element
   */
  @Override
  public CommandLocationGetType addNewCommandLocationGet()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandLocationGetType target = null;
      target = (CommandLocationGetType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
