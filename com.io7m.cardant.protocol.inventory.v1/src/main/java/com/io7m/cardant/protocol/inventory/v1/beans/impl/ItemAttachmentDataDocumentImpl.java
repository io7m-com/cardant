/*
 * An XML document type.
 * Localname: ItemAttachmentData
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemAttachmentData(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemAttachmentDataDocumentImpl extends XmlComplexContentImpl implements
  ItemAttachmentDataDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachmentData"),
  };

  public ItemAttachmentDataDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemAttachmentData" element
   */
  @Override
  public byte[] getItemAttachmentData()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return (target == null) ? null : target.getByteArrayValue();
    }
  }

  /**
   * Sets the "ItemAttachmentData" element
   */
  @Override
  public void setItemAttachmentData(final byte[] itemAttachmentData)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.setByteArrayValue(itemAttachmentData);
    }
  }

  /**
   * Gets (as xml) the "ItemAttachmentData" element
   */
  @Override
  public ItemAttachmentDataType xgetItemAttachmentData()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentDataType target = null;
      target = (ItemAttachmentDataType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets (as xml) the "ItemAttachmentData" element
   */
  @Override
  public void xsetItemAttachmentData(final ItemAttachmentDataType itemAttachmentData)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentDataType target = null;
      target = (ItemAttachmentDataType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      if (target == null) {
        target = (ItemAttachmentDataType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(itemAttachmentData);
    }
  }
}
