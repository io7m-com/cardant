/*
 * XML Type:  ItemMetadatasType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An XML ItemMetadatasType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemMetadatasTypeImpl extends XmlComplexContentImpl implements
  ItemMetadatasType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadata"),
  };

  public ItemMetadatasTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "ItemMetadata" elements
   */
  @Override
  public List<ItemMetadataType> getItemMetadataList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
        this::getItemMetadataArray,
        this::setItemMetadataArray,
        this::insertNewItemMetadata,
        this::removeItemMetadata,
        this::sizeOfItemMetadataArray
      );
    }
  }

  /**
   * Gets array of all "ItemMetadata" elements
   */
  @Override
  public ItemMetadataType[] getItemMetadataArray()
  {
    return this.getXmlObjectArray(
      PROPERTY_QNAME[0],
      new ItemMetadataType[0]);
  }

  /**
   * Sets array of all "ItemMetadata" element  WARNING: This method is not atomicaly synchronized.
   */
  @Override
  public void setItemMetadataArray(final ItemMetadataType[] itemMetadataArray)
  {
    this.check_orphaned();
    this.arraySetterHelper(itemMetadataArray, PROPERTY_QNAME[0]);
  }

  /**
   * Gets ith "ItemMetadata" element
   */
  @Override
  public ItemMetadataType getItemMetadataArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataType target = null;
      target = (ItemMetadataType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target;
    }
  }

  /**
   * Returns number of "ItemMetadata" element
   */
  @Override
  public int sizeOfItemMetadataArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]);
    }
  }

  /**
   * Sets ith "ItemMetadata" element
   */
  @Override
  public void setItemMetadataArray(
    final int i,
    final ItemMetadataType itemMetadata)
  {
    this.generatedSetterHelperImpl(
      itemMetadata,
      PROPERTY_QNAME[0],
      i,
      XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
  }

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ItemMetadata" element
   */
  @Override
  public ItemMetadataType insertNewItemMetadata(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataType target = null;
      target = (ItemMetadataType) this.get_store().insert_element_user(
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "ItemMetadata" element
   */
  @Override
  public ItemMetadataType addNewItemMetadata()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataType target = null;
      target = (ItemMetadataType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Removes the ith "ItemMetadata" element
   */
  @Override
  public void removeItemMetadata(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], i);
    }
  }
}
