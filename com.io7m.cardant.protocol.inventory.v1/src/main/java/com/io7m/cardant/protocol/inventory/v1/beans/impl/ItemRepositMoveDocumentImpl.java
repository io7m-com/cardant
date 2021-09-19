/*
 * An XML document type.
 * Localname: ItemRepositMove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemRepositMove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemRepositMoveDocumentImpl extends ItemRepositDocumentImpl implements
  ItemRepositMoveDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositMove"),
  };

  public ItemRepositMoveDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemRepositMove" element
   */
  @Override
  public ItemRepositMoveType getItemRepositMove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemRepositMoveType target = null;
      target = (ItemRepositMoveType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemRepositMove" element
   */
  @Override
  public void setItemRepositMove(final ItemRepositMoveType itemRepositMove)
  {
    this.generatedSetterHelperImpl(
      itemRepositMove,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemRepositMove" element
   */
  @Override
  public ItemRepositMoveType addNewItemRepositMove()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemRepositMoveType target = null;
      target = (ItemRepositMoveType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
