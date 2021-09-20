/*
 * XML Type:  ResponseLocationPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML ResponseLocationPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseLocationPutType extends ResponseType
{
  DocumentFactory<ResponseLocationPutType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responselocationputtype1d36type");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Location" element
   */
  LocationType getLocation();

  /**
   * Sets the "Location" element
   */
  void setLocation(LocationType location);

  /**
   * Appends and returns a new empty "Location" element
   */
  LocationType addNewLocation();
}