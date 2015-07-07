package com.brick.bussinessReport.command;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.bussinessReport.service.AchievementCaseService;
import com.brick.bussinessReport.service.AchievementReportService;
import com.brick.bussinessReport.to.AchievementTo;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class AchievementCaseCommand extends BaseCommand {

	Log logger = LogFactory.getLog(AchievementCaseCommand.class);
	
	private AchievementCaseService achievementCaseService;
	private AchievementReportService achievementReportService;
	private MailUtilService mailUtilService;
	
	public AchievementCaseService getAchievementCaseService() {
		return achievementCaseService;
	}

	public void setAchievementCaseService(
			AchievementCaseService achievementCaseService) {
		this.achievementCaseService = achievementCaseService;
	}
	
	public AchievementReportService getAchievementReportService() {
		return achievementReportService;
	}

	public void setAchievementReportService(
			AchievementReportService achievementReportService) {
		this.achievementReportService = achievementReportService;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	@Transactional(rollbackFor=Exception.class)
	public void batchJob() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for 客户案况汇总 start  --------------------");
		}
		List<AchievementTo> insertData=new ArrayList<AchievementTo>();
		
		//获得办事处
		Context context=new Context(null,null,null);
		context.contextMap.put("decp_id", "2");//2代表的是拿分公司
		List<Map> deptList=this.achievementReportService.getDeptList(context);
		for(int i=0;deptList!=null&&i<deptList.size();i++) {
			AchievementTo achievementTo=new AchievementTo();
			achievementTo.setDeptId(String.valueOf(deptList.get(i).get("DECP_ID")));
			achievementTo.setDeptName((String)deptList.get(i).get("DECP_NAME_CN"));
			insertData.add(achievementTo);
		}
		
		
		//获得当日拨款金额
		List<AchievementTo> payMoneyList=this.achievementCaseService.getPayMoney();
		//获得当日拨款件数
		List<AchievementTo> payCountList=this.achievementCaseService.getPayCount();
		
		//获得当日审查通过的案件数
		List<AchievementTo> approveList=this.achievementCaseService.getApproveCount();
		
		//获得当日送审案件
		List<AchievementTo> pendingApproveList=this.achievementCaseService.getPendingApproveCount();
		
		//获得当日入保证金案件
		List<AchievementTo> cautionCountList=this.achievementCaseService.getCautionCount();
		
		//获得当前在审案件  当日查询正在待评审中的案件
		List<AchievementTo> auditCountList=this.achievementCaseService.getAuditCount();
		
		//通过今天拿结账周期时间
		ReportDateTo to=ReportDateUtil.getDateByDate(DateUtil.dateToString(Calendar.getInstance().getTime(),"yyyy-MM-dd"));
		Map<String,Object> param1=new HashMap<String,Object>();
		param1.put("startTime",to.getBeginTime());
		param1.put("endTime",to.getEndTime());
		
		//获得本月总拨款金额
		List<AchievementTo> achievementMoneyList=this.achievementCaseService.getAchievementMoney(param1);
		//获得本月总拨款件数
		List<AchievementTo> achievementCountList=this.achievementCaseService.getAchievementCount(param1);
		
		//获得本月尾款金额件数
		List<AchievementTo> lastAchievementCountMoneyList=this.achievementCaseService.getLastAchievementCountMoney(param1);
		
		//如果首拨款和尾款是同一天,需要处理
		List<AchievementTo> financeSameDayList=this.achievementCaseService.getFinanceDateInOneDayCount();
		for(int i=0;financeSameDayList!=null&&i<financeSameDayList.size();i++) {
			boolean flag=true;
			for(int j=0;j<lastAchievementCountMoneyList.size();j++) {
				if(financeSameDayList.get(i).getDeptId().equals(lastAchievementCountMoneyList.get(j).getDeptId())) {
					lastAchievementCountMoneyList.get(j).setLastAchievementCount(lastAchievementCountMoneyList.get(j).getLastAchievementCount()+
							financeSameDayList.get(i).getLastAchievementCount());
					
					AchievementTo resultTo=this.achievementCaseService.getFinanceDateInOneDayMoney(financeSameDayList.get(i));
					lastAchievementCountMoneyList.get(j).setLastAchievementMoney(lastAchievementCountMoneyList.get(j).getLastAchievementMoney()+
							resultTo.getLastAchievementMoney());
					
					flag=false;
					break;
				}
			}
			if(flag) {//如果lastAchievementCountMoneyList中没有此办事处,则需要加入数据进去
				AchievementTo resultTo=this.achievementCaseService.getFinanceDateInOneDayMoney(financeSameDayList.get(i));
				resultTo.setLastAchievementCount(financeSameDayList.get(i).getLastAchievementCount());
				lastAchievementCountMoneyList.add(resultTo);
			}
		}
		
		//获得本月任务
		List<AchievementTo> targetMoneyList=this.achievementCaseService.getTargetMoney();
		
		String date=null;
		AchievementTo nextDay=new AchievementTo();
		//循环设置14,最长的休假不可能超过14天
		for(int i=1;i<14;i++) {
			//初始化是拿明天
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,i);
			date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			nextDay=this.achievementCaseService.getNextWorkDay(date);
			if(Constants.WD.equals(nextDay.getDayType())) {
				//如果下一天为工作日则跳出循环,否则继续取
				break;
			}
		}
		
		Map<String,Object> paramMap=new HashMap<String,Object>();
		//中信银行乘用车委贷
		paramMap.put("flag","DAY");
		List<Map<String,Object>> carCountMoneyToday=(List<Map<String,Object>>)DataAccessor.query("loan.getCarMoneyCount",paramMap,RS_TYPE.LIST);
		
		paramMap.put("flag","MONTH");
		List<Map<String,Object>> carCountMoneyMonth=(List<Map<String,Object>>)DataAccessor.query("loan.getCarMoneyCount",paramMap,RS_TYPE.LIST);
		
		paramMap.put("flag","NEXT_DAY");
		List<Map<String,Object>> carCountMoneyNextDay=(List<Map<String,Object>>)DataAccessor.query("loan.getCarMoneyCount",paramMap,RS_TYPE.LIST);
		
		//获得隔日拨款金额
		List<AchievementTo> nextDayPayMoneyList=this.achievementCaseService.getNextDayPayMoney(date);
		
		List<AchievementTo> infoAcessAuditApproveList=this.achievementCaseService.getInfoAcessAuditApprove();
		
		//加入数据
		for(int i=0;i<insertData.size();i++) {
			for(int j=0;j<payMoneyList.size();j++) {
				if(payMoneyList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setPayMoney(payMoneyList.get(j).getPayMoney());
					break;
				}
			}
			for(int j=0;j<payCountList.size();j++) {
				if(payCountList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setPayCount(payCountList.get(j).getPayCount());
					break;
				}
			}
			
			for(int j=0;j<carCountMoneyToday.size();j++) {
				if(insertData.get(i).getDeptId().equals(carCountMoneyToday.get(j).get("DEPT_ID").toString())) {
					insertData.get(i).setPayMoney(insertData.get(i).getPayMoney()+Double.valueOf(carCountMoneyToday.get(j).get("PAY_MONEY").toString()));
					break;
				}
				if(insertData.get(i).getDeptId().equals(carCountMoneyToday.get(j).get("DEPT_ID").toString())) {
					insertData.get(i).setPayCount(insertData.get(i).getPayCount()+Integer.valueOf(carCountMoneyToday.get(j).get("PAY_COUNT").toString()));
					break;
				}
			}
			
			for(int j=0;j<approveList.size();j++) {
				if(approveList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setApproveCount(approveList.get(j).getApproveCount());
					break;
				}
			}
			
			for(int j=0;j<pendingApproveList.size();j++) {
				if(pendingApproveList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setPendingApproveCount(pendingApproveList.get(j).getPendingApproveCount());
					break;
				}
			}
			
			for(int j=0;j<cautionCountList.size();j++) {
				if(cautionCountList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setCautionCount(cautionCountList.get(j).getCautionCount());
					break;
				}
			}
			
			for(int j=0;j<auditCountList.size();j++) {
				if(auditCountList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setAuditCount(auditCountList.get(j).getAuditCount());
					break;
				}
			}
			
			for(int j=0;j<infoAcessAuditApproveList.size();j++) {
				if(infoAcessAuditApproveList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setHasApproveCount(infoAcessAuditApproveList.get(j).getApproveCount1());
					insertData.get(i).setHasApproveAmount(infoAcessAuditApproveList.get(j).getApproveAmount1());
					break;
				}
			}
			
			for(int j=0;j<achievementMoneyList.size();j++) {
				if(achievementMoneyList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setAchievementMoney(achievementMoneyList.get(j).getAchievementMoney());
					break;
				}
			}
			for(int j=0;j<achievementCountList.size();j++) {
				if(achievementCountList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setAchievementCount(achievementCountList.get(j).getAchievementCount());
					break;
				}
			}
			
			for(int j=0;j<lastAchievementCountMoneyList.size();j++) {
				if(lastAchievementCountMoneyList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setLastAchievementCount(lastAchievementCountMoneyList.get(j).getLastAchievementCount());
					insertData.get(i).setLastAchievementMoney(lastAchievementCountMoneyList.get(j).getLastAchievementMoney());
					break;
				}
			}
			
			for(int j=0;j<targetMoneyList.size();j++) {
				if(targetMoneyList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setTargetMoney(targetMoneyList.get(j).getTargetMoney());
					break;
				}
			}
			
			for(int j=0;j<nextDayPayMoneyList.size();j++) {
				if(nextDayPayMoneyList.get(j).getDeptId().equals(insertData.get(i).getDeptId())) {
					insertData.get(i).setNextDayPayMoney(nextDayPayMoneyList.get(j).getNextDayPayMoney());
					break;
				}
			}
			
			for(int j=0;j<carCountMoneyMonth.size();j++) {
				if(insertData.get(i).getDeptId().equals(carCountMoneyMonth.get(j).get("DEPT_ID").toString())) {
					insertData.get(i).setAchievementMoney(insertData.get(i).getAchievementMoney()+Double.valueOf(carCountMoneyMonth.get(j).get("PAY_MONEY").toString()));
					break;
				}
				if(insertData.get(i).getDeptId().equals(carCountMoneyMonth.get(j).get("DEPT_ID").toString())) {
					insertData.get(i).setAchievementCount(insertData.get(i).getAchievementCount()+Integer.valueOf(carCountMoneyMonth.get(j).get("PAY_COUNT").toString()));
					break;
				}
			}
			
			for(int j=0;j<carCountMoneyNextDay.size();j++) {
				if(insertData.get(i).getDeptId().equals(carCountMoneyNextDay.get(j).get("DEPT_ID").toString())) {
					insertData.get(j).setNextDayPayMoney(insertData.get(j).getNextDayPayMoney()+Double.valueOf(carCountMoneyNextDay.get(j).get("PAY_MONEY").toString()));
					break;
				}
			}
		}
		
		//插入数据
		try {
			for(int i=0;i<insertData.size();i++) {
				if(!"1".equals(insertData.get(i).getDeptId())) {//苏州办事处(code=1)不需要插入
					//防止主键重复
					Thread.sleep(1);
					insertData.get(i).setAchievementId(String.valueOf(System.currentTimeMillis()));
					this.achievementCaseService.insertData(insertData.get(i));
				}
			}
			
			if(!super.baseService.isWorkingDay()) {
				//如果是holiday不发送mail
				return;
			}
			sendForEquipment(infoAcessAuditApproveList);
			sendForCar(infoAcessAuditApproveList);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for 客户案况汇总 end  --------------------");
		}
	}
	
	public void sendForEquipment(List<AchievementTo> infoAcessAuditApproveList) throws Exception{
		//准备插入发送邮件内容
		Context param=new Context(null,null,null);
		Calendar cal=Calendar.getInstance();
		String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
		param.contextMap.put("DATE",date);
		param.contextMap.put("SORT_TYPE","DEPT_NAME");
		param.contextMap.put("isCar","no");
		List<AchievementTo> resultList=this.achievementCaseService.query(param);
		
		for(int i=0;i<resultList.size();i++) {
			for(int j=0;j<infoAcessAuditApproveList.size();j++) {
				if(resultList.get(i).getDeptId().equals(infoAcessAuditApproveList.get(j).getDeptId())) {
					resultList.get(i).setInfoAmount(infoAcessAuditApproveList.get(j).getInfoAmount());
					resultList.get(i).setInfoCount(infoAcessAuditApproveList.get(j).getInfoCount());
					resultList.get(i).setHasAccessAmount(infoAcessAuditApproveList.get(j).getHasAccessAmount());
					resultList.get(i).setHasAccessCount(infoAcessAuditApproveList.get(j).getHasAccessCount());
					resultList.get(i).setAuditAmount1(infoAcessAuditApproveList.get(j).getAuditAmount1());
					resultList.get(i).setAuditCount1(infoAcessAuditApproveList.get(j).getAuditCount1());
					resultList.get(i).setApproveAmount1(infoAcessAuditApproveList.get(j).getApproveAmount1());
					resultList.get(i).setApproveCount1(infoAcessAuditApproveList.get(j).getApproveCount1());
					break;
				}
			}
		}
		StringBuffer mailContent=new StringBuffer();
		
		mailContent.append("<html><head></head>");
		
		mailContent.append("<style>.rhead { background-color: #006699}" +
				".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}" +
				".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }" +
				".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }" +
				".r11 {  background-color: #C4E2EC}" +
				".r12 { background-color: #D2EFF0}</style><body>");
		
		mailContent.append("<font size='3'><b>各位长官好:<b><br></font>" +
				"<font size='2'>当日各办事处业绩进度一览表(单位:K)</font><br><br>");
		
		mailContent.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>" +
				"<tr class='rhead'>" +
				"<td class='Body2BoldWhite2' style='width:40px;text-align:center' rowspan='2' align='center'>序号</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' rowspan='2' align='center'>办事处</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>当日拨款案件</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' rowspan='2' align='center'>本期目标</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>当月累计拨款</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' rowspan='2' align='center'>达成率</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' rowspan='2' align='center'>隔日预拨款</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>资料中</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>已访厂</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>审核中</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>已核准</td>" +
				"<tr class='rhead'>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td></tr>" );
		
		int num=0;
		int payCountTotal=0;
		double payMoneyTotal=0;
		double targetMoneyTotal=0;
		int achievementCountTotal=0;
		double achievementMoneyTotal=0;
		int lastAchievementCountTotal=0;
		double lastAchievementMoneyTotal=0;
		double nextDayPayMoneyTotal=0;
		int hasApproveCountTotal=0;
		double hasApproveAmountTotal=0;
		int pendingApproveCountTotal=0;
		int approveCountTotal=0;
		int auditCountTotal=0;
		int infoCountTotal=0;
		double infoAmountTotal=0;
		int hasAccessCountTotal=0;
		double hasAccessAmountTotal=0;
		int auditCount1Total=0;
		double auditAmount1Total=0;
		int approveCount1Total=0;
		double approveAmount1Total=0;
		DecimalFormat dfMoney=new DecimalFormat("#,##0.00");
		DecimalFormat dfPer=new DecimalFormat("##0.00");
		for(int i=0;i<resultList.size();i++) {
			num++;
			payCountTotal=payCountTotal+resultList.get(i).getPayCount();
			payMoneyTotal=payMoneyTotal+resultList.get(i).getPayMoney();
			targetMoneyTotal=targetMoneyTotal+resultList.get(i).getTargetMoney();
			achievementCountTotal=achievementCountTotal+resultList.get(i).getAchievementCount();
			achievementMoneyTotal=achievementMoneyTotal+resultList.get(i).getAchievementMoney();
			lastAchievementCountTotal=lastAchievementCountTotal+resultList.get(i).getLastAchievementCount();
			lastAchievementMoneyTotal=lastAchievementMoneyTotal+resultList.get(i).getLastAchievementMoney();
			nextDayPayMoneyTotal=nextDayPayMoneyTotal+resultList.get(i).getNextDayPayMoney();
			hasApproveCountTotal=hasApproveCountTotal+resultList.get(i).getHasApproveCount();
			hasApproveAmountTotal=hasApproveAmountTotal+resultList.get(i).getHasApproveAmount();
			pendingApproveCountTotal=pendingApproveCountTotal+resultList.get(i).getPendingApproveCount();
			approveCountTotal=approveCountTotal+resultList.get(i).getApproveCount();
			auditCountTotal=auditCountTotal+resultList.get(i).getAuditCount();
			infoCountTotal=infoCountTotal+resultList.get(i).getInfoCount();
			infoAmountTotal=infoAmountTotal+resultList.get(i).getInfoAmount();
			hasAccessCountTotal=hasAccessCountTotal+resultList.get(i).getHasAccessCount();
			hasAccessAmountTotal=hasAccessAmountTotal+resultList.get(i).getHasAccessAmount();
			auditCount1Total=auditCount1Total+resultList.get(i).getAuditCount1();
			auditAmount1Total=auditAmount1Total+resultList.get(i).getAuditAmount1();
			approveCount1Total=approveCount1Total+resultList.get(i).getApproveCount1();
			approveAmount1Total=approveAmount1Total+resultList.get(i).getApproveAmount1();
			
			mailContent.append("<tr class='r12'>");
			mailContent.append("<td class=body2 style='text-align:center;'>"+num+"</td>" +
					"<td class=body2 style='text-align:center;'>"+resultList.get(i).getDeptName()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getPayCount()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getPayMoney())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getTargetMoney())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getAchievementCount()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getAchievementMoney())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getAchievement()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getNextDayPayMoney())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getInfoCount()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getInfoAmount())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getHasAccessCount()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getHasAccessAmount())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getAuditCount1()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getAuditAmount1())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getApproveCount1()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getApproveAmount1())+"</td>" );
			mailContent.append("</tr>");
		}
		mailContent.append("<tr class='rhead'>" +
				"<td class='Body2BoldWhite2' style='text-align:center;font-size:9pt;' colspan='2'>总计</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+payCountTotal+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(payMoneyTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(targetMoneyTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+achievementCountTotal+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(achievementMoneyTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfPer.format(achievementMoneyTotal/targetMoneyTotal*100)+"%</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(nextDayPayMoneyTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+infoCountTotal+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(infoAmountTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+hasAccessCountTotal+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(hasAccessAmountTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+auditCount1Total+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(auditAmount1Total)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+approveCount1Total+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(approveAmount1Total)+"</td>" +
				"</tr></table><br><br>");
		mailContent.append("<font color='#FF0000'>*</font><font size='2'>当日各区业绩进度一览表不含委贷案件</font>");
		mailContent.append("</body></html>");
		MailSettingTo mailSettingTo=new MailSettingTo();
		mailSettingTo.setEmailSubject("当日各办事处业绩进度一览表(机器设备)");
		mailSettingTo.setEmailContent(mailContent.toString());
		this.mailUtilService.sendMail(2008,mailSettingTo);
	}
	
	public void sendForCar(List<AchievementTo> infoAcessAuditApproveList) throws Exception{
		//准备插入发送邮件内容
		Context param=new Context(null,null,null);
		Calendar cal=Calendar.getInstance();
		String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
		param.contextMap.put("DATE",date);
		param.contextMap.put("SORT_TYPE","DEPT_NAME");
		param.contextMap.put("isCar","yes");
		List<AchievementTo> resultList=this.achievementCaseService.query(param);
		
		for(int i=0;i<resultList.size();i++) {
			for(int j=0;j<infoAcessAuditApproveList.size();j++) {
				if(resultList.get(i).getDeptId().equals(infoAcessAuditApproveList.get(j).getDeptId())) {
					resultList.get(i).setInfoAmount(infoAcessAuditApproveList.get(j).getInfoAmount());
					resultList.get(i).setInfoCount(infoAcessAuditApproveList.get(j).getInfoCount());
					resultList.get(i).setHasAccessAmount(infoAcessAuditApproveList.get(j).getHasAccessAmount());
					resultList.get(i).setHasAccessCount(infoAcessAuditApproveList.get(j).getHasAccessCount());
					resultList.get(i).setAuditAmount1(infoAcessAuditApproveList.get(j).getAuditAmount1());
					resultList.get(i).setAuditCount1(infoAcessAuditApproveList.get(j).getAuditCount1());
					resultList.get(i).setApproveAmount1(infoAcessAuditApproveList.get(j).getApproveAmount1());
					resultList.get(i).setApproveCount1(infoAcessAuditApproveList.get(j).getApproveCount1());
					break;
				}
			}
		}
		StringBuffer mailContent=new StringBuffer();
		
		mailContent.append("<html><head></head>");
		
		mailContent.append("<style>.rhead { background-color: #006699}" +
				".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}" +
				".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }" +
				".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }" +
				".r11 {  background-color: #C4E2EC}" +
				".r12 { background-color: #D2EFF0}</style><body>");
		
		mailContent.append("<font size='3'><b>各位长官好:<b><br></font>" +
				"<font size='2'>当日各办事处业绩进度一览表(单位:K)</font><br><br>");
		
		mailContent.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>" +
				"<tr class='rhead'>" +
				"<td class='Body2BoldWhite2' style='width:40px;text-align:center' rowspan='2' align='center'>序号</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' rowspan='2' align='center'>办事处</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>当日拨款案件</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' rowspan='2' align='center'>本期目标</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>当月累计拨款</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' rowspan='2' align='center'>达成率</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' rowspan='2' align='center'>隔日预拨款</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>资料中</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>已访厂</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>审核中</td>" +
				"<td class='Body2BoldWhite2' colspan='2' align='center' style='text-align:center'>已核准</td>" +
				"<tr class='rhead'>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td>" +
				"<td class='Body2BoldWhite2' style='width:70px;text-align:center' align='center'>件数</td>" +
				"<td class='Body2BoldWhite2' style='width:100px;text-align:center' align='center'>金额</td></tr>" );
		
		int num=0;
		int payCountTotal=0;
		double payMoneyTotal=0;
		double targetMoneyTotal=0;
		int achievementCountTotal=0;
		double achievementMoneyTotal=0;
		int lastAchievementCountTotal=0;
		double lastAchievementMoneyTotal=0;
		double nextDayPayMoneyTotal=0;
		int hasApproveCountTotal=0;
		double hasApproveAmountTotal=0;
		int pendingApproveCountTotal=0;
		int approveCountTotal=0;
		int auditCountTotal=0;
		int infoCountTotal=0;
		double infoAmountTotal=0;
		int hasAccessCountTotal=0;
		double hasAccessAmountTotal=0;
		int auditCount1Total=0;
		double auditAmount1Total=0;
		int approveCount1Total=0;
		double approveAmount1Total=0;
		DecimalFormat dfMoney=new DecimalFormat("#,##0.00");
		DecimalFormat dfPer=new DecimalFormat("##0.00");
		for(int i=0;i<resultList.size();i++) {
			num++;
			payCountTotal=payCountTotal+resultList.get(i).getPayCount();
			payMoneyTotal=payMoneyTotal+resultList.get(i).getPayMoney();
			targetMoneyTotal=targetMoneyTotal+resultList.get(i).getTargetMoney();
			achievementCountTotal=achievementCountTotal+resultList.get(i).getAchievementCount();
			achievementMoneyTotal=achievementMoneyTotal+resultList.get(i).getAchievementMoney();
			lastAchievementCountTotal=lastAchievementCountTotal+resultList.get(i).getLastAchievementCount();
			lastAchievementMoneyTotal=lastAchievementMoneyTotal+resultList.get(i).getLastAchievementMoney();
			nextDayPayMoneyTotal=nextDayPayMoneyTotal+resultList.get(i).getNextDayPayMoney();
			hasApproveCountTotal=hasApproveCountTotal+resultList.get(i).getHasApproveCount();
			hasApproveAmountTotal=hasApproveAmountTotal+resultList.get(i).getHasApproveAmount();
			pendingApproveCountTotal=pendingApproveCountTotal+resultList.get(i).getPendingApproveCount();
			approveCountTotal=approveCountTotal+resultList.get(i).getApproveCount();
			auditCountTotal=auditCountTotal+resultList.get(i).getAuditCount();
			infoCountTotal=infoCountTotal+resultList.get(i).getInfoCount();
			infoAmountTotal=infoAmountTotal+resultList.get(i).getInfoAmount();
			hasAccessCountTotal=hasAccessCountTotal+resultList.get(i).getHasAccessCount();
			hasAccessAmountTotal=hasAccessAmountTotal+resultList.get(i).getHasAccessAmount();
			auditCount1Total=auditCount1Total+resultList.get(i).getAuditCount1();
			auditAmount1Total=auditAmount1Total+resultList.get(i).getAuditAmount1();
			approveCount1Total=approveCount1Total+resultList.get(i).getApproveCount1();
			approveAmount1Total=approveAmount1Total+resultList.get(i).getApproveAmount1();
			
			mailContent.append("<tr class='r12'>");
			mailContent.append("<td class=body2 style='text-align:center;'>"+num+"</td>" +
					"<td class=body2 style='text-align:center;'>"+resultList.get(i).getDeptName()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getPayCount()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getPayMoney())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getTargetMoney())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getAchievementCount()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getAchievementMoney())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getAchievement()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getNextDayPayMoney())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getInfoCount()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getInfoAmount())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getHasAccessCount()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getHasAccessAmount())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getAuditCount1()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getAuditAmount1())+"</td>" +
					"<td class=body2 style='text-align:right;'>"+resultList.get(i).getApproveCount1()+"</td>" +
					"<td class=body2 style='text-align:right;'>"+dfMoney.format(resultList.get(i).getApproveAmount1())+"</td>" );
			mailContent.append("</tr>");
		}
		mailContent.append("<tr class='rhead'>" +
				"<td class='Body2BoldWhite2' style='text-align:center;font-size:9pt;' colspan='2'>总计</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+payCountTotal+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(payMoneyTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(targetMoneyTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+achievementCountTotal+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(achievementMoneyTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfPer.format(achievementMoneyTotal/targetMoneyTotal*100)+"%</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(nextDayPayMoneyTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+infoCountTotal+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(infoAmountTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+hasAccessCountTotal+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(hasAccessAmountTotal)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+auditCount1Total+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(auditAmount1Total)+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+approveCount1Total+"</td>" +
				"<td class='Body2BoldWhite2' style='text-align:right;font-size:9pt;'>"+dfMoney.format(approveAmount1Total)+"</td>" +
				"</tr></table><br><br>");
		mailContent.append("<font color='#FF0000'>*</font><font size='2'>当日各区业绩进度一览表不含委贷案件</font>");
		mailContent.append("</body></html>");

		MailSettingTo mailSettingTo=new MailSettingTo();
		mailSettingTo.setEmailSubject("当日各办事处业绩进度一览表(乘用车)");
		mailSettingTo.setEmailContent(mailContent.toString());
		this.mailUtilService.sendMail(2010,mailSettingTo);
	}
	
	public void query(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map> deptList=null;
		List<AchievementTo> resultList=null;
		AchievementTo loanMoneyToday=null;
		AchievementTo loanMoneyMonth=null;
		
		String date=(String)context.contextMap.get("DATE");
		
		if(date==null||"".equals(date)) {
			Calendar cal=Calendar.getInstance();
			String [] time=DateUtil.dateToString(cal.getTime(),"HH:mm:SS").split(":");
			if(Integer.valueOf(time[0])>=17&&Integer.valueOf(time[1])>30) {
				//如果时间下午5点30以后,默认取今天的时间
			} else {
				cal.add(Calendar.DATE,-1);
			}
			date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			outputMap.put("month",DateUtil.dateToString(cal.getTime(),"MM"));
		} else {
			outputMap.put("month",date.substring(5,7));
		}
		
		
		try {
			context.contextMap.put("decp_id", "2");//2代表的是拿分公司
			deptList=this.achievementReportService.getDeptList(context);
			
			String sortType=(String)context.contextMap.get("SORT_TYPE");
			if(sortType==null||"".equals(sortType)) {
				context.contextMap.put("SORT_TYPE","ACHIEVEMENT_MONEY/TARGET_MONEY*1000");
				context.contextMap.put("SORT","DESC");
			}
			context.contextMap.put("DATE",date);
			resultList=this.achievementCaseService.query(context);
			
			loanMoneyToday=this.achievementCaseService.getLoanMoneyToday(context);
			loanMoneyMonth=this.achievementCaseService.getLoanMoneyMonth(context);
		} catch(Exception e) {
			context.errList.add("客户案况汇总表出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(context.errList.isEmpty()) {
			outputMap.put("DATE",date);
			outputMap.put("SORT",context.contextMap.get("SORT"));
			outputMap.put("SORT_TYPE",context.contextMap.get("SORT_TYPE"));
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("deptList",deptList);
			outputMap.put("resultList",resultList);
			outputMap.put("loanMoneyToday",loanMoneyToday);
			outputMap.put("loanMoneyMonth",loanMoneyMonth);
			Output.jspOutput(outputMap,context,"/achievementCase/achievementCase.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	

	public void  getAchievementCaseReport(Context context) throws Exception{
		
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) +1;
		
		Integer searchYear = currentYear;
		Integer searchMonth = currentMonth;
		String year = (String) context.contextMap.get("year");
		if (!StringUtils.isEmpty(year)) {
			try {
				searchYear = Integer.parseInt(year);
			} catch (NumberFormatException e) {
				searchYear = currentYear;
			}
		}else{
			 if("".equals(year)){
				 searchYear = null;
			 }
		}
		
		String month = (String) context.contextMap.get("month");
		if (!StringUtils.isEmpty(month)) {
			try {
				searchMonth = Integer.parseInt(month);
			} catch (NumberFormatException e) {
				searchMonth = currentMonth;
			}
		}else{
			if("".equals(month)){
				searchMonth = null;
			}
		}
		
		String credit_special_code = (String) context.contextMap.get("code");
		if(credit_special_code == null){//专案全选
			credit_special_code = "null";
		}
		

		//获得办事处
		//Context context = new Context(null,null,null);
		context.contextMap.put("decp_id", "2");//2代表的是拿分公司
		List<Map> deptList=this.achievementReportService.getDeptList(context);
		
		List<Map<String,String>> codeList = achievementCaseService.queryCreditSpecialCode();
		
		//无专案不在数据库中
		Map<String,String> noneCode = new HashMap<String,String>();
		noneCode.put("CREDIT_SPECIAL_NAME", "无专案");
		noneCode.put("CREDIT_SPECIAL_CODE", "");
		codeList.add(noneCode);
		
		Map<String,Object> outputMap = new HashMap<String,Object>();
		

		
		if(deptList!=null && deptList.size()>0){
			List<Map> resultList = new ArrayList<Map>();
			ReportDateTo reportDate = null;
			if(searchYear != null){
				if(searchMonth != null){
					reportDate = ReportDateUtil.getDateByYearAndMonth(searchYear, searchMonth);
				}else{				
					reportDate = ReportDateUtil.getDateByYear(searchYear);
				}
			}else{
				reportDate = new ReportDateTo();
			}

			List<AchievementTo> achievementlist = achievementCaseService.queryAchievementCase(reportDate.getBeginTime(),reportDate.getEndTime());
			
			for(Map decp:deptList){
				Map result = new HashMap();
				result.put("decp_name", decp.get("DECP_NAME_CN"));
				
				int decp_id = (Integer) decp.get("DECP_ID");
				int pay_count = 0;
				BigDecimal pay_money = new BigDecimal(0.00);
				for(AchievementTo achievementTo:achievementlist){
					if(String.valueOf(decp_id).equals(achievementTo.getDeptId())){					
					
						result.put(achievementTo.getCreditSpecialCode()!=null?achievementTo.getCreditSpecialCode()+"_pay_count":"_pay_count", achievementTo.getPayCount());
						result.put(achievementTo.getCreditSpecialCode()!=null?achievementTo.getCreditSpecialCode()+"_pay_money":"_pay_money", new BigDecimal(achievementTo.getPayMoney()));
						pay_count += achievementTo.getPayCount();
						pay_money = pay_money.add(new BigDecimal(achievementTo.getPayMoney()));
					}
				}				
				result.put("pay_count", pay_count);
				result.put("pay_money", pay_money);		
				resultList.add(result);
			}
			//计算合计一栏数据
			Map total = new HashMap();
			total.put("decp_name", "合计");
			int flag = 0 ;
			int total_pay_count = 0;
			BigDecimal total_pay_money = new BigDecimal(0.00);
			for(Map<String,String> code:codeList){
				int pay_count = 0;
				BigDecimal pay_money = new BigDecimal(0.00);
				
				String pay_count_key  = code.get("CREDIT_SPECIAL_CODE") + "_pay_count";
				String pay_money_key  = code.get("CREDIT_SPECIAL_CODE") + "_pay_money";

				for(Map result:resultList){
					if(result.get(pay_count_key)!=null){
						pay_count += (Integer)result.get(pay_count_key);
					}
					if(result.get(pay_money_key)!=null){
						pay_money = pay_money.add((BigDecimal)result.get(pay_money_key));
					}
					
					
					if(flag==0){
						total_pay_count += (Integer)result.get("pay_count");
						total_pay_money = total_pay_money.add((BigDecimal)result.get("pay_money"));
					}
				}
				flag++;
				total.put(pay_count_key, pay_count);
				total.put(pay_money_key, pay_money);
			}
			total.put("pay_count", total_pay_count);
			total.put("pay_money", total_pay_money);	
			resultList.add(total);
			outputMap.put("resultList",resultList);
		}
		List<Integer> yearList = (List<Integer>)DataAccessor.query("report.getYearList",null,RS_TYPE.LIST);
		
		outputMap.put("yearList",yearList);
		outputMap.put("year", searchYear);
		outputMap.put("month", searchMonth);
		outputMap.put("code", credit_special_code);
		outputMap.put("codeList", codeList);
		
		if("null".equalsIgnoreCase(credit_special_code)){
			outputMap.put("size",codeList.size());
		}else{
			outputMap.put("size",1);
		}
		
		
		Output.jspOutput(outputMap,context,"/achievementCase/achievementCaseReport.jsp");

	}
}
