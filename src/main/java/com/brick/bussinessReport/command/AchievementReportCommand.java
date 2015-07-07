package com.brick.bussinessReport.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.command.BaseCommand;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.bussinessReport.service.AchievementReportService;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class AchievementReportCommand extends BaseCommand {

	Log logger = LogFactory.getLog(AchievementReportCommand.class);
	
	private AchievementReportService achievementReportService;

	public AchievementReportService getAchievementReportService() {
		return achievementReportService;
	}

	public void setAchievementReportService(
			AchievementReportService achievementReportService) {
		this.achievementReportService = achievementReportService;
	}
	
	@SuppressWarnings("unchecked")
	public void query(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		Map<String,Object> outputMap=new HashMap<String,Object>();
		Map<String,Object> param=new HashMap<String,Object>();
		List<Map<String,Object>> targetList=null;
		List<Map<String,Object>> achievementList=null;
		//获得所有月份
		List<String> monthList=new ArrayList<String>();
		for(int i=1;i<13;i++) {
			monthList.add(String.valueOf(i));
		}
		outputMap.put("MONTH",context.contextMap.get("MONTH"));

		//获得办事处的drop down List
		context.contextMap.put("decp_id", "2");//2代表的是拿分公司
		List<Map> companyList=this.achievementReportService.getDeptList(context);

		//获得业绩表中所存在的年份
		List<String> yearList=this.achievementReportService.getYearAchievementReport(context);
		
		//保存页面的查询条件
		if("".equals(context.contextMap.get("YEAR"))||context.contextMap.get("YEAR")==null) {
			//获得系统年份,初始化进入时候拿系统年份
			outputMap.put("YEAR",Calendar.getInstance().get(Calendar.YEAR));
		} else {
			outputMap.put("YEAR",context.contextMap.get("YEAR"));
		}
		outputMap.put("DEPT",context.contextMap.get("DEPT"));
				
		//获得所有办事处目标
		param.put("YEAR",outputMap.get("YEAR"));
		param.put("CMPY_ID",context.contextMap.get("DEPT"));
		param.put("MONTH",context.contextMap.get("MONTH"));
		try {
			targetList=this.achievementReportService.getTargetByYear(param);
			
			if(StringUtils.isEmpty(context.contextMap.get("MONTH"))) {
				//月份选择全部
				for(int i=1;i<13;i++) {
					ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(Integer.valueOf(param.get("YEAR")+""),i);
					
					param.put("startTime",to.getBeginTime());
					param.put("endTime",to.getEndTime());
					achievementList=this.achievementReportService.getAchievementByYear(param);
					
					for(int j=0;targetList!=null&&j<targetList.size();j++) {//把业绩组装到目标中去
						if(targetList.get(j).get("MONTH").toString().equals(i+"")) {
							for(int k=0;achievementList!=null&&k<achievementList.size();k++) {
								if((targetList.get(j).get("DECP_ID")+"").equals(achievementList.get(k).get("DECP_ID")+"")) {
									targetList.get(j).put("PAY_MONEY",achievementList.get(k).get("PAY_MONEY"));
									break;
								}
							}
						}
					}
				}
				
				ReportDateTo to=ReportDateUtil.getDateByYear(Integer.valueOf(param.get("YEAR")+""));
				
				param.put("startTime",to.getBeginTime());
				param.put("endTime",to.getEndTime());
				achievementList=this.achievementReportService.getAchievementByYear(param);
				
				for(int j=0;targetList!=null&&j<targetList.size();j++) {
					if(Integer.valueOf(targetList.get(j).get("MONTH")+"")==1) {
						for(int k=0;achievementList!=null&&k<achievementList.size();k++) {
							if((targetList.get(j).get("DECP_ID")+"").equals(achievementList.get(k).get("DECP_ID")+"")) {
								targetList.get(j).put("TOTAL_PAY_MONEY",achievementList.get(k).get("PAY_MONEY"));
							}
						}
					} else {
						break;
					}
				}
			} else {
				//选择了某个月
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(Integer.valueOf(param.get("YEAR")+""),Integer.valueOf(context.contextMap.get("MONTH")+""));
				
				param.put("startTime",to.getBeginTime());
				param.put("endTime",to.getEndTime());
				achievementList=this.achievementReportService.getAchievementByYear(param);
				
				for(int j=0;targetList!=null&&j<targetList.size();j++) {//把业绩组装到目标中去
					if(targetList.get(j).get("MONTH").toString().equals(context.contextMap.get("MONTH")+"")) {
						for(int k=0;achievementList!=null&&k<achievementList.size();k++) {
							if((targetList.get(j).get("DECP_ID")+"").equals(achievementList.get(k).get("DECP_ID")+"")) {
								targetList.get(j).put("PAY_MONEY",achievementList.get(k).get("PAY_MONEY"));
								break;
							}
						}
					}
				}
				
				for(int j=0;targetList!=null&&j<targetList.size();j++) {
					if(Integer.valueOf(targetList.get(j).get("MONTH")+"")==1) {
						for(int k=0;achievementList!=null&&k<achievementList.size();k++) {
							if((targetList.get(j).get("DECP_ID")+"").equals(achievementList.get(k).get("DECP_ID")+"")) {
								targetList.get(j).put("TOTAL_PAY_MONEY",achievementList.get(k).get("PAY_MONEY"));
							}
						}
					} else {
						break;
					}
				}
			}
		} catch (Exception e) {
			
		}
		if(context.errList.isEmpty()) {
			
			outputMap.put("resultList",targetList);
			outputMap.put("yearList",yearList);
			outputMap.put("monthList",monthList);
			outputMap.put("companyList",companyList);

			if(logger.isDebugEnabled()) {
				logger.debug(log+" end.....");
			}
			Output.jspOutput(outputMap,context,"/achievementReport/achievementReport.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void showDetail(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......showDetail";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map outputMap=new HashMap();
		
		context.contextMap.put("JOB","业务员");
		context.contextMap.put("DEPT",context.contextMap.get("SELECT_DEPT"));
		context.contextMap.put("YEAR",context.contextMap.get("SELECT_YEAR"));
		
		List<Map> resultList=this.achievementReportService.showDetailByDeptId(context);
		
		List<Map> achievementList=this.achievementReportService.getDetailAchievement(context);
		
		//List<LoanTo> loanList=this.achievementReportService.getLoanInfoGroupByUser(context);
		
		for(int i=0;resultList!=null&&i<resultList.size();i++) {
			for(int j=0;achievementList!=null&&j<achievementList.size();j++) {
				if(String.valueOf(resultList.get(i).get("EMPL_ID")).equals(String.valueOf(achievementList.get(j).get("EMP_ID")))) {
					resultList.get(i).put("ACHIEVEMENT"+achievementList.get(j).get("MONTH"),achievementList.get(j).get("MONTH_PAY_MONEY"));
				}
			}
			
			/*for(int j=0;loanList!=null&&j<loanList.size();j++) {
				if(String.valueOf(resultList.get(i).get("EMPL_ID")).equals(loanList.get(j).getUserId())) {
					resultList.get(i).put("ACHIEVEMENT"+String.valueOf(loanList.get(j).getMonth()),
							((BigDecimal)resultList.get(i).get("ACHIEVEMENT"+String.valueOf(loanList.get(j).getMonth()))).doubleValue()+loanList.get(j).getPayMoney().doubleValue());
					break;
				}
			}*/
		}
		if(context.errList.isEmpty()) {
			outputMap.put("YEAR",context.contextMap.get("SELECT_YEAR"));
			outputMap.put("DEPT_NAME",context.contextMap.get("SELECT_NAME"));
			outputMap.put("resultList",resultList);
			Output.jspOutput(outputMap,context,"/achievementReport/achievementDetailReport.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
}
