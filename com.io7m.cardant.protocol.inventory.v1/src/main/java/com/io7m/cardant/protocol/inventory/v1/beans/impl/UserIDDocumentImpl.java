/*
 * An XML document type.
 * Localname: UserID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.UserIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one UserID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class UserIDDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.IDDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.UserIDDocument {
    private static final long serialVersionUID = 1L;

    public UserIDDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "UserID"),
    };


    /**
     * Gets the "UserID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UserIDType getUserID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UserIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UserIDType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "UserID" element
     */
    @Override
    public void setUserID(com.io7m.cardant.protocol.inventory.v1.beans.UserIDType userID) {
        generatedSetterHelperImpl(userID, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "UserID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UserIDType addNewUserID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UserIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UserIDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
