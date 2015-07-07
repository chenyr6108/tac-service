package com.brick.hr;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

import com.brick.base.service.BaseService;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;



public class HrAttendanceEmailService extends BaseService {
	private BasicDataSource dataSource;
	private MailUtilService mailUtilService;

	public BasicDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void sendAttenceEmail() {
		Calendar c = Calendar.getInstance();
		if(c.get(Calendar.DAY_OF_WEEK)!=2){//星期一发上周的
			return;
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		c.add(Calendar.DAY_OF_MONTH, -1);
		String endDate = df.format(c.getTime());
		c.add(Calendar.DAY_OF_MONTH, -6);
		String beginDate = df.format(c.getTime());
		//select * from Positions
		String sql = "SELECT t.Reg_Code, u.LoginCode FROM JHHR_User t left join Users u on u.UserID = t.Reg_Code left join relationshipusers p on p.UserID = t.Reg_Code and p.RelaPrimary =1 where t.Reg_Code <> 'Admin' and t.Reg_Code <> 'whj' and p.PosiID not in (1,1004,1011,1018,1023,1024,1157,1158,1159)";
		String sql_attendance = "{call jhhr_attendance_appealSearch(?,?,?,?,?)}";
		Connection con = null;
		try {
			con  = dataSource.getConnection();
			//查询当前未离职的用户
			PreparedStatement ps  = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			List<Map> users =  new ArrayList<Map>();
			while(rs.next()){
				String regCode = rs.getString(1);
				String loginCode = rs.getString(2);
				Map user = new HashMap();
				user.put("regCode", regCode);
				user.put("loginCode", loginCode);
				users.add(user);
			}
			rs.close();
			ps.close();
			//查询用户的考勤信息

	
			CallableStatement cs = null;

			for(Map user:users){

				
				cs = con.prepareCall(sql_attendance);
				cs.setString(1, (String)user.get("regCode"));
				cs.setString(2, beginDate);
				cs.setString(3, endDate);
				cs.setString(4, "");
				cs.setInt(5, 1);
				cs.execute();
				
				int count  = 0;
				ResultSet rs2 = cs.getResultSet();
				while(rs2.next()){					
					count = rs2.getInt(1);					
				}
				cs.close();
				rs2.close();
				if(count>0){
					String emailAddress = user.get("loginCode")+"@tacleasing.cn";
					MailSettingTo email = new MailSettingTo(); 						
					email.setEmailSubject("考勤异常提醒");
					email.setEmailContent("您好：<br>    您上周有"+count+"次考勤异常未处理，请尽快登录人资系统进行考勤申诉。");
					email.setEmailTo(emailAddress);	
					mailUtilService.sendMail(email);
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	
	
}
