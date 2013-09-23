/**
 * Copyright 2005-2013 The Kuali Foundation Licensed under the
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
 *
 */
package org.kuali.student.cm.course.service.impl;

import static org.kuali.student.logging.FormattedLogger.error;
import static org.kuali.student.logging.FormattedLogger.info;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.student.cm.course.form.CluInstructorInfoWrapper;
import org.kuali.student.cm.course.form.CollaboratorWrapper;
import org.kuali.student.cm.course.form.CourseJointInfoWrapper;
import org.kuali.student.cm.course.form.GenericStringForCollectionWrapper;
import org.kuali.student.cm.course.form.LearningObjectiveDialogWrapper;
import org.kuali.student.cm.course.form.LoCategoryInfoWrapper;
import org.kuali.student.cm.course.form.OrganizationInfoWrapper;
import org.kuali.student.cm.course.form.ResultValuesGroupInfoWrapper;
import org.kuali.student.cm.course.form.SubjectCodeWrapper;
import org.kuali.student.cm.course.service.CourseInfoMaintainable;
import org.kuali.student.r1.core.personsearch.service.impl.QuickViewByGivenName;
import org.kuali.student.r1.core.subjectcode.service.SubjectCodeService;
import org.kuali.student.r2.common.util.ContextUtils;
import org.kuali.student.r2.common.util.constants.LearningObjectiveServiceConstants;
import org.kuali.student.r2.core.comment.dto.CommentInfo;
import org.kuali.student.r2.core.comment.dto.DecisionInfo;
import org.kuali.student.r2.core.organization.service.OrganizationService;
import org.kuali.student.r2.core.proposal.dto.ProposalInfo;
import org.kuali.student.r2.core.search.dto.SearchParamInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultCellInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.dto.SearchResultRowInfo;
import org.kuali.student.r2.core.search.service.SearchService;
import org.kuali.student.r2.lum.clu.service.CluService;
import org.kuali.student.r2.lum.course.dto.CourseInfo;
import org.kuali.student.r2.lum.lo.service.LearningObjectiveService;
import org.kuali.student.r2.lum.util.constants.CluServiceConstants;

import org.kuali.rice.core.api.util.KeyValue;

/**
 * Base view helper service for both create and edit course info presentations.
 *
 * @author OpenCollab/rSmart KRAD CM Conversion Alliance!
 */
public class CourseInfoMaintainableImpl extends MaintainableImpl implements CourseInfoMaintainable {

	private static final long serialVersionUID = 1338662637708570500L;

	private transient OrganizationService organizationService;

	private transient SearchService searchService;

	private transient SubjectCodeService subjectCodeService;

	private transient CluService cluService;
        
	private transient LearningObjectiveService learningObjectiveService;

	private transient static CourseInfoMaintainable instance;

    private ProposalInfo proposalInfo;
    
    private boolean audit;
    
    private boolean passFail;
    
    private List<CluInstructorInfoWrapper> instructorWrappers;
    
    private List<CourseJointInfoWrapper> courseJointWrappers;
    
    private List<ResultValuesGroupInfoWrapper> creditOptionWrappers;
    
    private String finalExamStatus;
    
    private String finalExamRationale;
    
    private List<CommentInfo> commentInfos;
    
    private LearningObjectiveDialogWrapper loDialogWrapper;
    
    private Boolean showAll;
    
    private String userId;
    
    private List<DecisionInfo> decisions;
    
    private List<OrganizationInfoWrapper> administeringOrganizations;
    
    private String lastUpdated;
    
    private List<CollaboratorWrapper> collaboratorWrappers;

    private String unitsContentOwnerToAdd;
    
    private List<KeyValue> unitsContentOwner;
	
	public static final CourseInfoMaintainable getInstance() {
		if (instance == null) {
			instance = new CourseInfoMaintainableImpl();
		}
		return instance;
	}

    public void setUnitsContentOwnerToAdd(final String KeyValue) {
        this.unitsContentOwnerToAdd = unitsContentOwnerToAdd;
    }

    public String getUnitsContentOwnerToAdd() {
        return unitsContentOwnerToAdd;
    }

