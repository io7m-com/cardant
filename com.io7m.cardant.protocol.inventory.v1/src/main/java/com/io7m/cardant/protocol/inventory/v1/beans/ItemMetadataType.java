/*
 * XML Type:  ItemMetadataType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML ItemMetadataType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemMetadataType extends XmlObject
{
  DocumentFactory<ItemMetadataType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemmetadatatype5c9btype");
  SchemaType type = Factory.getType();


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
  ItemMetadataNameType xgetName();

  /**
   * Sets (as xml) the "name" attribute
   */
  void xsetName(ItemMetadataNameType name);

  /**
   * Gets the "value" attribute
   */
  String getValue();

  /**
   * Sets the "value" attribute
   */
  void setValue(String value);

  /**
   * Gets (as xml) the "value" attribute
   */
  ItemMetadataValueType xgetValue();

  /**
   * Sets (as xml) the "value" attribute
   */
  void xsetValue(ItemMetadataValueType value);
}
