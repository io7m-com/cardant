/*
 * XML Type:  UpdatedType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.util.List;


/**
 * An XML UpdatedType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface UpdatedType extends XmlObject
{
  DocumentFactory<UpdatedType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "updatedtypeb354type");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "ID" elements
   */
  List<IDType> getIDList();

  /**
   * Gets array of all "ID" elements
   */
  IDType[] getIDArray();

  /**
   * Sets array of all "ID" element
   */
  void setIDArray(IDType[] idArray);

  /**
   * Gets ith "ID" element
   */
  IDType getIDArray(int i);

  /**
   * Returns number of "ID" element
   */
  int sizeOfIDArray();

  /**
   * Sets ith "ID" element
   */
  void setIDArray(
    int i,
    IDType id);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ID" element
   */
  IDType insertNewID(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "ID" element
   */
  IDType addNewID();

  /**
   * Removes the ith "ID" element
   */
  void removeID(int i);
}
