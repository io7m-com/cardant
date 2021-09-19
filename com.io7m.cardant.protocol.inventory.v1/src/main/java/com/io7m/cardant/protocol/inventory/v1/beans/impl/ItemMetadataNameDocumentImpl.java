/*
 * An XML document type.
 * Localname: ItemMetadataName
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemMetadataName(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemMetadataNameDocumentImpl extends XmlComplexContentImpl implements
  ItemMetadataNameDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadataName"),
  };

  public ItemMetadataNameDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemMetadataName" element
   */
  @Override
  public String getItemMetadataName()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "ItemMetadataName" element
   */
  @Override
  public void setItemMetadataName(final String itemMetadataName)
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
      target.setStringValue(itemMetadataName);
    }
  }

  /**
   * Gets (as xml) the "ItemMetadataName" element
   */
  @Override
  public ItemMetadataNameType xgetItemMetadataName()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataNameType target = null;
      target = (ItemMetadataNameType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets (as xml) the "ItemMetadataName" element
   */
  @Override
  public void xsetItemMetadataName(final ItemMetadataNameType itemMetadataName)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataNameType target = null;
      target = (ItemMetadataNameType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      if (target == null) {
        target = (ItemMetadataNameType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(itemMetadataName);
    }
  }
}
