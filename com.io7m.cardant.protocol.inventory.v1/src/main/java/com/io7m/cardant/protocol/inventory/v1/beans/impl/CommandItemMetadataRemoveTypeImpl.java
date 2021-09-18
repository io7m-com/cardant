/*
 * XML Type:  CommandItemMetadataRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.JavaListObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An XML CommandItemMetadataRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemMetadataRemoveTypeImpl extends CommandTypeImpl implements
  CommandItemMetadataRemoveType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadataName"),
    new QName("", "item"),
  };

  public CommandItemMetadataRemoveTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "ItemMetadataName" elements
   */
  @Override
  public List<String> getItemMetadataNameList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListObject<>(
        this::getItemMetadataNameArray,
        this::setItemMetadataNameArray,
        this::insertItemMetadataName,
        this::removeItemMetadataName,
        this::sizeOfItemMetadataNameArray
      );
    }
  }

  /**
   * Gets array of all "ItemMetadataName" elements
   */
  @Override
  public String[] getItemMetadataNameArray()
  {
    return this.getObjectArray(
      PROPERTY_QNAME[0],
      SimpleValue::getStringValue,
      String[]::new);
  }

  /**
   * Sets array of all "ItemMetadataName" element
   */
  @Override
  public void setItemMetadataNameArray(final String[] itemMetadataNameArray)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.arraySetterHelper(itemMetadataNameArray, PROPERTY_QNAME[0]);
    }
  }

  /**
   * Gets ith "ItemMetadataName" element
   */
  @Override
  public String getItemMetadataNameArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target.getStringValue();
    }
  }

  /**
   * Gets (as xml) a List of "ItemMetadataName" elements
   */
  @Override
  public List<ItemMetadataNameType> xgetItemMetadataNameList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
        this::xgetItemMetadataNameArray,
        this::xsetItemMetadataNameArray,
        this::insertNewItemMetadataName,
        this::removeItemMetadataName,
        this::sizeOfItemMetadataNameArray
      );
    }
  }

  /**
   * Gets (as xml) array of all "ItemMetadataName" elements
   */
  @Override
  public ItemMetadataNameType[] xgetItemMetadataNameArray()
  {
    return this.xgetArray(
      PROPERTY_QNAME[0],
      ItemMetadataNameType[]::new);
  }

  /**
   * Gets (as xml) ith "ItemMetadataName" element
   */
  @Override
  public ItemMetadataNameType xgetItemMetadataNameArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataNameType target = null;
      target = (ItemMetadataNameType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target;
    }
  }

  /**
   * Returns number of "ItemMetadataName" element
   */
  @Override
  public int sizeOfItemMetadataNameArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]);
    }
  }

  /**
   * Sets ith "ItemMetadataName" element
   */
  @Override
  public void setItemMetadataNameArray(
    final int i,
    final String itemMetadataName)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      target.setStringValue(itemMetadataName);
    }
  }

  /**
   * Sets (as xml) array of all "ItemMetadataName" element
   */
  @Override
  public void xsetItemMetadataNameArray(final ItemMetadataNameType[] itemMetadataNameArray)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.arraySetterHelper(itemMetadataNameArray, PROPERTY_QNAME[0]);
    }
  }

  /**
   * Sets (as xml) ith "ItemMetadataName" element
   */
  @Override
  public void xsetItemMetadataNameArray(
    final int i,
    final ItemMetadataNameType itemMetadataName)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataNameType target = null;
      target = (ItemMetadataNameType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      target.set(itemMetadataName);
    }
  }

  /**
   * Inserts the value as the ith "ItemMetadataName" element
   */
  @Override
  public void insertItemMetadataName(
    final int i,
    final String itemMetadataName)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      final SimpleValue target =
        (SimpleValue) this.get_store().insert_element_user(
          PROPERTY_QNAME[0],
          i);
      target.setStringValue(itemMetadataName);
    }
  }

  /**
   * Appends the value as the last "ItemMetadataName" element
   */
  @Override
  public void addItemMetadataName(final String itemMetadataName)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      target.setStringValue(itemMetadataName);
    }
  }

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ItemMetadataName" element
   */
  @Override
  public ItemMetadataNameType insertNewItemMetadataName(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataNameType target = null;
      target = (ItemMetadataNameType) this.get_store().insert_element_user(
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "ItemMetadataName" element
   */
  @Override
  public ItemMetadataNameType addNewItemMetadataName()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataNameType target = null;
      target = (ItemMetadataNameType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Removes the ith "ItemMetadataName" element
   */
  @Override
  public void removeItemMetadataName(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], i);
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
