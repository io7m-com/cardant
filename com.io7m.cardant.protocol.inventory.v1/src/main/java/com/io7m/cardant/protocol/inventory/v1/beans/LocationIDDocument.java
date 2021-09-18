/*
 * An XML document type.
 * Localname: LocationID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one LocationID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface LocationIDDocument extends IDDocument
{
  DocumentFactory<LocationIDDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "locationidc677doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "LocationID" element
   */
  LocationIDType getLocationID();

  /**
   * Sets the "LocationID" element
   */
  void setLocationID(LocationIDType locationID);

  /**
   * Appends and returns a new empty "LocationID" element
   */
  LocationIDType addNewLocationID();
}
