/*
 * XML Type:  ItemType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.math.BigInteger;


/**
 * An XML ItemType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemType extends XmlObject
{
  DocumentFactory<ItemType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemtype2a8atype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemMetadatas" element
   */
  ItemMetadatasType getItemMetadatas();

  /**
   * Sets the "ItemMetadatas" element
   */
  void setItemMetadatas(ItemMetadatasType itemMetadatas);

  /**
   * Appends and returns a new empty "ItemMetadatas" element
   */
  ItemMetadatasType addNewItemMetadatas();

  /**
   * Gets the "Tags" element
   */
  TagsType getTags();

  /**
   * Sets the "Tags" element
   */
  void setTags(TagsType tags);

  /**
   * Appends and returns a new empty "Tags" element
   */
  TagsType addNewTags();

  /**
   * Gets the "ItemAttachments" element
   */
  ItemAttachmentsType getItemAttachments();

  /**
   * Sets the "ItemAttachments" element
   */
  void setItemAttachments(ItemAttachmentsType itemAttachments);

  /**
   * Appends and returns a new empty "ItemAttachments" element
   */
  ItemAttachmentsType addNewItemAttachments();

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
   * Gets the "name" attribute
   */
  String getName();

  /**
   * Sets the "name" attribute
   */
  void setName(String name);

  /**
   * Gets (as xml) the "name" attribute
   */
  ItemNameType xgetName();

  /**
   * Sets (as xml) the "name" attribute
   */
  void xsetName(ItemNameType name);

  /**
   * Gets the "count" attribute
   */
  BigInteger getCount();

  /**
   * Sets the "count" attribute
   */
  void setCount(BigInteger count);

  /**
   * Gets (as xml) the "count" attribute
   */
  XmlInteger xgetCount();

  /**
   * Sets (as xml) the "count" attribute
   */
  void xsetCount(XmlInteger count);
}
