/**
 * Copyright 2010 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.kuali.student.r2.lum.lu.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.student.r1.common.entity.Attribute;

@Entity
@Table(name = "KSLU_DLVMTHD_TYPE_ATTR")
public class DeliveryMethodTypeAttribute extends Attribute<DeliveryMethodType> {
	@ManyToOne
	@JoinColumn(name = "OWNER")
	private DeliveryMethodType owner;

	@Override
	public DeliveryMethodType getOwner() {
		return owner;
	}

	@Override
	public void setOwner(DeliveryMethodType owner) {
		this.owner = owner;
	}
}
