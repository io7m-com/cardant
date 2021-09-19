/*
 * An XML document type.
 * Localname: ItemAttachment
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemAttachment(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemAttachmentDocumentImpl extends XmlComplexContentImpl implements
  ItemAttachmentDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachment"),
  };

  public ItemAttachmentDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemAttachment" element
   */
  @Override
  public ItemAttachmentType getItemAttachment()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentType target = null;
      target = (ItemAttachmentType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemAttachment" element
   */
  @Override
  public void setItemAttachment(final ItemAttachmentType itemAttachment)
  {
    this.generatedSetterHelperImpl(
      itemAttachment,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemAttachment" element
   */
  @Override
  public ItemAttachmentType addNewItemAttachment()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentType target = null;
      target = (ItemAttachmentType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
