package com.brick.financialReport.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.financialReport.service.InsuranceFeeRemainderReportService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class InsuranceFeeRemainderReportCommand extends BaseCommand {

	private InsuranceFeeRemainderReportService insuranceFeeRemainderReportService;

	public InsuranceFeeRemainderReportService getInsuranceFeeRemainderReportService() {
		return insuranceFeeRemainderReportService;
	}

	public void setInsuranceFeeRemainderReportService(
			InsuranceFeeRemainderReportService insuranceFeeRemainderReportService) {
		this.insuranceFeeRemainderReportService = insuranceFeeRemainderReportService;
	}
	
	public void queryInsuranceFeeRemainderReport(Context context) {
		
		List<String> dateList=null;
		PagingInfo<Object> pagingInfo=null;
		List<Map<String,Object>> resultList=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			dateList=this.insuranceFeeRemainderReportService.getDateList();
			
			pagingInfo=baseService.queryForListWithPaging("financialReport.queryInsuranceFeeRemainderReport",context.contextMap,"START_DATE");
		
			if("9".equals(context.contextMap.get("JOB"))) {
				resultList=(List<Map<String,Object>>)DataAccessor.query("financialReport.queryInsuranceFeeRemainderReportForIT",null,RS_TYPE.LIST);
			}
		} catch (Exception e) {
			
		}
		
		if(context.contextMap.get("date")==null) {
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			context.contextMap.put("date",DateUtil.dateToString(cal.getTime(),"yyyy-MM"));
		}
		outputMap.put("job",context.contextMap.get("JOB"));
		outputMap.put("date",context.contextMap.get("date"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("payDate",context.contextMap.get("payDate"));
		outputMap.put("recpStatus",context.contextMap.get("recpStatus"));
		outputMap.put("dateList",dateList);
		outputMap.put("pagingInfo",pagingInfo);
		outputMap.put("resultList",resultList);
		
		Output.jspOutput(outputMap,context,"/financialReport/insuranceFeeRemainderReport.jsp");
	}
	
	public static List<Map<String,Object>> exportInsuranceFeeRemainderReport(String date,String content) {
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("date",date);
		param.put("content",content);
		
		List<Map<String,Object>> resultList=null;
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("financialReport.queryInsuranceFeeRemainderReport",param,RS_TYPE.LIST);
		} catch (Exception e) {
			
		}
		return resultList;
	}
	
	public void getInsuranceFeeRemainderReportByRecpCode(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=null;
		List<Map<String,Object>> reportList=new ArrayList<Map<String,Object>>();
		
		try {
			resultList=this.insuranceFeeRemainderReportService.getCurrentInsuranceFeeRemainderPayByRecpCode(context);
		} catch (Exception e) {
			
		}
		for(int i=0;resultList!=null&&i<resultList.size();i++) {
			Map<String,Object> reportMap=new HashMap<String,Object>();
			reportMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
			reportMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
			reportMap.put("RECP_CODE",resultList.get(i).get("RECP_CODE"));
			if(i==0) {
				reportMap.put("BEGIN_MONEY",0);
				reportMap.put("INCREASE_MONEY",resultList.get(i).get("INSURE"));
				reportMap.put("REDUCE_MONEY",resultList.get(i).get("MONTH_INSURE"));
				reportMap.put("END_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
			} else {
				reportMap.put("BEGIN_MONEY",reportList.get(i-1).get("END_MONEY"));
				reportMap.put("INCREASE_MONEY",0);
				reportMap.put("REDUCE_MONEY",resultList.get(i).get("MONTH_INSURE"));
				reportMap.put("END_MONEY",Double.valueOf(reportMap.get("BEGIN_MONEY").toString())-Double.valueOf(reportMap.get("REDUCE_MONEY").toString()));
			}
			if(i==resultList.size()-1) {
				reportMap.put("isLastRow","Y");
			}
			reportList.add(reportMap);
		}
		
		outputMap.put("reportList",reportList);
		Output.jspOutput(outputMap,context,"/financialReport/insuranceFeeRemainderReportByRecpCode.jsp");
	}
	
	public void generateHistoryInsuranceFeeRemainderFinancial(Context context) {
		
		try {
			this.insuranceFeeRemainderReportService.generateHistoryInsuranceFeeRemainderFinancial(context);
			
			Output.jsonFlageOutput(true,context);
		} catch (Exception e) {
			Output.jsonFlageOutput(false,context);
		}
	}
	
	public void generateHistoryInsuranceFeeRemainderPay(Context context) {
		try {
			this.insuranceFeeRemainderReportService.generateHistoryInsuranceFeeRemainderPay(context);
			
			Output.jsonFlageOutput(true,context);
		} catch (Exception e) {
			Output.jsonFlageOutput(false,context);
		}
	}
}
