package com.brick.servlet;

import java.io.IOException;
import javax.servlet.ServletContext; 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Timer; 
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.bonus.service.BonusService;
import com.brick.caseReport.service.CaseReportService;
import com.brick.contract.service.LockManagementService;

public class EmailSendToTime extends HttpServlet {

	private Timer timer1 = null;
	private Timer timer2 = null;
	Log logger = LogFactory.getLog(EmailSendToTime.class);

	private long diff=0l;

	public EmailSendToTime() {
		super();
	}

	
	public void destroy() {
		super.destroy();   
        if(timer1!=null){  
            timer1.cancel();  
        }  
        if(timer2!=null) {
        	timer2.cancel();
        }
	}

	
	public void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Put your code here
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
	}

	
	public void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Put your code here
	}

	
	public String getServletInfo() {
		return "This is my default servlet created by Eclipse";
	}

	
	public void init() throws ServletException {

		//计算离下次十二点的时间

		this.diff=getDiffTime("12:00:00");
		// (true为用定时间刷新缓存)  
		String startTask = getInitParameter("startTask");  
		// 启动定时器  
		if(startTask.equals("true")){
			timer1 = new Timer(true); 
			//EmailSendToTimeTask task=new EmailSendToTimeTask(context);
			TimerTask task = new TimerTask() {
				public void run() {
//					logger.debug("batch job for startTask start--------------------");
					//Calendar cal = Calendar.getInstance();
					//        			if(C_SCHEDULE_HOUR!=0)//第一次加载不运行
					//        			{
					//业务
//					            				LockManagementService lock=new LockManagementService();
//					            				lock.sendEmailToTime();
//					            				lock.sendSmSToTime();
//					//            				//lock.sendSmSToDunTime();
//					            				lock.sendSmsDailyForDun();
					//            				//diffDate=24*60*60*1000;
					//        			}
					//        			C_SCHEDULE_HOUR=1;
//					logger.debug("batch job for startTask end  --------------------");
				}
			};
//			timer1.scheduleAtFixedRate(task, diff,24*60*60*1000);
		}   


		//batch job for 案况报表每天晚上8点跑  add by ShenQi
//		String caseReport = getInitParameter("caseReport"); 
//		if("true".equals(caseReport)) {
//			//run every day 20:00:00
//			this.diff=getDiffTime("20:00:00");
//			timer2 = new Timer(true); 
//			TimerTask task1 = new TimerTask() {
//				public void run() {
//					logger.debug("batch job for case report start--------------------");
//					CaseReportService caseReportService=new CaseReportService();
//					caseReportService.batchJob();
//					logger.debug("batch job for case report end  --------------------");
//					
//					logger.debug("batch job for bonus start--------------------");
//					BonusService.bonusJob();
//					logger.debug("batch job for bonus end--------------------");
//				}
//			};
//			timer2.scheduleAtFixedRate(task1,diff,24*60*60*1000);
//		}
	}
	
	private long getDiffTime(String runTime) {
		long diff=0l;
		try {
			DateFormat df = new SimpleDateFormat("HH:mm:ss");
			Date da1=df.parse(runTime);

			Date time=new Date();
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");

			String da2=sf.format(time).toString();
			Date da3=df.parse(da2);

			diff=da1.getTime()-da3.getTime();
			if(diff<0) {
				diff=diff+(24*60*60*1000);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return diff;
	}
}