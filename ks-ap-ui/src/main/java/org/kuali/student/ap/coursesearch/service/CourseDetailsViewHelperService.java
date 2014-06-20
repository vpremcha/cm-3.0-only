package org.kuali.student.ap.coursesearch.service;

import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.student.ap.coursesearch.dataobject.ActivityOfferingDetailsWrapper;
import org.kuali.student.ap.coursesearch.dataobject.CourseOfferingDetailsWrapper;
import org.kuali.student.enrollment.courseoffering.dto.ActivityOfferingInfo;
import org.kuali.student.r2.core.acal.infc.Term;

import java.util.List;
import java.util.Map;

/**
 * ViewHelper class used to populate the data on the bottom half of the CourseDetails page (CourseSearchDetailsUI.xml)
 */
public interface CourseDetailsViewHelperService extends ViewHelperService {

    /**
     * Loads up the data objects on the form with data, according to the passed in courseId
     * @param form - Form to populate with data
     * @param courseId - Course ID used to look up the data to be populated on the form
     * @throws Exception
     */
    public void loadCourseSectionDetails(UifFormBase form, String courseId) throws Exception;

    /**
     * Map a list of course offerings to a term
     * @param courseIds - Course IDs used to find the desired course offerings
     * @param terms - List of terms to use
     * @return - A map with the key as the termId and the value being a list of CourseOfferingDetailsWrapper
     * @throws Exception
     */
    public Map<String, List<CourseOfferingDetailsWrapper>> processCourseOfferingsByTerm(List<String> courseIds, List<Term> terms) throws Exception;

    /**
     * Turn an ActivityOfferingInfo into an ActivityOfferingDetailsWrapper
     * @param ao - ActivityOfferingInfo object to harvest data from
     * @return - An ActivityOfferingDetailsWrapper which is a wrapper for an ActivityOfferingInfo
     * @throws Exception
     */
    public ActivityOfferingDetailsWrapper convertAOInfoToWrapper(ActivityOfferingInfo ao) throws Exception;

    /**
     * Sort a list of terms.  Sorting algorithm will be provided by the implementation.
     *
     * @param terms     - List of Terms to be sorted
     * @return - A list of sorted terms
     */
    public List<Term> sortTerms(List<Term> terms);
}
