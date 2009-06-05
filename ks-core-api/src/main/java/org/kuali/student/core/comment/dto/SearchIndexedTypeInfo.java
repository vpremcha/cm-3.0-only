/*
 * Copyright 2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.core.comment.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Identifier and alias for a type, used in a search.
 *
 * @Author KSContractMojo
 * @Author Neerav Agrawal
 * @Since Fri Jun 05 14:27:46 EDT 2009
 * @See <a href="https://test.kuali.org/confluence/display/KULSTR/searchIndexedTypeInfo+Structure+v1.0-rc2">SearchIndexedTypeInfo Structure</>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchIndexedTypeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String name;

    @XmlElement
    private String alias;

    /**
     * Identifier for a type.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Alias for the indexed type in the context of the search.
     */
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}