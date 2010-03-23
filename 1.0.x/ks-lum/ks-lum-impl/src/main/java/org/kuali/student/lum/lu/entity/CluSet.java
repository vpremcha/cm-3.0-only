/*
 * Copyright 2009 The Kuali Foundation Licensed under the
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
package org.kuali.student.lum.lu.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.kuali.student.common.util.UUIDHelper;
import org.kuali.student.core.entity.AttributeOwner;
import org.kuali.student.core.entity.MetaEntity;

@Entity
@Table(name = "KSLU_CLU_SET")
@NamedQueries( {
	@NamedQuery(name = "CluSet.getCluSetInfoByIdList", query = "SELECT c FROM CluSet c WHERE c.id IN (:cluSetIdList)"),
	@NamedQuery(name = "CluSet.isCluInCluSet", query = "SELECT COUNT(c) FROM CluSet c JOIN c.clus clu WHERE c.id = :cluSetId AND clu.id = :cluId")
})


public class CluSet extends MetaEntity implements
		AttributeOwner<CluSetAttribute> {
	@Id
	@Column(name = "ID")
	private String id;

	@Column(name = "NAME")
	private String name;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "RT_DESCR_ID")
	private LuRichText descr;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EFF_DT")
	private Date effectiveDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXPIR_DT")
	private Date expirationDate;

	@Column(name = "CRIT_SET")
	private boolean criteriaSet;
	
	// private CluCriteria cluCriteria;//TODO Criteria

	@ManyToMany
	@JoinTable(name = "KSLU_CLU_SET_JN_CLU_SET", joinColumns = @JoinColumn(name = "CLU_SET_PARENT_ID"), inverseJoinColumns = @JoinColumn(name = "CLU_SET_CHILD_ID"))
	private List<CluSet> cluSets;

	@ManyToMany
	@JoinTable(name = "KSLU_CLU_SET_JN_CLU", joinColumns = @JoinColumn(name = "CLU_SET_ID"), inverseJoinColumns = @JoinColumn(name = "CLU_ID"))
	private List<Clu> clus;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
	private List<CluSetAttribute> attributes;

	@Override
    public void onPrePersist() {
		this.id = UUIDHelper.genStringUUID(this.id);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Clu> getClus() {
		if(clus==null){
			clus=new ArrayList<Clu>();
		}
		return clus;
	}

	public void setClus(List<Clu> clus) {
		this.clus = clus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LuRichText getDescr() {
		return descr;
	}

	public void setDescr(LuRichText descr) {
		this.descr = descr;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public List<CluSet> getCluSets() {
		if (cluSets == null) {
			cluSets = new ArrayList<CluSet>();
		}
		return cluSets;
	}

	public void setCluSets(List<CluSet> cluSets) {
		this.cluSets = cluSets;
	}

	public List<CluSetAttribute> getAttributes() {
		if (attributes == null) {
			attributes = new ArrayList<CluSetAttribute>();
		}
		return attributes;
	}

	public void setAttributes(List<CluSetAttribute> attributes) {
		this.attributes = attributes;
	}

	public boolean isCriteriaSet() {
		return criteriaSet;
	}

	public void setCriteriaSet(boolean criteriaSet) {
		this.criteriaSet = criteriaSet;
	}

}
