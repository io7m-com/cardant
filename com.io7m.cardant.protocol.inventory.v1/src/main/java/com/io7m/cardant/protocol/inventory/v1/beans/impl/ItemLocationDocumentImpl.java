/*
 * An XML document type.
 * Localname: ItemLocation
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemLocation(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemLocationDocumentImpl extends XmlComplexContentImpl implements
  ItemLocationDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemLocation"),
  };

  public ItemLocationDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemLocation" element
   */
  @Override
  public ItemLocationType getItemLocation()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemLocationType target = null;
      target = (ItemLocationType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemLocation" element
   */
  @Override
  public void setItemLocation(final ItemLocationType itemLocation)
  {
    this.generatedSetterHelperImpl(
      itemLocation,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemLocation" element
   */
  @Override
  public ItemLocationType addNewItemLocation()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemLocationType target = null;
      target = (ItemLocationType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
