/**
 * Copyright 2010 The Kuali Foundation Licensed under the Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.osedu.org/licenses/ECL-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.kuali.student.enrollment.courseoffering.infc;

import java.util.Date;
import java.util.List;

import org.kuali.student.r2.common.infc.HasId;
import org.kuali.student.r2.common.infc.MeetingSchedule;
import org.kuali.student.r2.common.infc.RichText;
import org.kuali.student.r2.common.infc.TypeStateEntity;

/**
 * Individual activity offerings correspond to events in a scheduling system, each with a meeting pattern.
 * ActivityOffering map to 
 * @author Kamal
 */

public interface ActivityOffering extends HasId, TypeStateEntity {

    
    /**
     * The system assigned unique id to identify this Activity Offering.
     * Could be implemented as as sequence number or as a UUID.
     *
     * Attempts to set this value on creates should result in a ReadOnlyException being thrown
     *
     * An Id:<ul>
     * <li>An id is used when the actual value is unimportant and can therefore be a large hex value for example
     * <li>An id value might be 23b9ca9bd203df902
     * <li>An Id is never intended to be used directly by an end user.
     * <li>Ids are assumed to be of different values in different KS implementations
     * <li>Id values are generated by the service implementations
     * <li>Id values are never expected to be used in Configuration or Application code
     * </ul>
     * @name Unique Id
     * @readOnly
     * @required on updates
     * @impl maps to a lui id with a type that is one of the Activity Offering types.
     */
    @Override
    public String getId();
    
     /**
     * Unique identifier for the type of this activity offering.
     * 
     * For example: Lecture, Lab, Discussion group, etc.
     * 
     * This is not copied from the canonical but is translated from the canonical based on the Lu type to Lui Type mapping.
     * The initial configuration has this mapping as 1 to 1 but in the future this may not always hold true.
     * 
     * @name Type Key
     * @readOnly on updates
     * @required
     * @impl must be a lui type from the list of activity offering types
     */

    @Override
    public String getTypeKey();
    
     /**
     * Unique identifier for the state of this course offering.
     * i.e. draft, submitted, approved, offered, canceled, etc
     * 
     * @name State Key
     * @required
     * @impl maps to the states that are defined in the kuali.course.offering.process with an initial state of draft
     */

    @Override
    public String getStateKey();
    
    
    /**
     * A description of the Activity Offering.
     * @name Description
     */

    public RichText getDescr();
                
    /**
     * Canonical activity whose instance is this activity offering  
     * @name Activity Id
     */
    public String getActivityId();
       
    /**
     * Alphanumeric character that identifies the section of the course offering
     * @name Activity Code
     */
    public String getActivityCode();   
    
    /**
     * Academic term the activity is being offered in. Should be same as CourseOffering unless changed, then must 
     * be nested term of courseOffering
     * @name Term Id
     * @impl map to Lui.getAtpId
     */
    public String getTermId();
        
    /**
     * Indicates that the course is an Honors Course
     * @name Is Honors Offering
     */
    public Boolean getIsHonorsOffering();
    
    /**
     * The options/scales that indicate the allowable grades that can be awarded.
     * If the value is set here then the canonical course must have a grading option set on the
     * canonical activity
     * 
     * @name: Grading Option Keys
     * @impl maps to Lui.gradingOptions
     */
    public List<String> getGradingOptionKeys();
    

    /**
     * Instructors for the activity. This list should be constrained by the instructors listed on the course offering.
     * @name Instructors
     * @impl maps to Lui.instructors
     */
    public List<? extends OfferingInstructor> getInstructors();
       
    /********** Final Exam Information *****************/
    
    /**
     * Start time of final exam
     * @name Final Exam StartTime
     */
    public Date getFinalExamStartTime();
    
    /**
     * End time of final exam.
     * @name Final Exam EndTime
     */
    public Date getFinalExamEndTime();
    
    /**
     * Space code where final exam will be conducted
     * @name Final Exam Space Code
     */
    public String getFinalExamSpaceCode();


    /********************* Delivery Logistics ************************/
    
    /**
     * When/for how long does the offering meet in class. 
     * Calculated by system based on meeting times; may be validated against canonical.
     * The unit is hours
     * @name Weekly Inclass Contact Hours
     */    
    public Float getWeeklyInclassContactHours();
    
    /**
     * When/for how long does the offering meet out of class.
     * Entered by Scheduler.
     * The unit is hours
     * @name Weekly Outofclass Contact Hours
     */
    public Float getWeeklyOutofclassContactHours();

    /**
     * When/for how long does the offering meet in total.
     * Calculated by system based as sum of In Class and Out of Class hours.
     * The unit is hours
     * @name Weekly Total Contac Hours
     */
    public Float getWeeklyTotalContactHours();

    /**
     * Total maximum number of "seats" or enrollment slots that can be filled for the offering. 
     * @name Maximum Enrollment
     * @impl maps to Lui.maximumEnrollment
     */
    public Integer getMaximumEnrollment();

    /** 
     * Total minimum number of seats that must be filled for the offering not to be canceled. 
     * @name Minimum  Enrollment
     * @impl maps to Lui.minimumEnrollment
     */
    public Integer getMinimumEnrollment();    


    /**************************** Meeting Time and Space *****************/
    
    /**
     * @name Meeting Schedules
     * @impl maps to Lui.meetingSchedules
     */
    public List<? extends MeetingSchedule> getMeetingSchedules();    
}
