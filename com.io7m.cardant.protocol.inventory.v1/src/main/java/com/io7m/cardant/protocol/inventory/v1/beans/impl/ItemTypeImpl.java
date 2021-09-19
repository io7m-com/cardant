/*
 * XML Type:  ItemType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemType;
import com.io7m.cardant.protocol.inventory.v1.beans.TagsType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;
import java.math.BigInteger;

/**
 * An XML ItemType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemTypeImpl extends XmlComplexContentImpl implements
  ItemType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadatas"),
    new QName("urn:com.io7m.cardant.inventory:1", "Tags"),
    new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachments"),
    new QName("", "id"),
    new QName("", "name"),
    new QName("", "count"),
  };

  public ItemTypeImpl(final SchemaType sType)
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
   * Gets the "Tags" element
   */
  @Override
  public TagsType getTags()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TagsType target = null;
      target = (TagsType) this.get_store().find_element_user(
        PROPERTY_QNAME[1],
        0);
      return target;
    }
  }

  /**
   * Sets the "Tags" element
   */
  @Override
  public void setTags(final TagsType tags)
  {
    this.generatedSetterHelperImpl(
      tags,
      PROPERTY_QNAME[1],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "Tags" element
   */
  @Override
  public TagsType addNewTags()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TagsType target = null;
      target = (TagsType) this.get_store().add_element_user(
        PROPERTY_QNAME[1]);
      return target;
    }
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
        PROPERTY_QNAME[2],
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
      PROPERTY_QNAME[2],
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
        PROPERTY_QNAME[2]);
      return target;
    }
  }

  /**
   * Gets the "id" attribute
   */
  @Override
  public String getId()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "id" attribute
   */
  @Override
  public void setId(final String id)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[3]);
      }
      target.setStringValue(id);
    }
  }

  /**
   * Gets (as xml) the "id" attribute
   */
  @Override
  public UUIDType xgetId()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UUIDType target = null;
      target = (UUIDType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "id" attribute
   */
  @Override
  public void xsetId(final UUIDType id)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UUIDType target = null;
      target = (UUIDType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      if (target == null) {
        target = (UUIDType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[3]);
      }
      target.set(id);
    }
  }

  /**
   * Gets the "name" attribute
   */
  @Override
  public String getName()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[4]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "name" attribute
   */
  @Override
  public void setName(final String name)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[4]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[4]);
      }
      target.setStringValue(name);
    }
  }

  /**
   * Gets (as xml) the "name" attribute
   */
  @Override
  public ItemNameType xgetName()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemNameType target = null;
      target = (ItemNameType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[4]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "name" attribute
   */
  @Override
  public void xsetName(final ItemNameType name)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemNameType target = null;
      target = (ItemNameType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[4]);
      if (target == null) {
        target = (ItemNameType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[4]);
      }
      target.set(name);
    }
  }

  /**
   * Gets the "count" attribute
   */
  @Override
  public BigInteger getCount()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[5]);
      return (target == null) ? null : target.getBigIntegerValue();
    }
  }

  /**
   * Sets the "count" attribute
   */
  @Override
  public void setCount(final BigInteger count)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[5]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[5]);
      }
      target.setBigIntegerValue(count);
    }
  }

  /**
   * Gets (as xml) the "count" attribute
   */
  @Override
  public XmlInteger xgetCount()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlInteger target = null;
      target = (XmlInteger) this.get_store().find_attribute_user(
        PROPERTY_QNAME[5]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "count" attribute
   */
  @Override
  public void xsetCount(final XmlInteger count)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlInteger target = null;
      target = (XmlInteger) this.get_store().find_attribute_user(
        PROPERTY_QNAME[5]);
      if (target == null) {
        target = (XmlInteger) this.get_store().add_attribute_user(
          PROPERTY_QNAME[5]);
      }
      target.set(count);
    }
  }
}
