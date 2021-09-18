/*
 * XML Type:  ListLocationExactType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML ListLocationExactType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ListLocationExactType extends ListLocationsBehaviourType
{
  DocumentFactory<ListLocationExactType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "listlocationexacttypeff23type");
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
