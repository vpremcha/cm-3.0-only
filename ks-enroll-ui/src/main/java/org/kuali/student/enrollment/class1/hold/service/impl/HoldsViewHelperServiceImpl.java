package org.kuali.student.enrollment.class1.hold.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.common.uif.service.impl.KSViewHelperServiceImpl;
import org.kuali.student.enrollment.class1.hold.form.AppliedHoldManagementForm;
import org.kuali.student.enrollment.class1.hold.form.AppliedHoldResult;
import org.kuali.student.enrollment.class1.hold.form.HoldIssueManagementForm;
import org.kuali.student.enrollment.class1.hold.form.HoldIssueResult;
import org.kuali.student.enrollment.class1.hold.service.HoldsViewHelperService;
import org.kuali.student.enrollment.class1.hold.util.HoldsConstants;
import org.kuali.student.enrollment.class1.hold.util.HoldsResourceLoader;
import org.kuali.student.r2.core.hold.dto.AppliedHoldInfo;
import org.kuali.student.r2.core.hold.dto.HoldIssueInfo;

import java.util.ArrayList;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.and;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.like;

/**
 * Created with IntelliJ IDEA.
 * User: Blue Team (SA)
 * Date: 17 July 2014
 * <p/>
 * Implementation of the HoldIssueViewHelperService that contains helper methods that support the Hold Issue Management Controller.
 */
public class HoldsViewHelperServiceImpl extends KSViewHelperServiceImpl implements HoldsViewHelperService {

    /**
     * This method is used to search for hold issues and map them to HoldIssueResult
     *
     * @param holdIssueFrom
     * @return List holdIssueResultList
     */
    @Override
    public List<HoldIssueResult> searchHolds(HoldIssueManagementForm holdIssueFrom) {

        List<HoldIssueResult> holdIssueResultList = new ArrayList<HoldIssueResult>();
        List<HoldIssueInfo> holdIssueInfos = new ArrayList<HoldIssueInfo>();
        try {
            QueryByCriteria.Builder query = buildQueryByCriteria(holdIssueFrom.getName(), holdIssueFrom.getTypeKey(),
                    holdIssueFrom.getState(), holdIssueFrom.getOrganizationId(), holdIssueFrom.getDescr());
            holdIssueInfos = HoldsResourceLoader.getHoldService().searchForHoldIssues(query.build(), createContextInfo());

            for (HoldIssueInfo holdIssueInfo : holdIssueInfos) {
                HoldIssueResult holdIssueResult = new HoldIssueResult();
                holdIssueResult.setId(holdIssueInfo.getId());
                holdIssueResult.setName(holdIssueInfo.getName());
                holdIssueResult.setCode(holdIssueInfo.getHoldCode());
                holdIssueResult.setTypeKey(holdIssueInfo.getTypeKey());
                holdIssueResult.setDescr((holdIssueInfo.getDescr() != null ? holdIssueInfo.getDescr().getPlain() : StringUtils.EMPTY));
                holdIssueResult.setOrganizationId(holdIssueInfo.getOrganizationId());
                holdIssueResult.setFirstDate(holdIssueInfo.getFirstAppliedDate());
                holdIssueResult.setLastDate(holdIssueInfo.getLastAppliedDate());
                holdIssueResult.setAuthorization("Authorization");

                holdIssueResultList.add(holdIssueResult);
            }
        } catch (Exception e) {

            convertServiceExceptionsToUI(e);
        }
        return holdIssueResultList;
    }


    private static QueryByCriteria.Builder buildQueryByCriteria(String name, String type, String state, String orgId, String descr) {

        QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
        List<Predicate> pList = new ArrayList<Predicate>();

        qBuilder.setPredicates();
        if (StringUtils.isNotBlank(name)) {
            pList.add(like(HoldsConstants.HOLD_ISSUE_NAME, "%" + name + "%"));
        }

        if (StringUtils.isNotBlank(type)) {
            pList.add(equal(HoldsConstants.HOLD_ISSUE_TYPE_KEY, type));
        }

        if (StringUtils.isNotBlank(state)) {
            pList.add(equal(HoldsConstants.HOLD_ISSUE_STATE_KEY, state));
        }

        if (StringUtils.isNotBlank(orgId)) {
            pList.add(equal(HoldsConstants.HOLD_ISSUE_ORG_ID, orgId));
        }

        if (StringUtils.isNotBlank(descr)) {
            pList.add(like(HoldsConstants.HOLD_ISSUE_DESCR_PLAIN, "%" + descr + "%"));
        }

        if (!pList.isEmpty()) {
            Predicate[] preds = new Predicate[pList.size()];
            pList.toArray(preds);
            qBuilder.setPredicates(and(preds));
        }
        return qBuilder;
    }

    /**
     * This method is used to search for applied holds and map them to AppliedHoldResult
     *
     * @param holdFrom
     * @return List holdResultList
     */
    @Override
    public List<AppliedHoldResult> searchAppliedHolds(AppliedHoldManagementForm holdFrom) {

        List<AppliedHoldResult> holdResultList = new ArrayList<AppliedHoldResult>();
        List<AppliedHoldInfo> appliedHoldInfos = new ArrayList<AppliedHoldInfo>();


        try {
            appliedHoldInfos = HoldsResourceLoader.getHoldService().getAppliedHoldsByPerson(holdFrom.getStudentId(), createContextInfo());

            for (AppliedHoldInfo appliedHoldInfo : appliedHoldInfos) {

                AppliedHoldResult appliedHoldResult = new AppliedHoldResult();
                //appliedHoldResult.setId(appliedHoldInfo.getId());
                appliedHoldResult.setHoldName(appliedHoldInfo.getName());
                appliedHoldResult.setCode("");
                appliedHoldResult.setTypeKey(appliedHoldInfo.getTypeKey());
                appliedHoldResult.setConsequence((appliedHoldInfo.getDescr() != null ? appliedHoldInfo.getDescr().getPlain() : StringUtils.EMPTY));
                appliedHoldResult.setStartDate(appliedHoldInfo.getEffectiveDate());
                appliedHoldResult.setEndDate(appliedHoldInfo.getExpirationDate());
                appliedHoldResult.setStartTerm("");
                appliedHoldResult.setEndTerm("");

                holdResultList.add(appliedHoldResult);
            }
        } catch (Exception e) {

            convertServiceExceptionsToUI(e);
        }
        return holdResultList;
    }

}
