/*
 * Copyright 2010 The Kuali Foundation Licensed under the Educational
 * Community License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.osedu.org/licenses/ECL-2.0 Unless
 * required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.kuali.student.r2.common.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.kuali.student.r2.common.infc.Attribute;
import org.kuali.student.r2.common.infc.KeyEntity;
import org.kuali.student.r2.common.infc.Meta;
import org.kuali.student.r2.common.infc.RichText;

@SuppressWarnings("serial")
@XmlTransient
public abstract class KeyEntityInfo extends EntityInfo implements KeyEntity, Serializable {

    @XmlAttribute
    private String key;

    protected KeyEntityInfo() {
        key = null;
    }

    protected KeyEntityInfo(KeyEntity kEntity) {
        super(kEntity);
        if (null != kEntity) {
	        this.key = kEntity.getKey();
        }
    }

    protected KeyEntityInfo(String key, String name, RichText descr, String typeKey, String stateKey, List<? extends Attribute> attributes, Meta meta) {
        super(name, descr, typeKey, stateKey, attributes, meta);
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
    
    @Override
    public void setKey(String key) {
        this.key = key;
    }
}
