/*
 * An XML document type.
 * Localname: ItemRepositAdd
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemRepositAdd(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemRepositAddDocumentImpl extends ItemRepositDocumentImpl implements
  ItemRepositAddDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositAdd"),
  };

  public ItemRepositAddDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemRepositAdd" element
   */
  @Override
  public ItemRepositAddType getItemRepositAdd()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemRepositAddType target = null;
      target = (ItemRepositAddType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemRepositAdd" element
   */
  @Override
  public void setItemRepositAdd(final ItemRepositAddType itemRepositAdd)
  {
    this.generatedSetterHelperImpl(
      itemRepositAdd,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemRepositAdd" element
   */
  @Override
  public ItemRepositAddType addNewItemRepositAdd()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemRepositAddType target = null;
      target = (ItemRepositAddType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
