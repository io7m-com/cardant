/*
 * XML Type:  CommandLoginUsernamePasswordType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML CommandLoginUsernamePasswordType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandLoginUsernamePasswordTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType {
    private static final long serialVersionUID = 1L;

    public CommandLoginUsernamePasswordTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "user"),
        new QName("", "password"),
    };


    /**
     * Gets the "user" attribute
     */
    @Override
    public java.lang.String getUser() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "user" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlToken xgetUser() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlToken target = null;
            target = (org.apache.xmlbeans.XmlToken)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Sets the "user" attribute
     */
    @Override
    public void setUser(java.lang.String user) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(user);
        }
    }

    /**
     * Sets (as xml) the "user" attribute
     */
    @Override
    public void xsetUser(org.apache.xmlbeans.XmlToken user) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlToken target = null;
            target = (org.apache.xmlbeans.XmlToken)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlToken)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(user);
        }
    }

    /**
     * Gets the "password" attribute
     */
    @Override
    public java.lang.String getPassword() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "password" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetPassword() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Sets the "password" attribute
     */
    @Override
    public void setPassword(java.lang.String password) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(password);
        }
    }

    /**
     * Sets (as xml) the "password" attribute
     */
    @Override
    public void xsetPassword(org.apache.xmlbeans.XmlString password) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(password);
        }
    }
}
