package org.kuali.student.cm.course.form;

import org.kuali.student.cm.common.util.CurriculumManagementConstants;
import org.kuali.student.common.uif.form.KSUifMaintenanceDocumentForm;

/**
 * Created by venkateshpremchandran on 10/22/14.
 */
public class ProposalMaintenanceForm extends KSUifMaintenanceDocumentForm {

    protected boolean showMessage;

    protected CurriculumManagementConstants.UserInterfaceSections selectedSection;

    // Disallows any workflow action being taken against the document immediately after a workflow action has been performed
    protected boolean pendingWorkflowAction = false;

    public ProposalMaintenanceForm(){
        super();
        selectedSection = CurriculumManagementConstants.CourseViewSections.COURSE_INFO;
    }

    public boolean isPendingWorkflowAction() {
        return pendingWorkflowAction;
    }

    public void setPendingWorkflowAction(boolean pendingWorkflowAction) {
        this.pendingWorkflowAction = pendingWorkflowAction;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public CurriculumManagementConstants.UserInterfaceSections getSelectedSection() {
        return selectedSection;
    }

    public void setSelectedSection(CurriculumManagementConstants.UserInterfaceSections selectedSection) {
        this.selectedSection = selectedSection;
    }

}
