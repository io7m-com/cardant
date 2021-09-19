/*
 * XML Type:  LocationType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML LocationType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface LocationType extends XmlObject
{
  DocumentFactory<LocationType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "locationtypeffa8type");
  SchemaType type = Factory.getType();


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
   * Gets the "parent" attribute
   */
  String getParent();

  /**
   * Sets the "parent" attribute
   */
  void setParent(String parent);

  /**
   * Gets (as xml) the "parent" attribute
   */
  UUIDType xgetParent();

  /**
   * True if has "parent" attribute
   */
  boolean isSetParent();

  /**
   * Sets (as xml) the "parent" attribute
   */
  void xsetParent(UUIDType parent);

  /**
   * Unsets the "parent" attribute
   */
  void unsetParent();

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
  LocationNameType xgetName();

  /**
   * Sets (as xml) the "name" attribute
   */
  void xsetName(LocationNameType name);

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
  LocationDescriptionType xgetDescription();

  /**
   * Sets (as xml) the "description" attribute
   */
  void xsetDescription(LocationDescriptionType description);
}
