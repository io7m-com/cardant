/*
 * XML Type:  ListLocationWithDescendantsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML ListLocationWithDescendantsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ListLocationWithDescendantsType extends ListLocationsBehaviourType
{
  DocumentFactory<ListLocationWithDescendantsType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "listlocationwithdescendantstypec752type");
  SchemaType type = Factory.getType();


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
}
