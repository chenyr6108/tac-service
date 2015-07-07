package com.brick.test;

import java.sql.SQLException;

import org.junit.Test;

import com.brick.hr.HrAttendanceEmailService;

public class HrAttendanceEmailTestCase extends BaseTestCase{
	
	@Test
	public void testSendEmail() throws SQLException{
		HrAttendanceEmailService ser = (HrAttendanceEmailService) this.appContext.getBean("hrAttendanceEmailService");
		ser.sendAttenceEmail();
	}
}
