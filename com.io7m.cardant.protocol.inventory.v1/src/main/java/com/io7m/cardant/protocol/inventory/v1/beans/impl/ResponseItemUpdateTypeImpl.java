/*
 * XML Type:  ResponseItemUpdateType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * An XML ResponseItemUpdateType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseItemUpdateTypeImpl extends ResponseTypeImpl implements
  ResponseItemUpdateType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Item"),
  };

  public ResponseItemUpdateTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Item" element
   */
  @Override
  public ItemType getItem()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemType target = null;
      target = (ItemType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Item" element
   */
  @Override
  public void setItem(final ItemType item)
  {
    this.generatedSetterHelperImpl(
      item,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "Item" element
   */
  @Override
  public ItemType addNewItem()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemType target = null;
      target = (ItemType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
