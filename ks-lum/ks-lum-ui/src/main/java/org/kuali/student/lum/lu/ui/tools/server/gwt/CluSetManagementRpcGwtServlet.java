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

package org.kuali.student.lum.lu.ui.tools.server.gwt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.student.r1.common.assembly.data.AssemblyException;
import org.kuali.student.r1.common.assembly.data.Data;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r1.common.search.dto.SearchRequest;
import org.kuali.student.r1.common.search.dto.SearchResult;
import org.kuali.student.r1.common.search.dto.SearchResultCell;
import org.kuali.student.r1.common.search.dto.SearchResultRow;
import org.kuali.student.r2.common.util.ContextUtils;
import org.kuali.student.r2.core.versionmanagement.dto.VersionDisplayInfo;

import org.kuali.student.common.ui.client.service.DataSaveResult;
import org.kuali.student.common.ui.client.service.exceptions.OperationFailedException;
import org.kuali.student.common.ui.server.gwt.DataGwtServlet;
import org.kuali.student.lum.common.client.widgets.CluInformation;
import org.kuali.student.lum.common.client.widgets.CluSetInformation;
import org.kuali.student.lum.common.client.widgets.CluSetManagementRpcService;
import org.kuali.student.r1.lum.lrc.dto.ResultComponentInfo;
import org.kuali.student.r2.lum.lrc.service.LRCService;
import org.kuali.student.r2.lum.clu.dto.*;
import org.kuali.student.r2.lum.clu.service.CluService;
import org.kuali.student.r2.lum.util.constants.CluServiceConstants;

import org.apache.log4j.Logger;

// TODO KSCM import edu.emory.mathcs.backport.java.util.Collections;

