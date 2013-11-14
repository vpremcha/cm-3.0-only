package org.kuali.student.poc.rules.credit.limit;

import java.util.List;
import org.kuali.student.enrollment.academicrecord.dto.LoadInfo;
import org.kuali.student.enrollment.class2.courseregistration.service.decorators.CourseRegistrationServiceDecorator;
import org.kuali.student.enrollment.courseoffering.service.CourseOfferingService;
import org.kuali.student.enrollment.courseregistration.dto.CourseRegistrationInfo;
import org.kuali.student.enrollment.courseregistration.dto.CreditLoadInfo;
import org.kuali.student.enrollment.courseregistration.dto.RegistrationRequestInfo;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;

/**
 * This defines the interface that needs to be implemented to execute a load calculation
 */
public class CourseRegistrationLoadCalculationDecorator extends CourseRegistrationServiceDecorator {

    private CourseOfferingService courseOfferingService;

    public CourseOfferingService getCourseOfferingService() {
        return courseOfferingService;
    }

    public void setCourseOfferingService(CourseOfferingService courseOfferingService) {
        this.courseOfferingService = courseOfferingService;
    }
    // defaults to the simple logic 
    private String loadLevelTypeKey = AcademicRecordServiceTypeStateConstants.LOAD_CALC_CREDITS_INTEGER;

    public String getLoadLevelTypeKey() {
        return loadLevelTypeKey;
    }

    public void setLoadLevelTypeKey(String loadLevelTypeKey) {
        this.loadLevelTypeKey = loadLevelTypeKey;
    }

    @Override
    public CreditLoadInfo calculateCreditLoadForStudentRegistrationRequest(String registrationRequestId, String studentId,
            ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        LoadCalculator calculator = this.getCalculatorForRuleType(this.getLoadLevelTypeKey(), contextInfo);
        MergeRequestAndActual merger = new MergeRequestAndActual();
        merger.setCourseOfferingService(this.getCourseOfferingService());

        List<CourseRegistrationInfo> existingCrs = this.getCourseRegistrationsByStudent(studentId, contextInfo);
        RegistrationRequestInfo emptyRequest = new RegistrationRequestInfo();
        List<CourseRegistrationAction> beforeActions = merger.updateRegistrations(emptyRequest, existingCrs, contextInfo);
        LoadInfo beforeLoad = calculator.calculateLoad(beforeActions, studentId, contextInfo);

        RegistrationRequestInfo currentRequest = this.getRegistrationRequest(registrationRequestId, contextInfo);
        List<CourseRegistrationAction> afterActions = merger.updateRegistrations(currentRequest, existingCrs, contextInfo);
        LoadInfo afterLoad = calculator.calculateLoad(afterActions, studentId, contextInfo);

        int beforeCredits = this.parseCreditsAsInt(beforeLoad.getTotalCredits());
        int afterCredits = this.parseCreditsAsInt(afterLoad.getTotalCredits());
        CreditLoadInfo creditLoad = new CreditLoadInfo();
        creditLoad.setStudentId(studentId);
        creditLoad.setCreditLimit(this.calcStudentCreditLimit(studentId, contextInfo));
        creditLoad.setCreditLoad(beforeCredits + "");
        creditLoad.setAdditionalCredits((afterCredits - beforeCredits) + "");
        return creditLoad;
    }

    private String calcStudentCreditLimit(String studentId, ContextInfo contextInfo) throws OperationFailedException {
//        TODO: call GES to get this
//                ??? do we return override if student had an exemption here?
        return "12";
    }

    private LoadCalculator getCalculatorForRuleType(String loadLevelTypeKey, ContextInfo contextInfo)
            throws OperationFailedException {
        LoadCalculatorRuleUtility util = new LoadCalculatorRuleUtility();
        util.setCourseOfferingService(courseOfferingService);
        return util.getLoadCalculatorForRuleType(loadLevelTypeKey, contextInfo);
    }

    protected int parseCreditsAsInt(String creditString) throws OperationFailedException {
        try {
            int credits = Integer.parseInt(creditString);
            return credits;
        } catch (NumberFormatException ex) {
            throw new OperationFailedException("this implementation only supports integer credits: " + creditString, ex);
        }
    }
}
