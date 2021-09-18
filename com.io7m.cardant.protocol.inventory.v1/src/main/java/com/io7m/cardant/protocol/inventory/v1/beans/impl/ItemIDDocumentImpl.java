/*
 * An XML document type.
 * Localname: ItemID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemIDDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemIDDocumentImpl extends IDDocumentImpl implements
  ItemIDDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemID"),
  };

  public ItemIDDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemID" element
   */
  @Override
  public ItemIDType getItemID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemIDType target = null;
      target = (ItemIDType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemID" element
   */
  @Override
  public void setItemID(final ItemIDType itemID)
  {
    this.generatedSetterHelperImpl(
      itemID,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemID" element
   */
  @Override
  public ItemIDType addNewItemID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemIDType target = null;
      target = (ItemIDType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
