/*
 * XML Type:  CommandLocationPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML CommandLocationPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandLocationPutType extends CommandType
{
  DocumentFactory<CommandLocationPutType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandlocationputtype980etype");
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
