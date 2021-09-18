/*
 * An XML document type.
 * Localname: ItemAttachments
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemAttachments(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemAttachmentsDocumentImpl extends XmlComplexContentImpl implements
  ItemAttachmentsDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachments"),
  };

  public ItemAttachmentsDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemAttachments" element
   */
  @Override
  public ItemAttachmentsType getItemAttachments()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentsType target = null;
      target = (ItemAttachmentsType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemAttachments" element
   */
  @Override
  public void setItemAttachments(final ItemAttachmentsType itemAttachments)
  {
    this.generatedSetterHelperImpl(
      itemAttachments,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemAttachments" element
   */
  @Override
  public ItemAttachmentsType addNewItemAttachments()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentsType target = null;
      target = (ItemAttachmentsType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
