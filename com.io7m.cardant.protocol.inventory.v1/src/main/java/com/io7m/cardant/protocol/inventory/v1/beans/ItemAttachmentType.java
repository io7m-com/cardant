/*
 * XML Type:  ItemAttachmentType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedLong;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.math.BigInteger;


/**
 * An XML ItemAttachmentType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemAttachmentType extends XmlObject
{
  DocumentFactory<ItemAttachmentType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemattachmenttypee627type");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemAttachmentData" element
   */
  byte[] getItemAttachmentData();

  /**
   * Sets the "ItemAttachmentData" element
   */
  void setItemAttachmentData(byte[] itemAttachmentData);

  /**
   * Gets (as xml) the "ItemAttachmentData" element
   */
  ItemAttachmentDataType xgetItemAttachmentData();

  /**
   * True if has "ItemAttachmentData" element
   */
  boolean isSetItemAttachmentData();

  /**
   * Sets (as xml) the "ItemAttachmentData" element
   */
  void xsetItemAttachmentData(ItemAttachmentDataType itemAttachmentData);

  /**
   * Unsets the "ItemAttachmentData" element
   */
  void unsetItemAttachmentData();

  /**
   * Gets the "id" attribute
   */
  String getId();

  /**
   * Sets the "id" attribute
   */
  void setId(String id);

  /**
   * Gets (as xml) the "id" attribute
   */
  UUIDType xgetId();

  /**
   * Sets (as xml) the "id" attribute
   */
  void xsetId(UUIDType id);

  /**
   * Gets the "description" attribute
   */
  String getDescription();

  /**
   * Sets the "description" attribute
   */
  void setDescription(String description);

  /**
   * Gets (as xml) the "description" attribute
   */
  ItemAttachmentDescriptionType xgetDescription();

  /**
   * Sets (as xml) the "description" attribute
   */
  void xsetDescription(ItemAttachmentDescriptionType description);

  /**
   * Gets the "mediaType" attribute
   */
  String getMediaType();

  /**
   * Sets the "mediaType" attribute
   */
  void setMediaType(String mediaType);

  /**
   * Gets (as xml) the "mediaType" attribute
   */
  MediaType xgetMediaType();

  /**
   * Sets (as xml) the "mediaType" attribute
   */
  void xsetMediaType(MediaType mediaType);

  /**
   * Gets the "relation" attribute
   */
  String getRelation();

  /**
   * Sets the "relation" attribute
   */
  void setRelation(String relation);

  /**
   * Gets (as xml) the "relation" attribute
   */
  RelationType xgetRelation();

  /**
   * Sets (as xml) the "relation" attribute
   */
  void xsetRelation(RelationType relation);

  /**
   * Gets the "size" attribute
   */
  BigInteger getSize();

  /**
   * Sets the "size" attribute
   */
  void setSize(BigInteger size);

  /**
   * Gets (as xml) the "size" attribute
   */
  XmlUnsignedLong xgetSize();

  /**
   * Sets (as xml) the "size" attribute
   */
  void xsetSize(XmlUnsignedLong size);

  /**
   * Gets the "hashAlgorithm" attribute
   */
  String getHashAlgorithm();

  /**
   * Sets the "hashAlgorithm" attribute
   */
  void setHashAlgorithm(String hashAlgorithm);

  /**
   * Gets (as xml) the "hashAlgorithm" attribute
   */
  HashAlgorithmType xgetHashAlgorithm();

  /**
   * Sets (as xml) the "hashAlgorithm" attribute
   */
  void xsetHashAlgorithm(HashAlgorithmType hashAlgorithm);

  /**
   * Gets the "hashValue" attribute
   */
  String getHashValue();

  /**
   * Sets the "hashValue" attribute
   */
  void setHashValue(String hashValue);

  /**
   * Gets (as xml) the "hashValue" attribute
   */
  HashValueType xgetHashValue();

  /**
   * Sets (as xml) the "hashValue" attribute
   */
  void xsetHashValue(HashValueType hashValue);
}
