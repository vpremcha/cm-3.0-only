
package org.kuali.student.rules.factfinder.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by Apache CXF 2.1.2
 * Tue Jan 20 15:52:41 EST 2009
 * Generated source version: 2.1.2
 */

@XmlRootElement(name = "findFactTypesResponse", namespace = "http://student.kuali.org/wsdl/brms/FactFinder")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findFactTypesResponse", namespace = "http://student.kuali.org/wsdl/brms/FactFinder")

public class FindFactTypesResponse {

    @XmlElement(name = "return")
    private java.util.List _return;

    public java.util.List getReturn() {
        return this._return;
    }

    public void setReturn(java.util.List new_return)  {
        this._return = new_return;
    }

}

