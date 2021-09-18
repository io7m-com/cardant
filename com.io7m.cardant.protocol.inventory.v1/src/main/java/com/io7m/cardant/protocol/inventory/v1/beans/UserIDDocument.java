/*
 * An XML document type.
 * Localname: UserID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.UserIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one UserID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface UserIDDocument extends IDDocument
{
  DocumentFactory<UserIDDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "useridce21doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "UserID" element
   */
  UserIDType getUserID();

  /**
   * Sets the "UserID" element
   */
  void setUserID(UserIDType userID);

  /**
   * Appends and returns a new empty "UserID" element
   */
  UserIDType addNewUserID();
}
