
package org.kuali.student.core.statement.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by Apache CXF 2.2
 * Wed May 12 12:54:47 PDT 2010
 * Generated source version: 2.2
 */

@Deprecated
@XmlRootElement(name = "getRefObjectSubTypes", namespace = "http://student.kuali.org/wsdl/statement")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getRefObjectSubTypes", namespace = "http://student.kuali.org/wsdl/statement")

public class GetRefObjectSubTypes {

    @XmlElement(name = "objectTypeKey")
    private java.lang.String objectTypeKey;

    public java.lang.String getObjectTypeKey() {
        return this.objectTypeKey;
    }

    public void setObjectTypeKey(java.lang.String newObjectTypeKey)  {
        this.objectTypeKey = newObjectTypeKey;
    }

}

