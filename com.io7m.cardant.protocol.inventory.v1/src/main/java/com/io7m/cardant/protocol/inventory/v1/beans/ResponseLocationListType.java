/*
 * XML Type:  ResponseLocationListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.util.List;


/**
 * An XML ResponseLocationListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseLocationListType extends ResponseType
{
  DocumentFactory<ResponseLocationListType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responselocationlisttypedb69type");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "Location" elements
   */
  List<LocationType> getLocationList();

  /**
   * Gets array of all "Location" elements
   */
  LocationType[] getLocationArray();

  /**
   * Sets array of all "Location" element
   */
  void setLocationArray(LocationType[] locationArray);

  /**
   * Gets ith "Location" element
   */
  LocationType getLocationArray(int i);

  /**
   * Returns number of "Location" element
   */
  int sizeOfLocationArray();

  /**
   * Sets ith "Location" element
   */
  void setLocationArray(
    int i,
    LocationType location);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "Location" element
   */
  LocationType insertNewLocation(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "Location" element
   */
  LocationType addNewLocation();

  /**
   * Removes the ith "Location" element
   */
  void removeLocation(int i);
}