public class CluSetManagementRpcGwtServlet extends DataGwtServlet implements
		CluSetManagementRpcService {

	private static final long serialVersionUID = 1L;
	final static Logger LOG = Logger.getLogger(CluSetManagementRpcGwtServlet.class);
	private CluService cluService;
	private LRCService lrcService;
    
	public CluService getLuService() {
        return cluService;
    }

    public void setLuService(CluService cluService) {
        this.cluService = cluService;
    }

    public LRCService getLRCService() {
        return lrcService;
    }

    public void setLRCService(LRCService lrcService) {
        this.lrcService = lrcService;
    }
    
    @Override
    public Data getData(String id) throws OperationFailedException {
        try{
            //TODO KSCM-390 - Correct ContextInfo parameter?
            return getDataService().getData(id, ContextUtils.getContextInfo());
        } catch (Exception e) {
            LOG.error("Could not get Data ", e);
            throw new OperationFailedException("Failed to get data");
        }
    }

    @Override
    public DataSaveResult saveData(Data data) throws OperationFailedException {
        try{
            //TODO KSCM-390 - Correct ContextInfo parameter?
            return getDataService().saveData(data, ContextUtils.getContextInfo());
        } catch (Exception e) {
            LOG.error("Could not save data ", e);
            throw new OperationFailedException("Failed to save data");
        } 
    }

    private CluSetInfo getCluSetInfo(String cluSetId, ContextInfo contextInfo) throws OperationFailedException {
        List<String> cluIds = null;
        CluSetInfo cluSetInfo = null;
        try {
            // note: the cluIds returned by cluService.getCluSetInfo also contains the clus
            //       that are the result of query parameter search.  Set to null here and
            //       retrieve the clus that are direct members.
            //TODO KSCM-390 - Correct ContextInfo parameter?
            cluSetInfo = cluService.getCluSet(cluSetId, ContextUtils.getContextInfo());
            cluSetInfo.setCluIds(null);
            //TODO KSCM-390 - Correct ContextInfo parameter?
            cluIds = cluService.getCluIdsFromCluSet(cluSetId, contextInfo);
            cluSetInfo.setCluIds(cluIds);
            //TODO KSCM-390 - Correct ContextInfo parameter?
            upWrap(cluSetInfo, contextInfo);
        } catch (Exception e) {
            throw new OperationFailedException("Failed to retrieve cluset info for " + cluSetId, e);
        }
        return cluSetInfo;
    }
    
    private List<CluSetInfo> getCluSetInfos(List<String> cluSetIds, ContextInfo contextInfo) throws OperationFailedException {
        List<CluSetInfo> clusetInfos = null;
        if (cluSetIds != null) {
            for (String cluSetId : cluSetIds) {
                clusetInfos = (clusetInfos == null)? new ArrayList<CluSetInfo>() : clusetInfos;
                //TODO KSCM-390 - Correct ContextInfo parameter?
                clusetInfos.add(getCluSetInfo(cluSetId, contextInfo));
            }
        }
        return clusetInfos;
    }

    private void upWrap(CluSetInfo cluSetInfo, ContextInfo contextInfo) throws AssemblyException {
        List<String> cluSetIds = (cluSetInfo == null)? null : cluSetInfo.getCluSetIds();
        List<String> unWrappedCluSetIds = null;
        List<CluSetInfo> wrappedCluSets = null;
        List<CluSetInfo> subCluSets = null;

        try {
            if (cluSetIds != null && !cluSetIds.isEmpty()) {
                //TODO KSCM-390 - Correct ContextInfo parameter?
                subCluSets = cluService.getCluSetsByIds(cluSetIds, contextInfo);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new AssemblyException("Failed to retrieve the sub clusets of cluset " +
                    cluSetInfo.getId());
        }
        // goes through the list of sub clusets and ignore the ones that are not reusable
        if (subCluSets != null) {
            for (CluSetInfo subCluSet : subCluSets) {
                if (subCluSet.getIsReusable()) {
                    unWrappedCluSetIds = (unWrappedCluSetIds == null)?
                            new ArrayList<String>() : unWrappedCluSetIds;
                            unWrappedCluSetIds.add(subCluSet.getId());
                } else {
                    wrappedCluSets = (wrappedCluSets == null)?
                            new ArrayList<CluSetInfo>() : wrappedCluSets;
                            wrappedCluSets.add(subCluSet);
                }
            }
        }
        cluSetInfo.setCluSetIds(unWrappedCluSetIds);
        if (wrappedCluSets != null) {
            for (CluSetInfo wrappedCluSet : wrappedCluSets) {
                MembershipQueryInfo mqInfo = wrappedCluSet.getMembershipQuery();
                if (wrappedCluSet.getCluIds() != null && !wrappedCluSet.getCluIds().isEmpty()) {
                    cluSetInfo.setCluIds(wrappedCluSet.getCluIds());
                }
                if (mqInfo != null && mqInfo.getSearchTypeKey() != null && !mqInfo.getSearchTypeKey().isEmpty()) {
                    cluSetInfo.setMembershipQuery(mqInfo);
                }
            }
        }
    }
    
    private List<CluInformation> getCluInformations(List<String> cluIds, ContextInfo contextInfo) throws OperationFailedException {
        List<CluInformation> result = new ArrayList<CluInformation>();
        if (cluIds != null) {
            for (String cluId : cluIds) {
                try {
                    //TODO KSCM-390 - Correct ContextInfo parameter?
                	VersionDisplayInfo versionInfo = cluService.getCurrentVersion(CluServiceConstants.CLU_NAMESPACE_URI, cluId, contextInfo);
                	//TODO KSCM-390 - Correct ContextInfo parameter?
                	CluInfo cluInfo = cluService.getClu(versionInfo.getId(), contextInfo);
                    if (cluInfo != null) {

                        //retrieve credits
                        String credits = "";
                        //TODO KSCM-390 - Correct ContextInfo parameter?
                        List<CluResultInfo> cluResultInfos = cluService.getCluResultByClu(versionInfo.getId(), contextInfo);
                        if (cluResultInfos != null) {
                            for (CluResultInfo cluResultInfo : cluResultInfos) {
                                String cluType = cluResultInfo.getType();

                                //ignore non-credit results
                                if ((cluType == null) || (!cluType.equals("kuali.resultType.creditCourseResult"))) {
                                    continue;
                                }

                                //retrieve credit type and credit values
                                ResultComponentInfo resultComponentInfo = null;
                                List<String> resultValues = null;
                                String creditType = "";
                                if (cluResultInfo.getResultOptions() != null) {
                                    for (ResultOptionInfo resultOption : cluResultInfo.getResultOptions()) {
                                        if (resultOption.getResultComponentId() != null) {
                                            //TODO KSCM-390 - Correct ContextInfo parameter?
                                            resultComponentInfo = lrcService.getResultComponent(resultOption.getResultComponentId());
                                            resultValues = resultComponentInfo.getResultValues();
                                            creditType = resultComponentInfo.getType();
                                            break;
                                        }
                                    }
                                }
                                if (resultValues == null) {
                                    continue;
                                }

                                if (!credits.isEmpty()) {
                                    credits = credits + "; ";
                                }

                                if (creditType.equals("kuali.resultComponentType.credit.degree.fixed")) {
                                    credits = credits + resultValues.get(0);
                                } else if (creditType.equals("kuali.resultComponentType.credit.degree.multiple")) {
                                    boolean firstValue = true;
                                    for (String resultValue : resultValues) {
                                        credits = credits + (firstValue ? "" :", ")  + resultValue;
                                        firstValue = false;
                                    }
                                } else if (creditType.equals("kuali.resultComponentType.credit.degree.range")) {
                                    credits = credits + resultComponentInfo.getAttributes().get("minCreditValue") + " - " + resultComponentInfo.getAttributes().get("maxCreditValue");
                                }
                            }
                        }
                        
                        CluInformation cluInformation = new CluInformation();
                        if (cluInfo.getOfficialIdentifier() != null) {
                            cluInformation.setCode(cluInfo.getOfficialIdentifier().getCode());
                            cluInformation.setTitle(cluInfo.getOfficialIdentifier().getShortName());
                            cluInformation.setCredits(credits);
                        }
                        
                        cluInformation.setType(cluInfo.getType());
                        //If the clu type is variation, get the parent clu id. 
                        if ("kuali.lu.type.Variation".equals(cluInfo.getType())){
                            //TODO KSCM-390 - Correct ContextInfo parameter?
                            List<String> clus = cluService.getCluIdsByRelatedCluAndRelationType(cluInfo.getId(), "kuali.lu.lu.relation.type.hasVariationProgram", contextInfo);
                            if (clus == null || clus.size() == 0){ 
                                throw new RuntimeException("Statement Dependency clu found, but no parent Program exists"); 
                            } else if(clus.size()>1){ 
                                throw new RuntimeException("Statement Dependency clu can only have one parent Program relation"); 
                            }
                            cluInformation.setParentCluId(clus.get(0));
                        }
                        
                        cluInformation.setVerIndependentId(cluInfo.getId());
                        result.add(cluInformation);
                    }
                } catch (Exception e) {
                    throw new OperationFailedException("Failed to get info for cluId " + cluId, e);
                }
            }
        }
        return result;
    }

    @Override
    public CluSetInformation getCluSetInformation(String cluSetId) throws OperationFailedException {
        ContextInfo contextInfo = ContextUtils.getContextInfo();
        CluSetInformation result = new CluSetInformation();
        //TODO KSCM-390 - Correct ContextInfo parameter?
        CluSetInfo cluSetInfo = getCluSetInfo(cluSetId, contextInfo);
        List<String> allCluIds = cluSetInfo.getCluIds();
        List<String> cluSetIds =  cluSetInfo.getCluSetIds();
        final MembershipQueryInfo membershipQueryInfo = cluSetInfo.getMembershipQuery();
        result.setId(cluSetId);
        if (allCluIds != null) {
            List<CluInformation> clus = getCluInformations(allCluIds, contextInfo);
            result.setClus(clus);
        }
        if (cluSetIds != null) {
            //TODO KSCM-390 - Correct ContextInfo parameter?
            List<CluSetInfo> cluSetInfos = getCluSetInfos(cluSetIds, contextInfo);
            result.setCluSets(cluSetInfos);
        }
        if (membershipQueryInfo != null) {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.setSearchKey(membershipQueryInfo.getSearchTypeKey());
            searchRequest.setParams(membershipQueryInfo.getQueryParamValueList());
            SearchResult searchResult = null;
            try {
                //TODO KSCM-390 - Correct ContextInfo parameter?
                searchResult = cluService.search(searchRequest);
            } catch (Exception e) {
                throw new OperationFailedException("Failed to search for clus in clu range", e);
            }
            List<CluInformation> clusInRange = new ArrayList<CluInformation>();
            List<SearchResultRow> rows = searchResult.getRows();
            for(SearchResultRow row : rows) {
                List<SearchResultCell> cells = row.getCells();
                CluInformation cluInformation = new CluInformation();
                for(SearchResultCell cell : cells) {
                    if(cell.getKey().equals("lu.resultColumn.cluId")) {
                        cluInformation.setVerIndependentId(cell.getValue());
                    }
                    if (cell.getKey().equals("lu.resultColumn.luOptionalCode")) {
                        cluInformation.setCode(cell.getValue());
                    }
                    if (cell.getKey().equals("lu.resultColumn.luOptionalShortName")) {
                        cluInformation.setTitle(cell.getValue());
                    }
                }
                clusInRange.add(cluInformation);
            }
            result.setMembershipQueryInfo(membershipQueryInfo);
            result.setClusInRange(clusInRange);
        }
        if(result.getClus()!=null)
        	Collections.sort(result.getClus());
        return result;
    }
	
}
