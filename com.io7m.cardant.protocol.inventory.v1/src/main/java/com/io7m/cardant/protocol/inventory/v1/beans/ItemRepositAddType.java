/*
 * XML Type:  ItemRepositAddType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlUnsignedLong;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.math.BigInteger;


/**
 * An XML ItemRepositAddType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemRepositAddType extends ItemRepositType
{
  DocumentFactory<ItemRepositAddType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemrepositaddtyped9b5type");
  SchemaType type = Factory.getType();


  /**
   * Gets the "item" attribute
   */
  String getItem();

  /**
   * Sets the "item" attribute
   */
  void setItem(String item);

  /**
   * Gets (as xml) the "item" attribute
   */
  UUIDType xgetItem();

  /**
   * Sets (as xml) the "item" attribute
   */
  void xsetItem(UUIDType item);

  /**
   * Gets the "location" attribute
   */
  String getLocation();

  /**
   * Sets the "location" attribute
   */
  void setLocation(String location);

  /**
   * Gets (as xml) the "location" attribute
   */
  UUIDType xgetLocation();

  /**
   * Sets (as xml) the "location" attribute
   */
  void xsetLocation(UUIDType location);

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
  XmlUnsignedLong xgetCount();

  /**
   * Sets (as xml) the "count" attribute
   */
  void xsetCount(XmlUnsignedLong count);
}