    public void setUnitsContentOwner(final List<KeyValue> unitsContentOwner) {
        this.unitsContentOwner = unitsContentOwner;
    }

    public List<KeyValue> getUnitsContentOwner() {
        if (unitsContentOwner == null) {
            unitsContentOwner = new ArrayList<KeyValue>();
        }
        return unitsContentOwner;
    }

    /**
     * Method called when queryMethodToCall is executed for Administering Organizations in order to suggest back to the user an Administering Organization
     *
     * @param organizationName  
     * @return {@link List} of wrapper instances which get added to the {@link CourseForm}
     * @see CourseInfoMaintainable#getOrganizationsForSuggest(String)
     */
	public List<OrganizationInfoWrapper> getOrganizationsForSuggest(final String organizationName) {
		final List<OrganizationInfoWrapper> cluOrgInfoDisplays = new ArrayList<OrganizationInfoWrapper>();
		final List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();
        
        final SearchParamInfo displayNameParam = new SearchParamInfo();
        displayNameParam.setKey("org.queryParam.orgOptionalLongName");
        displayNameParam.getValues().add(organizationName);
        queryParamValueList.add(displayNameParam);

        final SearchParamInfo orgOptionalTypeParam = new SearchParamInfo();
        orgOptionalTypeParam.setKey("org.queryParam.orgOptionalType");
        orgOptionalTypeParam.getValues().add("kuali.org.COC");
        orgOptionalTypeParam.getValues().add("kuali.org.Department");
        orgOptionalTypeParam.getValues().add("kuali.org.College");
        queryParamValueList.add(orgOptionalTypeParam);
        
    	final SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey("org.search.generic");
        searchRequest.setParams(queryParamValueList);
        searchRequest.setStartAt(0);
        searchRequest.setNeededTotalResults(false);
        searchRequest.setSortColumn("org.resultColumn.orgOptionalLongName");
        
        SearchResultInfo searchResult = null;
        try {
        	searchResult = getOrganizationService().search(searchRequest, ContextUtils.getContextInfo());
		} catch (Exception e) {
			e.printStackTrace();
		}

        for (final SearchResultRowInfo result : searchResult.getRows()) {
            final List<SearchResultCellInfo> cells = result.getCells();
            final OrganizationInfoWrapper cluOrgInfoDisplay = new OrganizationInfoWrapper();
            for (final SearchResultCellInfo cell : cells) {
                
                if ("org.resultColumn.orgId".equals(cell.getKey())) {
                    cluOrgInfoDisplay.setId(cell.getValue());
                } 
                else if ("org.resultColumn.orgOptionalLongName".equals(cell.getKey())) {
                    cluOrgInfoDisplay.setOrganizationName(cell.getValue());
                } 
            }
            cluOrgInfoDisplays.add(cluOrgInfoDisplay);
        }
        
		return cluOrgInfoDisplays;
	}

