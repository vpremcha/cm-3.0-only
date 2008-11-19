package org.kuali.student.lum.atp.dao.impl;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.kuali.student.common.test.spring.AbstractServiceTest;
import org.kuali.student.common.test.spring.Client;
import org.kuali.student.common.test.spring.Dao;
import org.kuali.student.common.test.spring.Daos;
import org.kuali.student.common.test.spring.PersistenceFileLocation;
import org.kuali.student.core.dto.AttributeInfo;
import org.kuali.student.lum.atp.dto.AtpInfo;
import org.kuali.student.lum.atp.dto.DateRangeInfo;
import org.kuali.student.lum.atp.dto.MilestoneInfo;
import org.kuali.student.lum.atp.service.AtpService;

@Daos( { @Dao(value = "org.kuali.student.lum.atp.dao.impl.AtpDaoImpl", testDataFile = "classpath:test-beans.xml") })
@PersistenceFileLocation("classpath:META-INF/atp-persistence.xml")
public class TestAtpService extends AbstractServiceTest {
	@Client(value = "org.kuali.student.lum.atp.service.impl.AtpServiceImpl", port = "8181")
	public AtpService client;

	public static final String atpType_fallSemester = "atp.atpType.fallSemester";
	public static final String milestoneType_lastDateToDrop = "atp.milestoneType.lastDateToDrop";
	public static final String dateRangeType_finals = "atp.dateRangeType.finals";
	public static final String atpAttribute_notes = "atp.attribute.notes";
	public static final String dateRangeAttribute_notes = "atp.dateRangeAttribute.notes";
	public static final String milestoneAttribute_notes = "atp.milestoneAttribute.notes";
	public static final String atp_fall2008Semester = "atp.fall2008Semester";
	public static final String milestone_lastDateToDropFall2008 = "atp.milestone.lastDateToDropFall2008";
	public static final String dateRange_finalsFall2008 = "atp.dateRange.finalsFall2008";

	@Test
	public void TestNothing(){
		//Make an ATP
		AtpInfo atpInfo = new AtpInfo();
		atpInfo.setDesc("Atp for fall 2008 semester");
		atpInfo.setName("Fall 2008 Semester");
		atpInfo.setEffectiveDate(new Date());
		atpInfo.setExpirationDate(new Date());
		atpInfo.setState("new");
		
		AttributeInfo notesAttr = new AttributeInfo();
		notesAttr.setKey(atpAttribute_notes);
		notesAttr.setValue("Notes for the Fall 2008 Semester");
		atpInfo.getAttributes().add(notesAttr);
		
		try {
			AtpInfo result = client.createAtp(atpType_fallSemester, atp_fall2008Semester, atpInfo);
			result.getAttributes();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		//Make a DateRange
		DateRangeInfo dateRangeInfo=new DateRangeInfo();
		dateRangeInfo.setDesc("Date Range for fall 2008 semester finals");
		dateRangeInfo.setName("Finals Fall 2008 Semester");
		dateRangeInfo.setStartDate(new Date());
		dateRangeInfo.setEndDate(new Date());
		dateRangeInfo.setState("new");
		dateRangeInfo.setAtpKey(atp_fall2008Semester);
		dateRangeInfo.setType(dateRangeType_finals);

		AttributeInfo dateRangeNotesAttr = new AttributeInfo();
		dateRangeNotesAttr.setKey(dateRangeAttribute_notes);
		dateRangeNotesAttr.setValue("Notes for the Finals date range Fall 2008 Semester");
		dateRangeInfo.getAttributes().add(dateRangeNotesAttr);
		
		try {
			DateRangeInfo result = client.addDateRange(atp_fall2008Semester, dateRange_finalsFall2008, dateRangeInfo);
			result.getAttributes();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 
		
		//Make a Milestone
		MilestoneInfo milestoneInfo=new MilestoneInfo();
		milestoneInfo.setDesc("Milestone for fall 2008 semester last day to drop");
		milestoneInfo.setName("Last Day to Drop Fall 2008 Semester");
		milestoneInfo.setMilestoneDate(new Date());
		milestoneInfo.setState("new");
		milestoneInfo.setAtpKey(atp_fall2008Semester);
		milestoneInfo.setType(milestoneType_lastDateToDrop);

		AttributeInfo milestoneNotesAttr = new AttributeInfo();
		milestoneNotesAttr.setKey(milestoneAttribute_notes);
		milestoneNotesAttr.setValue("Notes for the Last Day to Drop Fall 2008 Semester");
		milestoneInfo.getAttributes().add(milestoneNotesAttr);
		
		try {
			MilestoneInfo result = client.addMilestone(atp_fall2008Semester, milestone_lastDateToDropFall2008, milestoneInfo);
			result.getAttributes();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 
	}
}
