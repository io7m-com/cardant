/*
 * An XML document type.
 * Localname: ItemAttachmentID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemAttachmentID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemAttachmentIDDocumentImpl extends IDDocumentImpl implements
  ItemAttachmentIDDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachmentID"),
  };

  public ItemAttachmentIDDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemAttachmentID" element
   */
  @Override
  public ItemAttachmentIDType getItemAttachmentID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentIDType target = null;
      target = (ItemAttachmentIDType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemAttachmentID" element
   */
  @Override
  public void setItemAttachmentID(final ItemAttachmentIDType itemAttachmentID)
  {
    this.generatedSetterHelperImpl(
      itemAttachmentID,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemAttachmentID" element
   */
  @Override
  public ItemAttachmentIDType addNewItemAttachmentID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentIDType target = null;
      target = (ItemAttachmentIDType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
