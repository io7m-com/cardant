/*
 * XML Type:  ItemRepositMoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType
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
 * An XML ItemRepositMoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemRepositMoveType extends ItemRepositType
{
  DocumentFactory<ItemRepositMoveType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemrepositmovetype2f65type");
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
   * Gets the "fromLocation" attribute
   */
  String getFromLocation();

  /**
   * Sets the "fromLocation" attribute
   */
  void setFromLocation(String fromLocation);

  /**
   * Gets (as xml) the "fromLocation" attribute
   */
  UUIDType xgetFromLocation();

  /**
   * Sets (as xml) the "fromLocation" attribute
   */
  void xsetFromLocation(UUIDType fromLocation);

  /**
   * Gets the "toLocation" attribute
   */
  String getToLocation();

  /**
   * Sets the "toLocation" attribute
   */
  void setToLocation(String toLocation);

  /**
   * Gets (as xml) the "toLocation" attribute
   */
  UUIDType xgetToLocation();

  /**
   * Sets (as xml) the "toLocation" attribute
   */
  void xsetToLocation(UUIDType toLocation);

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
