/*
 * XML Type:  CommandItemMetadataPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * An XML CommandItemMetadataPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemMetadataPutTypeImpl extends CommandTypeImpl implements
  CommandItemMetadataPutType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadatas"),
    new QName("", "item"),
  };

  public CommandItemMetadataPutTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemMetadatas" element
   */
  @Override
  public ItemMetadatasType getItemMetadatas()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadatasType target = null;
      target = (ItemMetadatasType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemMetadatas" element
   */
  @Override
  public void setItemMetadatas(final ItemMetadatasType itemMetadatas)
  {
    this.generatedSetterHelperImpl(
      itemMetadatas,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemMetadatas" element
   */
  @Override
  public ItemMetadatasType addNewItemMetadatas()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadatasType target = null;
      target = (ItemMetadatasType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Gets the "item" attribute
   */
  @Override
  public String getItem()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "item" attribute
   */
  @Override
  public void setItem(final String item)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.setStringValue(item);
    }
  }

  /**
   * Gets (as xml) the "item" attribute
   */
  @Override
  public UUIDType xgetItem()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UUIDType target = null;
      target = (UUIDType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "item" attribute
   */
  @Override
  public void xsetItem(final UUIDType item)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UUIDType target = null;
      target = (UUIDType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (UUIDType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.set(item);
    }
  }
}
