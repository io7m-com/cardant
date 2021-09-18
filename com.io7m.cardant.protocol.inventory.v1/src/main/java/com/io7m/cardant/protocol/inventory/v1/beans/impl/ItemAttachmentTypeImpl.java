/*
 * XML Type:  ItemAttachmentType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType;
import com.io7m.cardant.protocol.inventory.v1.beans.HashValueType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDescriptionType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType;
import com.io7m.cardant.protocol.inventory.v1.beans.MediaType;
import com.io7m.cardant.protocol.inventory.v1.beans.RelationType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlUnsignedLong;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;
import java.math.BigInteger;

/**
 * An XML ItemAttachmentType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemAttachmentTypeImpl extends XmlComplexContentImpl implements
  ItemAttachmentType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachmentData"),
    new QName("", "id"),
    new QName("", "description"),
    new QName("", "mediaType"),
    new QName("", "relation"),
    new QName("", "size"),
    new QName("", "hashAlgorithm"),
    new QName("", "hashValue"),
  };

  public ItemAttachmentTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemAttachmentData" element
   */
  @Override
  public byte[] getItemAttachmentData()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return (target == null) ? null : target.getByteArrayValue();
    }
  }

  /**
   * Sets the "ItemAttachmentData" element
   */
  @Override
  public void setItemAttachmentData(final byte[] itemAttachmentData)
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
      target.setByteArrayValue(itemAttachmentData);
    }
  }

  /**
   * Gets (as xml) the "ItemAttachmentData" element
   */
  @Override
  public ItemAttachmentDataType xgetItemAttachmentData()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentDataType target = null;
      target = (ItemAttachmentDataType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * True if has "ItemAttachmentData" element
   */
  @Override
  public boolean isSetItemAttachmentData()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
    }
  }

  /**
   * Sets (as xml) the "ItemAttachmentData" element
   */
  @Override
  public void xsetItemAttachmentData(final ItemAttachmentDataType itemAttachmentData)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentDataType target = null;
      target = (ItemAttachmentDataType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      if (target == null) {
        target = (ItemAttachmentDataType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(itemAttachmentData);
    }
  }

  /**
   * Unsets the "ItemAttachmentData" element
   */
  @Override
  public void unsetItemAttachmentData()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], 0);
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
        PROPERTY_QNAME[1]);
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
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
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
        PROPERTY_QNAME[1]);
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
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (UUIDType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.set(id);
    }
  }

  /**
   * Gets the "description" attribute
   */
  @Override
  public String getDescription()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "description" attribute
   */
  @Override
  public void setDescription(final String description)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
      }
      target.setStringValue(description);
    }
  }

  /**
   * Gets (as xml) the "description" attribute
   */
  @Override
  public ItemAttachmentDescriptionType xgetDescription()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentDescriptionType target = null;
      target = (ItemAttachmentDescriptionType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "description" attribute
   */
  @Override
  public void xsetDescription(final ItemAttachmentDescriptionType description)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentDescriptionType target = null;
      target = (ItemAttachmentDescriptionType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (ItemAttachmentDescriptionType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
      }
      target.set(description);
    }
  }

  /**
   * Gets the "mediaType" attribute
   */
  @Override
  public String getMediaType()
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
   * Sets the "mediaType" attribute
   */
  @Override
  public void setMediaType(final String mediaType)
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
      target.setStringValue(mediaType);
    }
  }

  /**
   * Gets (as xml) the "mediaType" attribute
   */
  @Override
  public MediaType xgetMediaType()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      MediaType target = null;
      target = (MediaType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "mediaType" attribute
   */
  @Override
  public void xsetMediaType(final MediaType mediaType)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      MediaType target = null;
      target = (MediaType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      if (target == null) {
        target = (MediaType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[3]);
      }
      target.set(mediaType);
    }
  }

  /**
   * Gets the "relation" attribute
   */
  @Override
  public String getRelation()
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
   * Sets the "relation" attribute
   */
  @Override
  public void setRelation(final String relation)
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
      target.setStringValue(relation);
    }
  }

  /**
   * Gets (as xml) the "relation" attribute
   */
  @Override
  public RelationType xgetRelation()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      RelationType target = null;
      target = (RelationType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[4]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "relation" attribute
   */
  @Override
  public void xsetRelation(final RelationType relation)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      RelationType target = null;
      target = (RelationType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[4]);
      if (target == null) {
        target = (RelationType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[4]);
      }
      target.set(relation);
    }
  }

  /**
   * Gets the "size" attribute
   */
  @Override
  public BigInteger getSize()
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
   * Sets the "size" attribute
   */
  @Override
  public void setSize(final BigInteger size)
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
      target.setBigIntegerValue(size);
    }
  }

  /**
   * Gets (as xml) the "size" attribute
   */
  @Override
  public XmlUnsignedLong xgetSize()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlUnsignedLong target = null;
      target = (XmlUnsignedLong) this.get_store().find_attribute_user(
        PROPERTY_QNAME[5]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "size" attribute
   */
  @Override
  public void xsetSize(final XmlUnsignedLong size)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlUnsignedLong target = null;
      target = (XmlUnsignedLong) this.get_store().find_attribute_user(
        PROPERTY_QNAME[5]);
      if (target == null) {
        target = (XmlUnsignedLong) this.get_store().add_attribute_user(
          PROPERTY_QNAME[5]);
      }
      target.set(size);
    }
  }

  /**
   * Gets the "hashAlgorithm" attribute
   */
  @Override
  public String getHashAlgorithm()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[6]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "hashAlgorithm" attribute
   */
  @Override
  public void setHashAlgorithm(final String hashAlgorithm)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[6]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[6]);
      }
      target.setStringValue(hashAlgorithm);
    }
  }

  /**
   * Gets (as xml) the "hashAlgorithm" attribute
   */
  @Override
  public HashAlgorithmType xgetHashAlgorithm()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      HashAlgorithmType target = null;
      target = (HashAlgorithmType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[6]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "hashAlgorithm" attribute
   */
  @Override
  public void xsetHashAlgorithm(final HashAlgorithmType hashAlgorithm)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      HashAlgorithmType target = null;
      target = (HashAlgorithmType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[6]);
      if (target == null) {
        target = (HashAlgorithmType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[6]);
      }
      target.set(hashAlgorithm);
    }
  }

  /**
   * Gets the "hashValue" attribute
   */
  @Override
  public String getHashValue()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[7]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "hashValue" attribute
   */
  @Override
  public void setHashValue(final String hashValue)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[7]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[7]);
      }
      target.setStringValue(hashValue);
    }
  }

  /**
   * Gets (as xml) the "hashValue" attribute
   */
  @Override
  public HashValueType xgetHashValue()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      HashValueType target = null;
      target = (HashValueType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[7]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "hashValue" attribute
   */
  @Override
  public void xsetHashValue(final HashValueType hashValue)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      HashValueType target = null;
      target = (HashValueType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[7]);
      if (target == null) {
        target = (HashValueType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[7]);
      }
      target.set(hashValue);
    }
  }
}
