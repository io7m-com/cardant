/*
 * An XML document type.
 * Localname: Location
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Location(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface LocationDocument extends XmlObject
{
  DocumentFactory<LocationDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "locationd4b2doctype");
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