    /**
     *
     * @see CourseInfoMaintainable#getInstructorsForSuggest(String)
     */
	public List<CluInstructorInfoWrapper> getInstructorsForSuggest(
			String instructorName) {
		List<CluInstructorInfoWrapper> cluInstructorInfoDisplays = new ArrayList<CluInstructorInfoWrapper>();
		
		List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();
        
        SearchParamInfo displayNameParam = new SearchParamInfo();
        displayNameParam.setKey(QuickViewByGivenName.NAME_PARAM);
        displayNameParam.getValues().add(instructorName);
        queryParamValueList.add(displayNameParam);
        
    	SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey(QuickViewByGivenName.SEARCH_TYPE);
        searchRequest.setParams(queryParamValueList);
        searchRequest.setStartAt(0);
        searchRequest.setNeededTotalResults(false);
        searchRequest.setSortColumn(QuickViewByGivenName.DISPLAY_NAME_RESULT);
        
        SearchResultInfo searchResult = null;
        try {
        	searchResult = getSearchService().search(searchRequest, ContextUtils.getContextInfo());
        	for (SearchResultRowInfo result : searchResult.getRows()) {
                List<SearchResultCellInfo> cells = result.getCells();
                CluInstructorInfoWrapper cluInstructorInfoDisplay = new CluInstructorInfoWrapper();
                for (SearchResultCellInfo cell : cells) {
                    if (QuickViewByGivenName.GIVEN_NAME_RESULT.equals(cell.getKey())) {
                    	cluInstructorInfoDisplay.setGivenName(cell.getValue());
                    } else if (QuickViewByGivenName.PERSON_ID_RESULT.equals(cell.getKey())) {
                    	cluInstructorInfoDisplay.setPersonId(cell.getValue());
                    } else if (QuickViewByGivenName.ENTITY_ID_RESULT.equals(cell.getKey())) {
                    	cluInstructorInfoDisplay.setId(cell.getValue());
                    } else if (QuickViewByGivenName.PRINCIPAL_NAME_RESULT.equals(cell.getKey())) {
                    	cluInstructorInfoDisplay.setPrincipalName(cell.getValue());
                    } else if (QuickViewByGivenName.DISPLAY_NAME_RESULT.equals(cell.getKey())) {
                    	cluInstructorInfoDisplay.setDisplayName(cell.getValue());
                    }
                }
                cluInstructorInfoDisplays.add(cluInstructorInfoDisplay);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return cluInstructorInfoDisplays;
	}
	
    /**
     *
     * @see CourseInfoMaintainable#getInstructor(String)
     */
	public CluInstructorInfoWrapper getInstructor(String instructorName) {
	    CluInstructorInfoWrapper instructor = null;

		List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();

		SearchParamInfo displayNameParam = new SearchParamInfo();
		displayNameParam.setKey(QuickViewByGivenName.NAME_PARAM);
		displayNameParam.getValues().add(instructorName);
		queryParamValueList.add(displayNameParam);

		SearchRequestInfo searchRequest = new SearchRequestInfo();
		searchRequest.setSearchKey(QuickViewByGivenName.SEARCH_TYPE);
		searchRequest.setParams(queryParamValueList);
		searchRequest.setStartAt(0);
		searchRequest.setNeededTotalResults(false);
		searchRequest.setSortColumn(QuickViewByGivenName.DISPLAY_NAME_RESULT);

		SearchResultInfo searchResult = null;
		try {
			searchResult = getSearchService().search(searchRequest,
					ContextUtils.getContextInfo());
			if (searchResult.getRows().size() == 1) {
				SearchResultRowInfo result = searchResult.getRows().get(0);
				List<SearchResultCellInfo> cells = result.getCells();
				instructor = new CluInstructorInfoWrapper();
				for (SearchResultCellInfo cell : cells) {
				    if (QuickViewByGivenName.GIVEN_NAME_RESULT.equals(cell.getKey())) {
				        instructor.setGivenName(cell.getValue());
                    } else if (QuickViewByGivenName.PERSON_ID_RESULT.equals(cell.getKey())) {
                        instructor.setPersonId(cell.getValue());
                    } else if (QuickViewByGivenName.ENTITY_ID_RESULT.equals(cell.getKey())) {
                        instructor.setId(cell.getValue());
                    } else if (QuickViewByGivenName.PRINCIPAL_NAME_RESULT.equals(cell.getKey())) {
                        instructor.setPrincipalName(cell.getValue());
                    } else if (QuickViewByGivenName.DISPLAY_NAME_RESULT.equals(cell.getKey())) {
                        instructor.setDisplayName(cell.getValue());
                    }
				}
			} else {
				error("The method getInstructor returned more than 1 search result.");
			}
		} catch (Exception e) {
			error(
                "An error occurred in the getInstructor method. %s", e.getMessage());
		}

		return instructor;
	}

    /**
     *
     * @see CourseInfoMaintainable#getSubjectCodesForSuggest(String)
     */
    public List<SubjectCodeWrapper> getSubjectCodesForSuggest(String subjectCode) {
        List<SubjectCodeWrapper> retrievedCodes = new ArrayList<SubjectCodeWrapper>();

        List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();

        SearchParamInfo codeParam = new SearchParamInfo();
        codeParam.setKey(LookupableConstants.SUBJECTCODE_CODE_PARAM);
        List<String> codeValues = new ArrayList<String>();
        codeValues.add(subjectCode);
        codeParam.setValues(codeValues);

        queryParamValueList.add(codeParam);

        SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey(LookupableConstants.SUBJECTCODE_GENERIC_SEARCH);
        searchRequest.setParams(queryParamValueList);

        SearchResultInfo searchResult = null;
        try {
            searchResult = getSubjectCodeService().search(searchRequest, ContextUtils.getContextInfo());
            for (SearchResultRowInfo result : searchResult.getRows()) {
                List<SearchResultCellInfo> cells = result.getCells();
                String id = "";
                String code = "";
                for (SearchResultCellInfo cell : cells) {
                    if (LookupableConstants.SUBJECTCODE_ID_RESULT.equals(cell.getKey())) {
                        id = cell.getValue();
                    } else if (LookupableConstants.SUBJECTCODE_CODE_RESULT.equals(cell.getKey())) {
                        code = cell.getValue();
                    }
                }
                retrievedCodes.add(new SubjectCodeWrapper(id, code));
            }
        } catch (Exception e) {
            error("An error occurred retrieving the SubjectCodeDisplay: %s", e);
        }

        return retrievedCodes;
    }
	
	public List<CourseJointInfoWrapper> getJointOfferingCourseNumbersForSuggest(String courseNumber) {
		List<CourseJointInfoWrapper> courseJoints = new ArrayList<CourseJointInfoWrapper>();
		
		List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();
		
		SearchParamInfo codeParam = new SearchParamInfo();
        codeParam.setKey(LookupableConstants.OPTIONAL_CODE_PARAM);
        List<String> codeValues = new ArrayList<String>();
        codeValues.add(courseNumber);
        codeParam.setValues(codeValues);
        
        SearchParamInfo typeParam = new SearchParamInfo();
        typeParam.setKey(LookupableConstants.OPTIONAL_TYPE_PARAM);
        List<String> typeValues = new ArrayList<String>();
        typeValues.add(LookupableConstants.CREDITCOURSE_lU);
        typeParam.setValues(typeValues);
        
        queryParamValueList.add(codeParam);
        queryParamValueList.add(typeParam);
        
        SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey(LookupableConstants.CURRENT_QUICK_SEARCH);
        searchRequest.setParams(queryParamValueList);
        searchRequest.setStartAt(0);
        searchRequest.setSortColumn(LookupableConstants.OPTIONALCODE_RESULT);
        
        SearchResultInfo searchResult = null;
        try {
        	searchResult = getCluService().search(searchRequest, ContextUtils.getContextInfo());
        	
            for (SearchResultRowInfo result : searchResult.getRows()) {
                List<SearchResultCellInfo> cells = result.getCells();
                String id = "";
                String code = "";
                for (SearchResultCellInfo cell : cells) {
                	if (LookupableConstants.ID_RESULT.equals(cell.getKey())) {
                		id = cell.getValue();
                	} else if (LookupableConstants.OPTIONALCODE_RESULT.equals(cell.getKey())) {
                		code = cell.getValue();
                	}
                }
                CourseJointInfoWrapper courseJointDisplay = new CourseJointInfoWrapper();
                courseJointDisplay.setCourseId(id);
                courseJointDisplay.setCourseCode(code);
                String subjectArea = code.replaceAll("\\d", "");
                String numberSuffix = code.replaceAll("\\D", "");
                courseJointDisplay.setSubjectArea(subjectArea);
                courseJointDisplay.setCourseNumberSuffix(numberSuffix);
                courseJoints.add(courseJointDisplay);
            }
        } catch (Exception e) {
            error("An error occurred retrieving the courseJointDisplay: ", e);
        }
		
		return courseJoints;
	}
	
	/**
	 * Returns the CourseJointInfoDisplay object for the specified course code.
	 * @param courseCode The entire course code should be passed.
	 * @return Only 1 CourseJointInfoDisplay result is expected and will to be returned.
	 */
	public CourseJointInfoWrapper getJointOfferingCourse(String courseCode) {
	    CourseJointInfoWrapper courseJointInfo = null;
		
		List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();
		
		SearchParamInfo codeParam = new SearchParamInfo();
        codeParam.setKey(LookupableConstants.OPTIONAL_CODE_PARAM);
        List<String> codeValues = new ArrayList<String>();
        codeValues.add(courseCode);
        codeParam.setValues(codeValues);
        
        SearchParamInfo typeParam = new SearchParamInfo();
        typeParam.setKey(LookupableConstants.OPTIONAL_TYPE_PARAM);
        List<String> typeValues = new ArrayList<String>();
        typeValues.add(LookupableConstants.CREDITCOURSE_lU);
        typeParam.setValues(typeValues);
        
        queryParamValueList.add(codeParam);
        queryParamValueList.add(typeParam);
        
        SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey(LookupableConstants.CURRENT_QUICK_SEARCH);
        searchRequest.setParams(queryParamValueList);
        
        SearchResultInfo searchResult = null;
        try {
        	searchResult = getCluService().search(searchRequest, ContextUtils.getContextInfo());
        	//Only 1 item should be retrieved in this search
        	if (searchResult.getRows().size() == 1) {
        		SearchResultRowInfo result = searchResult.getRows().get(0);
        		List<SearchResultCellInfo> cells = result.getCells();
                String id = "";
                String code = "";
                for (SearchResultCellInfo cell : cells) {
                	if (LookupableConstants.ID_RESULT.equals(cell.getKey())) {
                		id = cell.getValue();
                	} else if (LookupableConstants.OPTIONALCODE_RESULT.equals(cell.getKey())) {
                		code = cell.getValue();
                	}
                }
                courseJointInfo = new CourseJointInfoWrapper();
                courseJointInfo.setCourseId(id);
                courseJointInfo.setCourseCode(code);
                String subjectArea = code.replaceAll("\\d", "");
                String numberSuffix = code.replaceAll("\\D", "");
                courseJointInfo.setSubjectArea(subjectArea);
                courseJointInfo.setCourseNumberSuffix(numberSuffix);
                
        	} else {
        		error("The getJointOfferingCourse method has returned more than 1 result.");
        	}
        	
        } catch (Exception e) {
        	error("An error occurred in getJointOfferingCourse.", e);
        }
		
		return courseJointInfo;
	}
	
	public List<LoCategoryInfoWrapper> getLoCategoriesForSuggest(String categoryName) {
        List<LoCategoryInfoWrapper> retrievedCategories = new ArrayList<LoCategoryInfoWrapper>();

        List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();

        SearchParamInfo categoryNameParam = new SearchParamInfo();
        categoryNameParam.setKey(LookupableConstants.OPTIONAL_LO_CATEGORY_NAME_PARAM);
        List<String> categoryNameValues = new ArrayList<String>();
        categoryNameValues.add(categoryName);
        categoryNameParam.setValues(categoryNameValues);

        queryParamValueList.add(categoryNameParam);

        SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey(LookupableConstants.LOCATEGORY_SEARCH);
        searchRequest.setParams(queryParamValueList);
        searchRequest.setSortColumn(LookupableConstants.LO_CATEGORY_NAME_AND_TYPE_RESULT);

        try {
            SearchResultInfo searchResult = getLearningObjectiveService().search(searchRequest,
                    ContextUtils.getContextInfo());
            for (SearchResultRowInfo result : searchResult.getRows()) {
                List<SearchResultCellInfo> cells = result.getCells();
                LoCategoryInfoWrapper newCat = new LoCategoryInfoWrapper();
                for (SearchResultCellInfo cell : cells) {
                    if (LookupableConstants.LO_CATEGORY_ID_RESULT.equals(cell.getKey())) {
                        newCat.setId(cell.getValue());
                    } else if (LookupableConstants.LO_CATEGORY_NAME_RESULT.equals(cell.getKey())) {
                        newCat.setName(cell.getValue());
                    } else if(LookupableConstants.LO_CATEGORY_TYPE_RESULT.equals(cell.getKey())){
                        newCat.setTypeKey(cell.getValue());
                    } else if(LookupableConstants.LO_CATEGORY_TYPE_NAME_RESULT.equals(cell.getKey())){
                        newCat.setTypeName(cell.getValue());
                    } else if (LookupableConstants.LO_CATEGORY_NAME_AND_TYPE_RESULT.equals(cell.getKey())) {
                        newCat.setCatNameAndType(cell.getValue());
                    } else if(LookupableConstants.LO_CATEGORY_STATE_RESULT.equals(cell.getKey())){
                        newCat.setStateKey(cell.getValue());
                    }
                }
                retrievedCategories.add(newCat);
            }
        } catch (Exception e) {
            error("An error occurred in getLoCategoriesForSuggest.", e);
        }

        return retrievedCategories;
    }
	
	private SearchService getSearchService() {
		if (searchService == null) {
			searchService = GlobalResourceLoader.getService(new QName(LookupableConstants.NAMESPACE_PERSONSEACH, LookupableConstants.PERSONSEACH_SERVICE_NAME_LOCAL_PART));
		}
		return searchService;
	}
	
	private SubjectCodeService getSubjectCodeService() {
		if (subjectCodeService == null) {
			subjectCodeService = GlobalResourceLoader.getService(new QName(LookupableConstants.NAMESPACE_SUBJECTCODE, SubjectCodeService.class.getSimpleName()));
		}
		return subjectCodeService;
	}	
	
	private CluService getCluService() {
		if (cluService == null) {
			cluService = GlobalResourceLoader.getService(new QName(CluServiceConstants.CLU_NAMESPACE, CluService.class.getSimpleName()));
		}
		return cluService;
	}	
	
	private LearningObjectiveService getLearningObjectiveService() {
        if (learningObjectiveService == null) {
            learningObjectiveService = GlobalResourceLoader.getService(new QName(
                    LearningObjectiveServiceConstants.NAMESPACE, LearningObjectiveService.class.getSimpleName()));
        }
        return learningObjectiveService;
    }
	
	protected OrganizationService getOrganizationService() {
		if (organizationService == null) {
	        organizationService = (OrganizationService) GlobalResourceLoader
                .getService(new QName("http://student.kuali.org/wsdl/organization","OrganizationService"));
		}
		return organizationService;
	}


    public ProposalInfo getProposal() {
        return proposalInfo;
        
    }

    public void setProposal(final ProposalInfo proposal) {
        this.proposalInfo = proposal;
    }

    public CourseInfo getCourse() {
        return (CourseInfo) getDataObject();
    }

    public void setCourse(final CourseInfo course) {
        setDataObject(course);
    }

    /**
     * Gets the value of audit
     * 
     * @return the value of audit
     */
    public boolean isAudit() {
        return this.audit;
    }

    /**
     * Sets the value of audit
     * 
     * @param argAudit Value to assign to this.audit
     */
    public void setAudit(final boolean argAudit) {
        this.audit = argAudit;
    }

    /**
     * Gets the value of passFail
     * 
     * @return the value of passFail
     */
    public boolean isPassFail() {
        return this.passFail;
    }

    /**
     * Sets the value of passFail
     * 
     * @param argPassFail Value to assign to this.passFail
     */
    public void setPassFail(final boolean argPassFail) {
        this.passFail = argPassFail;
    }

    /**
     * Gets the value of finalExamStatus
     * 
     * @return the value of finalExamStatus
     */
    public String getFinalExamStatus() {
        return this.finalExamStatus;
    }

    /**
     * Sets the value of finalExamStatus
     * 
     * @param argFinalExamStatus Value to assign to this.finalExamStatus
     */
    public void setFinalExamStatus(final String argFinalExamStatus) {
        this.finalExamStatus = argFinalExamStatus;
    }

    /**
     * Gets the value of finalExamRationale
     * 
     * @return the value of finalExamRationale
     */
    public String getFinalExamRationale() {
        return this.finalExamRationale;
    }

    /**
     * Sets the value of finalExamRationale
     * 
     * @param argFinalExamRationale Value to assign to this.finalExamRationale
     */
    public void setFinalExamRationale(final String argFinalExamRationale) {
        this.finalExamRationale = argFinalExamRationale;
    }

    /**
     * Gets the value of loDialogWrapper
     * 
     * @return the value of loDialogWrapper
     */
    public LearningObjectiveDialogWrapper getLoDialogWrapper() {
        return this.loDialogWrapper;
    }

    /**
     * Sets the value of loDialogWrapper
     * 
     * @param argLoDialogWrapper Value to assign to this.loDialogWrapper
     */
    public void setLoDialogWrapper(final LearningObjectiveDialogWrapper argLoDialogWrapper) {
        this.loDialogWrapper = argLoDialogWrapper;
    }

    /**
     * Gets the value of showAll
     * 
     * @return the value of showAll
     */
    public Boolean getShowAll() {
        return this.showAll;
    }

    /**
     * Sets the value of showAll
     * 
     * @param argShowAll Value to assign to this.showAll
     */
    public void setShowAll(final Boolean argShowAll) {
        this.showAll = argShowAll;
    }

    /**
     * Gets the value of userId
     * 
     * @return the value of userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the value of userId
     * 
     * @param argUserId Value to assign to this.userId
     */
    public void setUserId(final String argUserId) {
        this.userId = argUserId;
    }

    /**
     * Gets the value of lastUpdated
     * 
     * @return the value of lastUpdated
     */
    public String getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Sets the value of lastUpdated
     * 
     * @param argLastUpdated Value to assign to this.lastUpdated
     */
    public void setLastUpdated(final String argLastUpdated) {
        this.lastUpdated = argLastUpdated;
    }

    /**
     * Gets the list of Instructor wrappers
     * 
     * @return the list of {@link CluInstructorInfoWrapper}
     */
    public List<CluInstructorInfoWrapper> getInstructorWrappers() {
        if (instructorWrappers == null) {
            instructorWrappers = new ArrayList<CluInstructorInfoWrapper>(0);
        }
        return instructorWrappers;
    }

    /**
     * Sets the list of Instructor wrappers
     * 
     * @param instructorWrappers List of {@link CluInstructorInfoWrapper}
     */
    public void setInstructorWrappers(List<CluInstructorInfoWrapper> instructorWrappers) {
        this.instructorWrappers = instructorWrappers;
    }

    /**
     * Gets the list of Course Joint wrappers
     * 
     * @return the list of {@link CourseJointInfoWrapper}
     */
    public List<CourseJointInfoWrapper> getCourseJointWrappers() {
        if (courseJointWrappers == null) {
            courseJointWrappers = new ArrayList<CourseJointInfoWrapper>(0);
        }
        return courseJointWrappers;
    }

    /**
     * Sets the list of Course Joint wrappers
     * 
     * @param courseJointWrappers List of {@link CourseJointInfoWrapper}
     */
    public void setCourseJointWrappers(List<CourseJointInfoWrapper> courseJointWrappers) {
        this.courseJointWrappers = courseJointWrappers;
    }

    /**
     * Gets the list of Credit Option wrappers
     * 
     * @return the list of {@link ResultValuesGroupInfoWrapper}
     */
    public List<ResultValuesGroupInfoWrapper> getCreditOptionWrappers() {
        if (creditOptionWrappers == null) {
            creditOptionWrappers = new ArrayList<ResultValuesGroupInfoWrapper>(0);
        }
        return creditOptionWrappers;
    }

    /**
     * Sets the list of Credit Option wrappers
     * 
     * @param creditOptionWrappers List of {@link ResultValuesGroupInfoWrapper}
     */
    public void setCreditOptionWrappers(List<ResultValuesGroupInfoWrapper> creditOptionWrappers) {
        this.creditOptionWrappers = creditOptionWrappers;
    }

    /**
     * Gets the list of Comments
     * 
     * @return the list of {@link CommentInfo}
     */
    public List<CommentInfo> getCommentInfos() {
        if (commentInfos == null) {
            commentInfos = new ArrayList<CommentInfo>(0);
        }
        return commentInfos;
    }

    /**
     * Sets the list of Comments
     * 
     * @param commentInfos List of {@link CommentInfo}
     */
    public void setCommentInfos(List<CommentInfo> commentInfos) {
        this.commentInfos = commentInfos;
    }

    /**
     * Gets the list of Decisions
     * 
     * @return the list of {@link DecisionInfo}
     */
    public List<DecisionInfo> getDecisions() {
        if (decisions == null) {
            decisions = new ArrayList<DecisionInfo>(0);
        }
        return decisions;
    }

    /**
     * Sets the list of Decisions
     * 
     * @param decisions List of {@link DecisionInfo}
     */
    public void setDecisions(List<DecisionInfo> decisions) {
        this.decisions = decisions;
    }

    /**
     * Gets the list of Administering Organizations
     * 
     * @return the list of {@link OrganizationInfoWrapper}
     */
    public List<OrganizationInfoWrapper> getAdministeringOrganizations() {
        if (administeringOrganizations == null) {
            administeringOrganizations = new ArrayList<OrganizationInfoWrapper>(0);
        }
        return administeringOrganizations;
    }

    /**
     * Sets the list of Administering Organizations
     * 
     * @param administeringOrganizations List of {@link OrganizationInfoWrapper}
     */
    public void setAdministeringOrganizations(List<OrganizationInfoWrapper> administeringOrganizations) {
        this.administeringOrganizations = administeringOrganizations;
    }

    /**
     * 
     * This overridden method ...
     * 
     * @see org.kuali.student.cm.course.service.CourseInfoMaintainable#getCollaboratorWrappers()
     */
    @Override
    public List<CollaboratorWrapper> getCollaboratorWrappers() {
        if (collaboratorWrappers == null) {
            collaboratorWrappers = new ArrayList<CollaboratorWrapper>(0);
        }
        return collaboratorWrappers;
    }

    /**
     * 
     * This overridden method ...
     * 
     * @see org.kuali.student.cm.course.service.CourseInfoMaintainable#setCollaboratorWrappers(java.util.List)
     */
    @Override
    public void setCollaboratorWrappers(List<CollaboratorWrapper> collaboratorWrappers) {
        this.collaboratorWrappers = collaboratorWrappers;
        
    }
    
    /**
    *
    * @see CourseInfoMaintainable#getCollaboratorWrappersSuggest(String)
    */
   public List<CollaboratorWrapper> getCollaboratorWrappersSuggest(
           String principalId) {
       List<CollaboratorWrapper> listCollaboratorWrappers = new ArrayList<CollaboratorWrapper>();
       
       List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();
       
       SearchParamInfo displayNameParam = new SearchParamInfo();
       displayNameParam.setKey(QuickViewByGivenName.NAME_PARAM);
       displayNameParam.getValues().add(principalId);
       queryParamValueList.add(displayNameParam);
       
       SearchRequestInfo searchRequest = new SearchRequestInfo();
       searchRequest.setSearchKey(QuickViewByGivenName.SEARCH_TYPE);
       searchRequest.setParams(queryParamValueList);
       searchRequest.setStartAt(0);
       searchRequest.setNeededTotalResults(false);
       searchRequest.setSortColumn(QuickViewByGivenName.DISPLAY_NAME_RESULT);
       
       SearchResultInfo searchResult = null;
       try {
           searchResult = getSearchService().search(searchRequest, ContextUtils.getContextInfo());
           for (SearchResultRowInfo result : searchResult.getRows()) {
               List<SearchResultCellInfo> cells = result.getCells();
               CollaboratorWrapper theCollaboratorWrapper = new CollaboratorWrapper();
               for (SearchResultCellInfo cell : cells) {
                   if (QuickViewByGivenName.GIVEN_NAME_RESULT.equals(cell.getKey())) {
                       theCollaboratorWrapper.setGivenName(cell.getValue());
                   } else if (QuickViewByGivenName.PERSON_ID_RESULT.equals(cell.getKey())) {
                       theCollaboratorWrapper.setPersonID(cell.getValue());
                   } else if (QuickViewByGivenName.ENTITY_ID_RESULT.equals(cell.getKey())) {
                       theCollaboratorWrapper.setPrincipalId(cell.getValue());
                   } else if (QuickViewByGivenName.PRINCIPAL_NAME_RESULT.equals(cell.getKey())) {
                       theCollaboratorWrapper.setPrincipalName(cell.getValue());
                   } else if (QuickViewByGivenName.DISPLAY_NAME_RESULT.equals(cell.getKey())) {
                       theCollaboratorWrapper.setDisplayName(cell.getValue());
                   }
               }
               listCollaboratorWrappers.add(theCollaboratorWrapper);
           }
       } catch (Exception e) {
           error("Error retrieving Personel search List %s", e);
           //throw new RuntimeException();
       }
       
       return listCollaboratorWrappers;
   }

}
