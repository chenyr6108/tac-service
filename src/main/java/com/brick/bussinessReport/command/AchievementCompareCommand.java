package com.brick.bussinessReport.command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.command.BaseCommand;
import com.brick.bussinessReport.service.AchievementCompareService;
import com.brick.bussinessReport.to.AchievementTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class AchievementCompareCommand extends BaseCommand {

	Log logger = LogFactory.getLog(AchievementCompareCommand.class);

	private AchievementCompareService achievementCompareService;

	public AchievementCompareService getAchievementCompareService() {
		return achievementCompareService;
	}

	public void setAchievementCompareService(
			AchievementCompareService achievementCompareService) {
		this.achievementCompareService = achievementCompareService;
	}

	public void query(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		Map<String,Object> outputMap=new HashMap<String,Object>();

		String lastMonthBeginDate="";
		String lastMonthEndDate="";
		String currentMonthBeginDate="";
		String currentMonthEndDate="";
		int currentMonthCount=0;
		int lastMonthCount=0;
		List<AchievementTo> searchResult=new ArrayList<AchievementTo>();
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String date=(String)context.contextMap.get("datebegin");
		List<LoanTo> currentLoanList=null;
		List<LoanTo> lastLoanList=null;
		List<LoanTo> currentTotalLoanList=null;

		try {
			if(date==null||"".equals(date)) {
				date=String.valueOf(Calendar.getInstance().get(Calendar.YEAR))+"-"+
				String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1)+"-"+
				String.valueOf(Calendar.getInstance().get(Calendar.DATE));
			}
			//计算当月的工作天数,和上月的工作天数
			context.contextMap.put("DATE",date);
			outputMap.put("DATE",date);
			currentMonthCount=this.achievementCompareService.getCountWorkDayOfCurrentMonth(context);

			//如果上月工作天数为0直接跳转页面
			if(date!=null) {
				if(DateUtil.strToDate(date,"yyyy-MM-dd").compareTo(DateUtil.strToDate("2012-02-01","yyyy-MM-dd"))==-1
						&&DateUtil.strToDate(date,"yyyy-MM-dd").compareTo(DateUtil.strToDate("2011-12-25","yyyy-MM-dd"))==1) {

					context.contextMap.put("DATE",date);
					int count=this.achievementCompareService.getWorkDayCount(context);
					outputMap.put("CURRENT_MONTH_BEGIN_DATE","2011-12-26");
					outputMap.put("CURRENT_MONTH_END_DATE",date);
					outputMap.put("CURRENT_MONTH_COUNT",String.valueOf(count));
					outputMap.put("MSG","上期");

					Output.jspOutput(outputMap,context,"/achievementCompare/achievementCompare.jsp");
					return;
				}
			}
			//如果本月工作天为0直接跳转页面
			if(currentMonthCount==0) {

				resultMap=this.achievementCompareService.getCurrentBeginEndDate(context).get(0);
				currentMonthBeginDate=(String)resultMap.get("BEGIN_DATE");
				currentMonthEndDate=(String)resultMap.get("END_DATE");
				outputMap.put("CURRENT_MONTH_BEGIN_DATE",currentMonthBeginDate);
				outputMap.put("CURRENT_MONTH_END_DATE",currentMonthEndDate);
				outputMap.put("CURRENT_MONTH_COUNT",currentMonthCount);
				outputMap.put("LAST_MONTH_COUNT",lastMonthCount);
				outputMap.put("MSG","本期");

				Output.jspOutput(outputMap,context,"/achievementCompare/achievementCompare.jsp");
				return;
			}

			List<Map<String,Object>> result=this.achievementCompareService.getLastMonthWorkDay(context);

			if(date!=null&&"02".equals(date.toString().split("-")[1])&&"2012".equals(date.toString().split("-")[0])) {
				result=this.achievementCompareService.getLastMonthWorkDayForMonthSecond(context);
			}
			//如果上期没有结果,取出当前区间的开始日期和结束日期
			if(result.size()==0) {
				resultMap=this.achievementCompareService.getCurrentBeginEndDate(context).get(0);
				currentMonthBeginDate=(String)resultMap.get("BEGIN_DATE");
				currentMonthEndDate=(String)resultMap.get("END_DATE");
			}

			for(int i=0;i<result.size();i++) {//result.size是上个月的工作天数
				//当 当月的工作天数小于等于上个月工作天数取值
				if(currentMonthCount<=result.size()) {
					if(currentMonthCount-1==i) {
						lastMonthBeginDate=(String)result.get(0).get("LAST_MONTH_BEGIN_DATE");
						lastMonthEndDate=(String)result.get(i).get("DATE");
						currentMonthBeginDate=(String)result.get(0).get("BEGIN_DATE");
						currentMonthEndDate=(String)result.get(0).get("END_DATE");
						//break;
					}

					//如果当月工作天小于上月工作天,平且当月工作天选择最后天时候,上月也是取最后天工作日
					if(date==null) {
						String currentDate=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
						context.contextMap.put("DATE",currentDate);
						if(currentDate.equals(this.achievementCompareService.getLastDateOfMonth(context).get(0).get("DATE"))) {

							lastMonthBeginDate=(String)result.get(0).get("LAST_MONTH_BEGIN_DATE");
							context.contextMap.put("IS_LAST_MONTH","Y");
							lastMonthEndDate=(String)this.achievementCompareService.getLastDateOfMonth(context).get(0).get("DATE");
							currentMonthBeginDate=(String)result.get(0).get("BEGIN_DATE");
							currentMonthEndDate=currentDate;
							break;
						}
					} else {
						context.contextMap.put("DATE",date);
						if(date.equals(this.achievementCompareService.getLastDateOfMonth(context).get(0).get("DATE"))) {

							lastMonthBeginDate=(String)result.get(0).get("LAST_MONTH_BEGIN_DATE");
							context.contextMap.put("IS_LAST_MONTH","Y");
							lastMonthEndDate=(String)this.achievementCompareService.getLastDateOfMonth(context).get(0).get("DATE");
							currentMonthBeginDate=(String)result.get(0).get("BEGIN_DATE");
							currentMonthEndDate=date;
							break;
						}
					}

				}
				else {//当 当月的工作天数大于上个月工作天数取值
					lastMonthBeginDate=(String)result.get(0).get("LAST_MONTH_BEGIN_DATE");
					lastMonthEndDate=(String)result.get(result.size()-1).get("DATE");
					currentMonthBeginDate=(String)result.get(0).get("BEGIN_DATE");
					currentMonthEndDate=(String)result.get(0).get("END_DATE");
					break;
				}
			}

			//获得上期工作区间的天数
			context.contextMap.put("LAST_BEGIN_DATE",lastMonthBeginDate);
			context.contextMap.put("LAST_END_DATE",lastMonthEndDate);
			lastMonthCount=this.achievementCompareService.getCountWorkDayOfLastMonth(context);

			//获得所有办事处
			context.contextMap.put("decp_id","2");
			List<Map<String,String>> companys=(List<Map<String,String>>)DataAccessor.query("employee.getCompany",context.contextMap,DataAccessor.RS_TYPE.LIST);
			//苏州办事处不需要
			for(int i=0;i<companys.size();i++) {
				if("1".equals(String.valueOf(companys.get(i).get("DECP_ID")))) {
					companys.remove(i);
					break;
				}
			}

			for(int i=0;i<companys.size();i++) {
				AchievementTo to=new AchievementTo();
				to.setDeptId(String.valueOf(companys.get(i).get("DECP_ID")));
				to.setDeptName(companys.get(i).get("DECP_NAME_CN"));
				searchResult.add(to);
			}
			//获得本期业绩
			context.contextMap.put("BEGIN_DATE",currentMonthBeginDate);
			context.contextMap.put("END_DATE",currentMonthEndDate);
			List<Map<String,Object>> currentAchievement=this.achievementCompareService.getCompareAchievement(context);

			//获得上期同区间业绩
			if("2012-01-01".equals(lastMonthBeginDate)) {
				lastMonthBeginDate="2011-12-26";
			}
			context.contextMap.put("BEGIN_DATE",lastMonthBeginDate);
			context.contextMap.put("END_DATE",lastMonthEndDate);
			List<Map<String,Object>> lastAchievement=this.achievementCompareService.getCompareAchievement(context);

			//获得本期目标
			int month=0;
			int yearParam=Calendar.getInstance().get(Calendar.YEAR);
			if(context.contextMap.get("datebegin")==null) {
				month=new Date(System.currentTimeMillis()).getMonth()+1;
			} else {
				month=Integer.valueOf(context.contextMap.get("datebegin").toString().split("-")[1]);
				yearParam=Integer.valueOf(context.contextMap.get("datebegin").toString().split("-")[0]);
			}
			context.contextMap.put("BEGIN_MONTH",month);
			context.contextMap.put("END_MONTH",month);
			context.contextMap.put("YEAR_PARAM",yearParam);
			List<Map<String,Object>> currentTarget=this.achievementCompareService.getCompareTarget(context);

			//获得累计业绩,当年1月到今天
			/*String year=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));*/

			//2012年的累计业绩需要算上2011 12月25至31
			context.contextMap.put("BEGIN_DATE","2012".equals(yearParam)?"2011-12-26":yearParam+"-01-01");
			context.contextMap.put("END_DATE",currentMonthEndDate);
			List<Map<String,Object>> totalAchievement=this.achievementCompareService.getCompareAchievement(context);

			//获得累计目标,当年1月到本月
			context.contextMap.put("BEGIN_MONTH",1);
			context.contextMap.put("END_MONTH",month);
			List<Map<String,Object>> totalTarget=this.achievementCompareService.getCompareTarget(context);

			//获得年度总目标
			context.contextMap.put("BEGIN_MONTH",1);
			context.contextMap.put("END_MONTH",12);
			List<Map<String,Object>> yearTarget=this.achievementCompareService.getCompareTarget(context);

			//配置遍历的数据
			for(int i=0;i<searchResult.size();i++) {

				//加入本期业绩
				for(int j=0;j<currentAchievement.size();j++) {
					if(searchResult.get(i).getDeptId().equals(String.valueOf(currentAchievement.get(j).get("DECP_ID")))) {
						searchResult.get(i).setCurrentAchievement(((java.math.BigDecimal)currentAchievement.get(j).get("PAY_MONEY")).doubleValue());
						break;
					}
				}

				//加入上期业绩
				for(int j=0;j<lastAchievement.size();j++) {
					if(searchResult.get(i).getDeptId().equals(String.valueOf(lastAchievement.get(j).get("DECP_ID")))) {
						searchResult.get(i).setLastAchievement(((java.math.BigDecimal)lastAchievement.get(j).get("PAY_MONEY")).doubleValue());
						break;
					}
				}

				//加入本期目标
				for(int j=0;j<currentTarget.size();j++) {
					if(searchResult.get(i).getDeptId().equals(String.valueOf(currentTarget.get(j).get("DECP_ID")))) {
						searchResult.get(i).setCurrentTarget(((java.math.BigDecimal)currentTarget.get(j).get("TARGET_AMOUNT")).doubleValue());
						break;
					}
				}

				//加入累计业绩
				for(int j=0;j<totalAchievement.size();j++) {
					if(searchResult.get(i).getDeptId().equals(String.valueOf(totalAchievement.get(j).get("DECP_ID")))) {
						searchResult.get(i).setTotalAchievement(((java.math.BigDecimal)totalAchievement.get(j).get("PAY_MONEY")).doubleValue());
						break;
					}
				}

				//加入累计目标
				for(int j=0;j<totalTarget.size();j++) {
					if(searchResult.get(i).getDeptId().equals(String.valueOf(totalTarget.get(j).get("DECP_ID")))) {
						searchResult.get(i).setTotalTarget(((java.math.BigDecimal)totalTarget.get(j).get("TARGET_AMOUNT")).doubleValue());
						break;
					}
				}

				//加入年度目标
				for(int j=0;j<yearTarget.size();j++) {
					if(searchResult.get(i).getDeptId().equals(String.valueOf(yearTarget.get(j).get("DECP_ID")))) {
						searchResult.get(i).setYearTarget(((java.math.BigDecimal)yearTarget.get(j).get("TARGET_AMOUNT")).doubleValue());
						break;
					}
				}

			}

			//加入委贷金额
			//当前业绩
			/*context.contextMap.put("YEAR",date.split("-")[0]);
			context.contextMap.put("BEGIN_DATE",currentMonthBeginDate);
			context.contextMap.put("END_DATE",currentMonthEndDate);
			currentLoanList=this.achievementCompareService.getLoanInfoByPeriod(context);

			//上期业绩
			if("1".equals(date.split("-")[1])) {//如果是1月 上期的年=当前年-1
				context.contextMap.put("YEAR",String.valueOf(Integer.valueOf(date.split("-")[0])-1));
			} else {
				context.contextMap.put("YEAR",date.split("-")[0]);
			}
			context.contextMap.put("BEGIN_DATE",lastMonthBeginDate);
			context.contextMap.put("END_DATE",lastMonthEndDate);
			lastLoanList=this.achievementCompareService.getLoanInfoByPeriod(context);

			//当前总业绩
			context.contextMap.put("YEAR",date.split("-")[0]);
			context.contextMap.put("BEGIN_DATE",context.contextMap.get("YEAR")+"-1-1");
			context.contextMap.put("END_DATE",currentMonthEndDate);
			currentTotalLoanList=this.achievementCompareService.getLoanInfoByPeriod(context);

			for(int i=0;i<searchResult.size();i++) {

				for(int j=0;j<currentLoanList.size();j++) {
					if(searchResult.get(i).getDeptId().equals(currentLoanList.get(j).getDeptId())) {
						double value=searchResult.get(i).getCurrentAchievement()+currentLoanList.get(j).getPayMoney().doubleValue();
						//double totalValue=searchResult.get(i).getTotalAchievement()+currentLoanList.get(j).getPayMoney().doubleValue();
						searchResult.get(i).setCurrentAchievement(value);
						//searchResult.get(i).setTotalAchievement(totalValue);
					}
				}

				for(int j=0;j<lastLoanList.size();j++) {
					if(searchResult.get(i).getDeptId().equals(lastLoanList.get(j).getDeptId())) {
						double value=searchResult.get(i).getLastAchievement()+lastLoanList.get(j).getPayMoney().doubleValue();
						searchResult.get(i).setLastAchievement(value);
					}
				}

				for(int j=0;j<currentTotalLoanList.size();j++) {
					if(searchResult.get(i).getDeptId().equals(currentTotalLoanList.get(j).getDeptId())) {
						double totalValue=searchResult.get(i).getTotalAchievement()+currentTotalLoanList.get(j).getPayMoney().doubleValue();
						searchResult.get(i).setTotalAchievement(totalValue);
					}
				}
			}*/

		} catch(Exception e) {
			context.errList.add("业绩同期表出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		if(context.errList.isEmpty()) {

			outputMap.put("CURRENT_MONTH_BEGIN_DATE",currentMonthBeginDate);
			outputMap.put("CURRENT_MONTH_END_DATE",currentMonthEndDate);
			outputMap.put("LAST_MONTH_BEGIN_DATE",lastMonthBeginDate);
			outputMap.put("LAST_MONTH_END_DATE",lastMonthEndDate);
			outputMap.put("CURRENT_MONTH_COUNT",currentMonthCount);
			outputMap.put("LAST_MONTH_COUNT",lastMonthCount);
			outputMap.put("resultList",searchResult);

			Output.jspOutput(outputMap,context,"/achievementCompare/achievementCompare.jsp");
			if(logger.isDebugEnabled()) {
				logger.debug(log+" end.....");
			}
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
}
