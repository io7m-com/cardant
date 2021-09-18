/*
 * An XML document type.
 * Localname: CommandTagsDelete
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one CommandTagsDelete(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandTagsDeleteDocumentImpl extends CommandDocumentImpl implements
  CommandTagsDeleteDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsDelete"),
  };

  public CommandTagsDeleteDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "CommandTagsDelete" element
   */
  @Override
  public CommandTagsDeleteType getCommandTagsDelete()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandTagsDeleteType target = null;
      target = (CommandTagsDeleteType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "CommandTagsDelete" element
   */
  @Override
  public void setCommandTagsDelete(final CommandTagsDeleteType commandTagsDelete)
  {
    this.generatedSetterHelperImpl(
      commandTagsDelete,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "CommandTagsDelete" element
   */
  @Override
  public CommandTagsDeleteType addNewCommandTagsDelete()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandTagsDeleteType target = null;
      target = (CommandTagsDeleteType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
