/*
 * An XML document type.
 * Localname: CommandItemCreate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandItemCreate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemCreateDocumentImpl extends CommandDocumentImpl implements
  CommandItemCreateDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandItemCreate"),
  };

  public CommandItemCreateDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandItemCreate" element
   */
  @Override
  public CommandItemCreateType getCommandItemCreate()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemCreateType target = null;
      target = (CommandItemCreateType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandItemCreate" element
   */
  @Override
  public void setCommandItemCreate(final CommandItemCreateType commandItemCreate)
  {
    this.generatedSetterHelperImpl(
      commandItemCreate,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandItemCreate" element
   */
  @Override
  public CommandItemCreateType addNewCommandItemCreate()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandItemCreateType target = null;
      target = (CommandItemCreateType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
