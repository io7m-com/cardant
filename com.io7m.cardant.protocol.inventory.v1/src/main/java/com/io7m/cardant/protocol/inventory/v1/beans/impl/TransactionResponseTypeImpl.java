/*
 * XML Type:  TransactionResponseType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML TransactionResponseType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class TransactionResponseTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.MessageTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseType {
    private static final long serialVersionUID = 1L;

    public TransactionResponseTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Response"),
        new QName("", "failed"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemsRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsDelete"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseFilePut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "Response"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLoginUsernamePassword"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseFileRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemCreate"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemAttachmentRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemLocationsList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemUpdate"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseError"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemGet"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemAttachmentAdd"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemReposit"),
    }),
    };

    /**
     * Gets a List of "Response" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ResponseType> getResponseList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getResponseArray,
                this::setResponseArray,
                this::insertNewResponse,
                this::removeResponse,
                this::sizeOfResponseArray
            );
        }
    }

    /**
     * Gets array of all "Response" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseType[] getResponseArray() {
        return getXmlObjectArray(PROPERTY_QSET[0], new com.io7m.cardant.protocol.inventory.v1.beans.ResponseType[0]);
    }

    /**
     * Gets ith "Response" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseType getResponseArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseType)get_store().find_element_user(PROPERTY_QSET[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Response" element
     */
    @Override
    public int sizeOfResponseArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QSET[0]);
        }
    }

    /**
     * Sets array of all "Response" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setResponseArray(com.io7m.cardant.protocol.inventory.v1.beans.ResponseType[] responseArray) {
        check_orphaned();
        arraySetterHelper(responseArray, PROPERTY_QNAME[0], PROPERTY_QSET[0]);
    }

    /**
     * Sets ith "Response" element
     */
    @Override
    public void setResponseArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ResponseType response) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseType)get_store().find_element_user(PROPERTY_QSET[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(response);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Response" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseType insertNewResponse(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseType)get_store().insert_element_user(PROPERTY_QSET[0], PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Response" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseType addNewResponse() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Response" element
     */
    @Override
    public void removeResponse(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QSET[0], i);
        }
    }

    /**
     * Gets the "failed" attribute
     */
    @Override
    public boolean getFailed() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return (target == null) ? false : target.getBooleanValue();
        }
    }

    /**
     * Gets (as xml) the "failed" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlBoolean xgetFailed() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Sets the "failed" attribute
     */
    @Override
    public void setFailed(boolean failed) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setBooleanValue(failed);
        }
    }

    /**
     * Sets (as xml) the "failed" attribute
     */
    @Override
    public void xsetFailed(org.apache.xmlbeans.XmlBoolean failed) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(failed);
        }
    }
}
