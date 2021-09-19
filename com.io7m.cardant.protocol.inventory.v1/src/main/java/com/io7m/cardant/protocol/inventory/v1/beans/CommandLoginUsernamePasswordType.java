/*
 * XML Type:  CommandLoginUsernamePasswordType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML CommandLoginUsernamePasswordType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandLoginUsernamePasswordType extends CommandType
{
  DocumentFactory<CommandLoginUsernamePasswordType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commandloginusernamepasswordtypedd8etype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "user" attribute
   */
  String getUser();

  /**
   * Sets the "user" attribute
   */
  void setUser(String user);

  /**
   * Gets (as xml) the "user" attribute
   */
  XmlToken xgetUser();

  /**
   * Sets (as xml) the "user" attribute
   */
  void xsetUser(XmlToken user);

  /**
   * Gets the "password" attribute
   */
  String getPassword();

  /**
   * Sets the "password" attribute
   */
  void setPassword(String password);

  /**
   * Gets (as xml) the "password" attribute
   */
  XmlString xgetPassword();

  /**
   * Sets (as xml) the "password" attribute
   */
  void xsetPassword(XmlString password);
}
