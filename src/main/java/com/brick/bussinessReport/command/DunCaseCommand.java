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

import com.brick.base.command.BaseCommand;
import com.brick.bussinessReport.service.DunCaseService;
import com.brick.bussinessReport.to.DunCaseChartTO;
import com.brick.bussinessReport.to.DunCaseDetailTO;
import com.brick.bussinessReport.to.DunCaseTO;
import com.brick.chartDirector.ChartDataSet;
import com.brick.chartDirector.ChartFactory;
import com.brick.chartDirector.ChartInfo;
import com.brick.chartDirector.ChartResult;
import com.brick.deptCmpy.to.DeptCmpyTO;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DateUtil;
import com.brick.util.DeptMapListener;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;

public class DunCaseCommand extends BaseCommand {
	
	Log logger=LogFactory.getLog(DunCaseCommand.class);
	
	private DunCaseService dunCaseService;

	public DunCaseService getDunCaseService() {
		return dunCaseService;
	}

	public void setDunCaseService(DunCaseService dunCaseService) {
		this.dunCaseService = dunCaseService;
	}
	
	public void query(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" 开始.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<DunCaseTO> resultList=null;
		try {
			resultList=this.dunCaseService.getDunCaseDetail(context.contextMap);
		} catch (Exception e) {
			context.errList.add("查看详细逾期报表出错,请联系管理员!");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		outputMap.put("resultList",resultList);
		
		if(context.errList.isEmpty()) {
			Output.jspOutput(outputMap,context,"/report/dunInformationDetail.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" 结束.....");
		}
	}
	
	public void queryDunCase(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......查询逾期报表(按金额分组)";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" 开始.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<DunCaseTO> resultList=null;
		
		if(context.contextMap.get("DATE")==null||"".equals(context.contextMap.get("DATE"))) {
			Calendar cal=Calendar.getInstance();
			//cal.add(Calendar.DATE,-1);
			cal.set(2013, 11, 31);
			context.contextMap.put("DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
		}
		
		try {
			resultList=this.dunCaseService.queryDunCaseByMoney(context.contextMap);
		} catch (Exception e) {
			context.errList.add("查询逾期报表(按金额分组)出错!");
			LogPrint.getLogStackTrace(e,logger);
			Output.errorPageOutput(e,context);
			return;
		}
		
		outputMap.put("resultList",resultList);
		outputMap.put("DATE",context.contextMap.get("DATE"));
		
		Output.jspOutput(outputMap,context,"/report/dunInformationByMoney.jsp");
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" 结束.....");
		}
	}
	
	/**
	 * 逾期状况统计表，一级查询数据
	 * @param context
	 */
	public void queryDunCaseReport(Context context){
		String byType = context.contextMap.get("byType").toString();
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......查询逾期报表(按" + byType + "分组)";
		List<Map<String, Object>> areaList = null;
		if(logger.isDebugEnabled()) {
			logger.debug(log+" 开始.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<DunCaseDetailTO> resultList=null;
		
		if(context.contextMap.get("DATE")==null||"".equals(context.contextMap.get("DATE"))) {
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			context.contextMap.put("DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
		}
		
		String queryType=(String) context.contextMap.get("QUERY_TYPE");
		if(queryType == null || "".equals(queryType)){
			//0代表全部
			queryType="0";
		}else if(("1").equals(queryType)){
			//1代表查询重车
			context.contextMap.put("TYPE1", "商用车");
			context.contextMap.put("TYPE2", "重车");
		}else if(("2").equals(queryType)){
			//2代表查询设备
			context.contextMap.put("TYPE1", "设备");
			context.contextMap.put("TYPE2", "设备");
		}else if(("3").equals(queryType)){
			//3代表查询乘用车
			context.contextMap.put("TYPE1", "乘用车");
			context.contextMap.put("TYPE2", "乘用车");
		}
		
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("dataType", "设备业务区域");
		try {
			areaList=(List<Map<String, Object>>) DataAccessor.query("dataDictionary.queryDataDictionary", paramMap, DataAccessor.RS_TYPE.LIST);
			
			if(StringUtils.isEmpty(context.contextMap.get("area"))) {
				
			} else {
				String deptStr=DeptMapListener.departmentMap.get(context.contextMap.get("area")).toString();

				int loop=deptStr.substring(1,deptStr.length()-1).split(",").length;
				StringBuffer param=new StringBuffer();
				for(int i=0;i<loop;i++) {
					if(i==loop-1) {
						param.append("'"+deptStr.substring(1,deptStr.length()-1).split(",")[i].trim()+"'");
					} else {
						param.append("'"+deptStr.substring(1,deptStr.length()-1).split(",")[i].trim()+"',");
					}
				}
				
				paramMap.put("areaList",param.toString());
				List<Map<String,Object>> cmpyList=(List<Map<String,Object>>)DataAccessor.query("report.getAreaCmpy",paramMap,DataAccessor.RS_TYPE.LIST);

				param=new StringBuffer();
				for(int i=0;i<cmpyList.size();i++) {
					if(i==cmpyList.size()-1) {
						param.append("'"+cmpyList.get(i).get("DECP_NAME_CN")+"'");
					} else {
						param.append("'"+cmpyList.get(i).get("DECP_NAME_CN")+"',");
					}
				}
				context.contextMap.put("cmpyList",param.toString());
			}
		} catch (Exception e1) {
		}
		outputMap.put("areaList", areaList);
		
		String returnUrl = "";
		try {
			if(byType.equals("dept")){
				//办事处
				resultList=this.dunCaseService.queryDunCase(context.contextMap);
				returnUrl = "/report/dunCaseReportByDept.jsp";
			} else if(byType.equals("special")){
				//专案
				resultList=this.dunCaseService.queryDunCase(context.contextMap);
				returnUrl = "/report/dunCaseReportBySpecial.jsp";
			} else if(byType.equals("orgUpUser")){
				//原始主管
				resultList=this.dunCaseService.queryDunCase(context.contextMap);
				returnUrl = "/report/dunCaseReportByOrgUpUser.jsp";
			} else if(byType.equals("orgUser")){
				//原始经办人
				resultList=this.dunCaseService.queryDunCase(context.contextMap);
				returnUrl = "/report/dunCaseReportByOrgUser.jsp";
			} else if(byType.equals("price")){
				//金额
				context.contextMap.put("money_text1",Constants._50);
				context.contextMap.put("money_text2",Constants._50_100);
				context.contextMap.put("money_text3",Constants._100_200);
				context.contextMap.put("money_text4",Constants._200_300);
				context.contextMap.put("money_text5",Constants._300);
				context.contextMap.put("money1",Constants.$50);
				context.contextMap.put("money2",Constants.$100);
				context.contextMap.put("money3",Constants.$200);
				context.contextMap.put("money4",Constants.$300);
//				resultList=this.dunCaseService.queryDunCaseByPrice(context.contextMap);
				//一级查询+二级查询金额 与 一级查询金额 方法通用
				resultList=this.dunCaseService.queryDunCaseDetailByPrimaryAndPrice(context.contextMap);
				returnUrl = "/report/dunCaseReportByPrice.jsp";
			} else if(byType.equals("approvalUser")){
				//核准人
				resultList=this.dunCaseService.queryDunCase(context.contextMap);
				returnUrl = "/report/dunCaseReportByApprovalUser.jsp";
			}
		} catch (Exception e) {
			context.errList.add("查询逾期报表(按" + byType + "分组)出错!");
			LogPrint.getLogStackTrace(e,logger);
			Output.errorPageOutput(e,context);
			return;
		}
		outputMap.put("area", context.contextMap.get("area"));
		outputMap.put("QUERY_TYPE", queryType);
		outputMap.put("resultList",resultList);
		outputMap.put("DATE",context.contextMap.get("DATE"));
		
		Output.jspOutput(outputMap,context,returnUrl);
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" 结束.....");
		}
	}
	
	/**
	 * 逾期状况统计表,二级查询数据
	 * @param context
	 */
	public void queryDunCaseReportDetail(Context context) {
		String primaryType = context.contextMap.get("byType").toString();
		String secondaryType = context.contextMap.get("type").toString();
		Map<String, String> types = new HashMap<String, String>();
		types.put("special", "专案名");
		types.put("orgUpUser", "原始主管");
		types.put("orgUser", "原始经办人");
		types.put("dept", "办事处");
		types.put("price", "金额");
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......查询逾期报表(按" + types.get(primaryType) + "+" + types.get(secondaryType) + "分组)";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" 开始.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<DunCaseDetailTO> resultList=null;
		String search_dun_day = (String) context.contextMap.get("DATE");
		context.contextMap.put("money_text1",Constants._50);
		context.contextMap.put("money_text2",Constants._50_100);
		context.contextMap.put("money_text3",Constants._100_200);
		context.contextMap.put("money_text4",Constants._200_300);
		context.contextMap.put("money_text5",Constants._300);
		context.contextMap.put("money1",Constants.$50);
		context.contextMap.put("money2",Constants.$100);
		context.contextMap.put("money3",Constants.$200);
		context.contextMap.put("money4",Constants.$300);

		try {
			if (search_dun_day == null || "".equals(search_dun_day)) {
				resultList = new ArrayList<DunCaseDetailTO>();
			} else {
				//一级查询金额+二级查询
				if(primaryType.equals("price")){
					if (context.contextMap.get("moneyType") == null || "".equals(context.contextMap.get("moneyType"))) {
						resultList = new ArrayList<DunCaseDetailTO>();
					} else {
						String moneyType = context.contextMap.get("moneyType").toString();
						if (moneyType.equals(Constants._50)) {
							context.contextMap.put("priceEnd",Constants.$50);
						} else if (moneyType.equals(Constants._50_100)) {
							context.contextMap.put("priceBegin",Constants.$50);
							context.contextMap.put("priceEnd",Constants.$100);
						} else if (moneyType.equals(Constants._100_200)) {
							context.contextMap.put("priceBegin",Constants.$100);
							context.contextMap.put("priceEnd",Constants.$200);
						} else if (moneyType.equals(Constants._200_300)) {
							context.contextMap.put("priceBegin",Constants.$200);
							context.contextMap.put("priceEnd",Constants.$300);
						} else if (moneyType.equals(Constants._300)) {
							context.contextMap.put("priceBegin",Constants.$300);
						}
						resultList=this.dunCaseService.queryDunCaseDetailByPriceAndSecondary(context.contextMap);
					}
				} else {
					if ((primaryType.equals("special") && (context.contextMap.get("CREDIT_SPECIAL_CODE") == null || "".equals(context.contextMap.get("CREDIT_SPECIAL_CODE"))))
							|| (primaryType.equals("dept") && (context.contextMap.get("DECPNAME") == null || "".equals(context.contextMap.get("DECPNAME"))))
							|| (primaryType.equals("orgUpUser") && (context.contextMap.get("upUserId") == null || "".equals(context.contextMap.get("upUserId"))))
							|| (primaryType.equals("orgUser") && (context.contextMap.get("orgUserId") == null || "".equals(context.contextMap.get("orgUserId"))))
							|| (primaryType.equals("approvalUser") && (context.contextMap.get("approvalUserId") == null || "".equals(context.contextMap.get("approvalUserId"))))) {
						resultList = new ArrayList<DunCaseDetailTO>();
					}
					if(secondaryType.equals("price")){
						//一级查询+二级查询金额
						resultList=this.dunCaseService.queryDunCaseDetailByPrimaryAndPrice(context.contextMap);
					} else {
						//一级查询（非金额）+二级查询（非金额）
						resultList=this.dunCaseService.queryDunCaseDetail(context.contextMap);
					}
				}
			}
		}catch (Exception e) {
			context.errList.add("查询逾期报表(按" + types.get(primaryType) + "+" + types.get(secondaryType) + "分组)出错!");
			LogPrint.getLogStackTrace(e,logger);
			Output.errorPageOutput(e,context);
			return;
		}
		outputMap.put("urlParams",context.contextMap.get("urlParams"));
		outputMap.put("resultList",resultList);
		outputMap.put("DATE",context.contextMap.get("DATE"));
		outputMap.put("type",context.contextMap.get("type"));
		outputMap.put("money_50",Constants._50);
		outputMap.put("money_50_100",Constants._50_100);
		outputMap.put("money_100_200",Constants._100_200);
		outputMap.put("money_200_300",Constants._200_300);
		outputMap.put("money_300",Constants._300);
		
		Output.jspOutput(outputMap,context,"/report/dunCaseReportDetail.jsp?type=" + secondaryType);
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" 结束.....");
		}
	}
	 
	public void queryRentMoney(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......查询租金余额";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" 开始.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<DunCaseTO> resultList=null;
		
		if(context.contextMap.get("DATE")==null||"".equals(context.contextMap.get("DATE"))) {
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			context.contextMap.put("DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
		}
		
		try {
			resultList=this.dunCaseService.queryRentMoney(context.contextMap);
		} catch (Exception e) {
			context.errList.add("查询租金余额出错!");
			LogPrint.getLogStackTrace(e,logger);
			Output.errorPageOutput(e,context);
			return;
		}
		
		outputMap.put("resultList",resultList);
		outputMap.put("DATE",context.contextMap.get("DATE"));
		
		Output.jspOutput(outputMap,context,"/report/rentMoney.jsp");
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" 结束.....");
		}
	}
	
	public void dunCaseChart(Context context) {
		
		Calendar cal=Calendar.getInstance();
		Map<String,Object> outputMap=new HashMap<String,Object>();
		String fromDate=null;
		String toDate=null;
		List<DunCaseChartTO> resultList=null;
		ChartResult chartResult1=null;
		ChartResult chartResult2=null;
		List<DeptCmpyTO> cmpyList=null;
		List<String> weekList=new ArrayList<String>();
		
		String title="";
		
		if(context.contextMap.get("DEPT_NAME")==null) {
			context.contextMap.put("DEPT_NAME","ALL");
		}
		if("ALL".equals(context.contextMap.get("DEPT_NAME"))) {
			title="全公司";
		} else {
			title=(String)context.contextMap.get("DEPT_NAME");
		}
		try {
			
			cmpyList=this.dunCaseService.getCompanyList();
			
			if(context.contextMap.get("fromDate")==null&&context.contextMap.get("toDate")==null) {//默认进入页面
				 
				//默认条件天,取系统时间所在周的周日为From Date
				/*cal.set(Calendar.DAY_OF_WEEK,1);
				fromDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				//如果是选择天的话,取系统时间所在周的周六为To Date
				long f=cal.getTimeInMillis();
				cal.set(Calendar.DAY_OF_WEEK,7);
				toDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				long t=cal.getTimeInMillis();*/
				
				//如果是选择天的话,取系统时间所在周的周日为From Date
				cal.add(Calendar.MONTH,-6);
				cal.set(Calendar.DAY_OF_WEEK,6);
				fromDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				long f=cal.getTimeInMillis();
				//如果是选择天的话,取系统时间所在周的周六为To Date
				cal.add(Calendar.MONTH,6);
				cal.set(Calendar.DAY_OF_WEEK,6);
				toDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				long t=cal.getTimeInMillis();
				
				long loop=((t-f)/(1000*60*60*24)+1)/7;
				
				cal.setTime(DateUtil.strToDate(fromDate,"yyyy-MM-dd"));
				for(int i=0;i<loop;i++) {
					cal.set(Calendar.DAY_OF_WEEK,6);
					weekList.add(DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
					cal.add(Calendar.DATE,7);
				}
				
				context.contextMap.put("fromDate",fromDate);
				context.contextMap.put("toDate",toDate);
				resultList=this.dunCaseService.getWeekChart(context.contextMap);
				
				/*cal.set(Calendar.DAY_OF_WEEK,1);
				dayList.add(fromDate);
				for(int i=0;i<7;i++) {
					cal.add(Calendar.DATE,1);
					dayList.add(DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
				}*/
				
				//生成折线统计图
				
				ChartInfo chartInfo=new ChartInfo();
				ChartInfo chartInfo1=new ChartInfo();
				
				chartInfo.chartHeight=350;
				chartInfo.xLableFontAngle=0;
				chartInfo.setChartTitle(title+"31,91,181天以上逾期金额比统计图");
				chartInfo1.chartHeight=350;
				chartInfo1.xLableFontAngle=0;
				chartInfo1.setChartTitle(title+"31,91,181天以上逾期件数比统计图");
				
				chartInfo.setyTitle("金额比");
				chartInfo.setChartName("money");
				chartInfo1.setyTitle("件数比");
				chartInfo.setChartName("count");
				
				Calendar compareDay=Calendar.getInstance();
				compareDay.add(Calendar.DATE,-1);
				List<String> x=new ArrayList<String>();
				for(int i=0;i<weekList.size();i++) {
					if(DateUtil.strToDate(weekList.get(i),"yyyy-MM-dd").compareTo(compareDay.getTime())==-1) {
						x.add(weekList.get(i));
					}
				}
				
				chartInfo.chartWidth=x.size()<=14?1024:(x.size()/14+1)*400;
				chartInfo1.chartWidth=x.size()<=14?1024:(x.size()/14+1)*400;
				
				chartInfo.setxLabel(x.toArray(new String [0]));
				chartInfo1.setxLabel(x.toArray(new String [0]));
				
				List<ChartDataSet> chartDataSet=new ArrayList<ChartDataSet>();
				ChartDataSet dataSet=null;
				List<Double> yData=null;
				
				List<ChartDataSet> chartDataSet1=new ArrayList<ChartDataSet>();
				ChartDataSet dataSet1=null;
				List<Double> yData1=null;
				/*for(int ii=0;ii<cmpyList.size();ii++) {
					if(!"ALL".equals(cmpyList.get(ii).getCompanyName())) {
						continue;
					}*/
				for(int ii=0;ii<3;ii++) {
					dataSet=new ChartDataSet();
					yData=new ArrayList<Double>();
					
					dataSet1=new ChartDataSet();
					yData1=new ArrayList<Double>();
					if(ii==0) {
						dataSet.setTitle("31天以上");
						
						dataSet1.setTitle("31天以上");
						for(int j=0;j<x.size();j++) {
							boolean flag=true;
							for(int i=0;i<resultList.size();i++) {
								if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
									yData.add(Double.valueOf(resultList.get(i).getPer31_money()));
									yData1.add(Double.valueOf(resultList.get(i).getPer31_count()));
									flag=false;
									break;
								}
							}
							if(flag) {
								yData.add(0.0);
								yData1.add(0.0);
							}
						}
					} else if(ii==1) {
						dataSet.setTitle("91天以上");
						dataSet1.setTitle("91天以上");
						for(int j=0;j<x.size();j++) {
							boolean flag=true;
							for(int i=0;i<resultList.size();i++) {
								if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
									yData.add(Double.valueOf(resultList.get(i).getPer91_money()));
									yData1.add(Double.valueOf(resultList.get(i).getPer91_count()));
									flag=false;
									break;
								}
							}
							if(flag) {
								yData.add(0.0);
								yData1.add(0.0);
							}
						}
					} else {
						dataSet.setTitle("181天以上");
						dataSet1.setTitle("181天以上");
						for(int j=0;j<x.size();j++) {
							boolean flag=true;
							for(int i=0;i<resultList.size();i++) {
								if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
									yData.add(Double.valueOf(resultList.get(i).getPer181_money()));
									yData1.add(Double.valueOf(resultList.get(i).getPer181_count()));
									flag=false;
									break;
								}
							}
							if(flag) {
								yData.add(0.0);
								yData1.add(0.0);
							}
						}
					}
					dataSet.setyData(yData);
					chartDataSet.add(dataSet);
					
					dataSet1.setyData(yData1);
					chartDataSet1.add(dataSet1);
				/*}*/
				}
				chartInfo.setChartDataList(chartDataSet);
				chartInfo.setAlertTitle("{xLabel}日的比例是{value}%");
				
				chartInfo1.setChartDataList(chartDataSet1);
				chartInfo1.setAlertTitle("{xLabel}日的比例是{value}%");
				
				chartResult1=ChartFactory.getLineChart(context.getRequest(),chartInfo);
				chartResult2=ChartFactory.getLineChart(context.getRequest(),chartInfo1);
			} else {//点击查询进入页面
				if("day".equals(context.contextMap.get("dateFormat"))) {
					
					resultList=this.dunCaseService.getDayChart(context.contextMap);
					
					//生成折线统计图
					
					ChartInfo chartInfo=new ChartInfo();
					ChartInfo chartInfo1=new ChartInfo();
					
					chartInfo.chartHeight=350;
					chartInfo.xLableFontAngle=0;
					chartInfo.setChartTitle(title+"31,91,181天以上逾期金额比统计图");
					chartInfo1.chartHeight=350;
					chartInfo1.xLableFontAngle=0;
					chartInfo1.setChartTitle(title+"31,91,181天以上逾期件数比统计图");
					
					chartInfo.setyTitle("金额比");
					chartInfo.setChartName("money");
					chartInfo1.setyTitle("件数比");
					chartInfo.setChartName("count");
					
					String [] dayList=HTMLUtil.getParameterValues(context.request,"dayList","");
					Calendar compareDay=Calendar.getInstance();
					compareDay.add(Calendar.DATE,-1);
					List<String> x=new ArrayList<String>();
					for(int i=0;i<dayList.length;i++) {
						if(DateUtil.strToDate(dayList[i],"yyyy-MM-dd").compareTo(compareDay.getTime())==-1) {
							x.add(dayList[i]);
						}
					}
					
					if(x.size()==1) {
						outputMap.put("deptName",context.contextMap.get("DEPT_NAME"));
						outputMap.put("cmpyList",cmpyList);
						outputMap.put("chartResult1",chartResult1);
						outputMap.put("chartResult2",chartResult2);
						outputMap.put("dateFormat",context.contextMap.get("dateFormat"));
						outputMap.put("fromDate",context.contextMap.get("fromDate"));
						outputMap.put("toDate",context.contextMap.get("toDate"));
						outputMap.put("dayList",HTMLUtil.getParameterValues(context.request,"dayList",""));
						outputMap.put("weekList",HTMLUtil.getParameterValues(context.request,"weekList",""));
						outputMap.put("monthList",HTMLUtil.getParameterValues(context.request,"monthList",""));
						Output.jspOutput(outputMap,context,"/dun/dunCaseChart.jsp");
						return;
					}
					chartInfo.chartWidth=x.size()<=14?1024:(x.size()/14+1)*400;
					chartInfo1.chartWidth=x.size()<=14?1024:(x.size()/14+1)*400;
					
					chartInfo.setxLabel(x.toArray(new String [0]));
					chartInfo1.setxLabel(x.toArray(new String [0]));
					
					List<ChartDataSet> chartDataSet=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet=null;
					List<Double> yData=null;
					
					List<ChartDataSet> chartDataSet1=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet1=null;
					List<Double> yData1=null;
					/*for(int ii=0;ii<cmpyList.size();ii++) {
						if(!"ALL".equals(cmpyList.get(ii).getCompanyName())) {
							continue;
						}*/
					for(int ii=0;ii<3;ii++) {
						dataSet=new ChartDataSet();
						yData=new ArrayList<Double>();
						
						dataSet1=new ChartDataSet();
						yData1=new ArrayList<Double>();
						if(ii==0) {
							dataSet.setTitle("31天以上");
							
							dataSet1.setTitle("31天以上");
							for(int j=0;j<x.size();j++) {
								boolean flag=true;
								for(int i=0;i<resultList.size();i++) {
									if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
										yData.add(Double.valueOf(resultList.get(i).getPer31_money()));
										yData1.add(Double.valueOf(resultList.get(i).getPer31_count()));
										flag=false;
										break;
									}
								}
								if(flag) {
									yData.add(0.0);
									yData1.add(0.0);
								}
							}
						} else if(ii==1) {
							dataSet.setTitle("91天以上");
							dataSet1.setTitle("91天以上");
							for(int j=0;j<x.size();j++) {
								boolean flag=true;
								for(int i=0;i<resultList.size();i++) {
									if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
										yData.add(Double.valueOf(resultList.get(i).getPer91_money()));
										yData1.add(Double.valueOf(resultList.get(i).getPer91_count()));
										flag=false;
										break;
									}
								}
								if(flag) {
									yData.add(0.0);
									yData1.add(0.0);
								}
							}
						} else {
							dataSet.setTitle("181天以上");
							dataSet1.setTitle("181天以上");
							for(int j=0;j<x.size();j++) {
								boolean flag=true;
								for(int i=0;i<resultList.size();i++) {
									if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
										yData.add(Double.valueOf(resultList.get(i).getPer181_money()));
										yData1.add(Double.valueOf(resultList.get(i).getPer181_count()));
										flag=false;
										break;
									}
								}
								if(flag) {
									yData.add(0.0);
									yData1.add(0.0);
								}
							}
						}
						dataSet.setyData(yData);
						chartDataSet.add(dataSet);
						
						dataSet1.setyData(yData1);
						chartDataSet1.add(dataSet1);
					/*}*/
					}
					chartInfo.setChartDataList(chartDataSet);
					chartInfo.setAlertTitle("{xLabel}日的比例是{value}%");
					
					chartInfo1.setChartDataList(chartDataSet1);
					chartInfo1.setAlertTitle("{xLabel}日的比例是{value}%");
					
					chartResult1=ChartFactory.getLineChart(context.getRequest(),chartInfo);
					chartResult2=ChartFactory.getLineChart(context.getRequest(),chartInfo1);
				} else if("week".equals(context.contextMap.get("dateFormat"))) {
					
					resultList=this.dunCaseService.getWeekChart(context.contextMap);
					
					//生成折线统计图
					
					ChartInfo chartInfo=new ChartInfo();
					ChartInfo chartInfo1=new ChartInfo();
					
					chartInfo.chartHeight=350;
					chartInfo.xLableFontAngle=0;
					chartInfo.setChartTitle(title+"31,91,181天以上逾期金额比统计图");
					
					chartInfo1.chartHeight=350;
					chartInfo1.xLableFontAngle=0;
					chartInfo1.setChartTitle(title+"31,91,181天以上逾期件数比统计图");
					
					chartInfo.setyTitle("金额比");
					chartInfo.setChartName("money");
					
					chartInfo1.setyTitle("件数比");
					chartInfo1.setChartName("count");
					
					String [] wkList=HTMLUtil.getParameterValues(context.request,"weekList","");
					Calendar compareDay=Calendar.getInstance();
					compareDay.add(Calendar.DATE,-1);
					List<String> x=new ArrayList<String>();
					for(int i=0;i<wkList.length;i++) {
						if(DateUtil.strToDate(wkList[i],"yyyy-MM-dd").compareTo(compareDay.getTime())==-1) {
							x.add(wkList[i]);
						}
					}
					
					if(x.size()==1||x.size()==0) {
						outputMap.put("deptName",context.contextMap.get("DEPT_NAME"));
						outputMap.put("cmpyList",cmpyList);
						outputMap.put("chartResult1",chartResult1);
						outputMap.put("chartResult2",chartResult2);
						outputMap.put("dateFormat",context.contextMap.get("dateFormat"));
						outputMap.put("fromDate",context.contextMap.get("fromDate"));
						outputMap.put("toDate",context.contextMap.get("toDate"));
						outputMap.put("dayList",HTMLUtil.getParameterValues(context.request,"dayList",""));
						outputMap.put("weekList",HTMLUtil.getParameterValues(context.request,"weekList",""));
						outputMap.put("monthList",HTMLUtil.getParameterValues(context.request,"monthList",""));
						Output.jspOutput(outputMap,context,"/dun/dunCaseChart.jsp");
						return;
					}
					
					chartInfo.chartWidth=x.size()<=14?1024:(x.size()/14+1)*400;
					chartInfo1.chartWidth=x.size()<=14?1024:(x.size()/14+1)*400;
					
					chartInfo.setxLabel(x.toArray(new String [0]));
					chartInfo1.setxLabel(x.toArray(new String [0]));
					
					List<ChartDataSet> chartDataSet=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet=null;
					List<Double> yData=null;
					
					List<ChartDataSet> chartDataSet1=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet1=null;
					List<Double> yData1=null;
					/*for(int ii=0;ii<cmpyList.size();ii++) {
						if(!"ALL".equals(cmpyList.get(ii).getCompanyName())) {
							continue;
						}*/
					for(int ii=0;ii<3;ii++) {
						dataSet=new ChartDataSet();
						yData=new ArrayList<Double>();
						
						dataSet1=new ChartDataSet();
						yData1=new ArrayList<Double>();
						if(ii==0) {
							dataSet.setTitle("31天以上");
							dataSet1.setTitle("31天以上");
							for(int j=0;j<x.size();j++) {
								boolean flag=true;
								for(int i=0;i<resultList.size();i++) {
									if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
										yData.add(Double.valueOf(resultList.get(i).getPer31_money()));
										yData1.add(Double.valueOf(resultList.get(i).getPer31_count()));
										flag=false;
										break;
									}
								}
								if(flag) {
									yData.add(0.0);
									yData1.add(0.0);
								}
							}
						} else if(ii==1) {
							dataSet.setTitle("91天以上");
							dataSet1.setTitle("91天以上");
							for(int j=0;j<x.size();j++) {
								boolean flag=true;
								for(int i=0;i<resultList.size();i++) {
									if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
										yData.add(Double.valueOf(resultList.get(i).getPer91_money()));
										yData1.add(Double.valueOf(resultList.get(i).getPer91_count()));
										flag=false;
										break;
									}
								}
								if(flag) {
									yData.add(0.0);
									yData1.add(0.0);
								}
							}
						} else {
							dataSet.setTitle("181天以上");
							dataSet1.setTitle("181天以上");
							for(int j=0;j<x.size();j++) {
								boolean flag=true;
								for(int i=0;i<resultList.size();i++) {
									if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
										yData.add(Double.valueOf(resultList.get(i).getPer181_money()));
										yData1.add(Double.valueOf(resultList.get(i).getPer181_count()));
										flag=false;
										break;
									}
								}
								if(flag) {
									yData.add(0.0);
									yData1.add(0.0);
								}
							}
						}
						dataSet.setyData(yData);
						chartDataSet.add(dataSet);
						dataSet1.setyData(yData1);
						chartDataSet1.add(dataSet1);
					/*}*/
					}
					chartInfo.setChartDataList(chartDataSet);
					chartInfo.setAlertTitle("{xLabel}日的比例是{value}%");
					chartResult1=ChartFactory.getLineChart(context.getRequest(),chartInfo);
					
					chartInfo1.setChartDataList(chartDataSet1);
					chartInfo1.setAlertTitle("{xLabel}日的比例是{value}%");
					chartResult2=ChartFactory.getLineChart(context.getRequest(),chartInfo1);
				} else if("month".equals(context.contextMap.get("dateFormat"))) {
					
					//生成折线统计图
					
					ChartInfo chartInfo=new ChartInfo();
					ChartInfo chartInfo1=new ChartInfo();
					
					chartInfo.chartHeight=350;
					chartInfo.xLableFontAngle=0;
					chartInfo.setChartTitle(title+"31,91,181天以上逾期金额比统计图");
					
					chartInfo1.chartHeight=350;
					chartInfo1.xLableFontAngle=0;
					chartInfo1.setChartTitle(title+"31,91,181天以上逾期件数比统计图");
					
					chartInfo.setyTitle("金额比");
					chartInfo.setChartName("money");
					
					chartInfo1.setyTitle("件数比");
					chartInfo1.setChartName("count");
					
					String [] monthList=HTMLUtil.getParameterValues(context.request,"monthList","");
					
					if(monthList.length==0) {
						outputMap.put("deptName",context.contextMap.get("DEPT_NAME"));
						outputMap.put("cmpyList",cmpyList);
						outputMap.put("chartResult1",chartResult1);
						outputMap.put("chartResult2",chartResult2);
						outputMap.put("dateFormat",context.contextMap.get("dateFormat"));
						outputMap.put("fromDate",context.contextMap.get("fromDate"));
						outputMap.put("toDate",context.contextMap.get("toDate"));
						outputMap.put("dayList",HTMLUtil.getParameterValues(context.request,"dayList",""));
						outputMap.put("weekList",HTMLUtil.getParameterValues(context.request,"weekList",""));
						outputMap.put("monthList",HTMLUtil.getParameterValues(context.request,"monthList",""));
						Output.jspOutput(outputMap,context,"/dun/dunCaseChart.jsp");
						return;
					}
					StringBuffer param=new StringBuffer();
					Calendar compareDay=Calendar.getInstance();
					compareDay.add(Calendar.DATE,-1);
					List<String> x=new ArrayList<String>();
					for(int i=0;i<monthList.length;i++) {
						if(DateUtil.strToDate(monthList[i],"yyyy-MM-dd").compareTo(compareDay.getTime())==-1) {
							x.add(monthList[i]);
						}
						if(i!=monthList.length-1) {
							param.append("'").append(monthList[i]).append("',");
						} else {
							param.append("'").append(monthList[i]).append("'");
						}
					}
					
					if(x.size()==1||x.size()==0) {
						outputMap.put("deptName",context.contextMap.get("DEPT_NAME"));
						outputMap.put("cmpyList",cmpyList);
						outputMap.put("chartResult1",chartResult1);
						outputMap.put("chartResult2",chartResult2);
						outputMap.put("dateFormat",context.contextMap.get("dateFormat"));
						outputMap.put("fromDate",context.contextMap.get("fromDate"));
						outputMap.put("toDate",context.contextMap.get("toDate"));
						outputMap.put("dayList",HTMLUtil.getParameterValues(context.request,"dayList",""));
						outputMap.put("weekList",HTMLUtil.getParameterValues(context.request,"weekList",""));
						outputMap.put("monthList",HTMLUtil.getParameterValues(context.request,"monthList",""));
						Output.jspOutput(outputMap,context,"/dun/dunCaseChart.jsp");
						return;
					}
					
					context.contextMap.put("dateList",param);
					resultList=this.dunCaseService.getMonthChart(context.contextMap);
					
					chartInfo.chartWidth=x.size()<=14?1024:(x.size()/14+1)*400;
					chartInfo1.chartWidth=x.size()<=14?1024:(x.size()/14+1)*400;
					
					chartInfo.setxLabel(x.toArray(new String [0]));
					chartInfo1.setxLabel(x.toArray(new String [0]));
					
					List<ChartDataSet> chartDataSet=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet=null;
					List<Double> yData=null;
					
					List<ChartDataSet> chartDataSet1=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet1=null;
					List<Double> yData1=null;
					/*for(int ii=0;ii<cmpyList.size();ii++) {
						if(!"ALL".equals(cmpyList.get(ii).getCompanyName())) {
							continue;
						}*/
					for(int ii=0;ii<3;ii++) {
						dataSet=new ChartDataSet();
						yData=new ArrayList<Double>();
						
						dataSet1=new ChartDataSet();
						yData1=new ArrayList<Double>();
						if(ii==0) {
							dataSet.setTitle("31天以上");
							dataSet1.setTitle("31天以上");
							for(int j=0;j<x.size();j++) {
								boolean flag=true;
								for(int i=0;i<resultList.size();i++) {
									if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
										yData.add(Double.valueOf(resultList.get(i).getPer31_money()));
										yData1.add(Double.valueOf(resultList.get(i).getPer31_count()));
										flag=false;
										break;
									}
								}
								if(flag) {
									yData.add(0.0);
									yData1.add(0.0);
								}
							}
						} else if(ii==1) {
							dataSet.setTitle("91天以上");
							dataSet1.setTitle("91天以上");
							for(int j=0;j<x.size();j++) {
								boolean flag=true;
								for(int i=0;i<resultList.size();i++) {
									if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
										yData.add(Double.valueOf(resultList.get(i).getPer91_money()));
										yData1.add(Double.valueOf(resultList.get(i).getPer91_count()));
										flag=false;
										break;
									}
								}
								if(flag) {
									yData.add(0.0);
									yData1.add(0.0);
								}
							}
						} else {
							dataSet.setTitle("181天以上");
							dataSet1.setTitle("181天以上");
							for(int j=0;j<x.size();j++) {
								boolean flag=true;
								for(int i=0;i<resultList.size();i++) {
									if(context.contextMap.get("DEPT_NAME").equals(resultList.get(i).getCompanyName())&&x.get(j).equals(resultList.get(i).getCreateOnDescr())) {
										yData.add(Double.valueOf(resultList.get(i).getPer181_money()));
										yData1.add(Double.valueOf(resultList.get(i).getPer181_count()));
										flag=false;
										break;
									}
								}
								if(flag) {
									yData.add(0.0);
									yData1.add(0.0);
								}
							}
						}
						dataSet.setyData(yData);
						chartDataSet.add(dataSet);
						dataSet1.setyData(yData1);
						chartDataSet1.add(dataSet1);
					/*}*/
					}
					chartInfo.setChartDataList(chartDataSet);
					chartInfo.setAlertTitle("{xLabel}日的比例是{value}%");
					chartResult1=ChartFactory.getLineChart(context.getRequest(),chartInfo);
					
					chartInfo1.setChartDataList(chartDataSet1);
					chartInfo1.setAlertTitle("{xLabel}日的比例是{value}%");
					chartResult2=ChartFactory.getLineChart(context.getRequest(),chartInfo1);
				}
			}
		} catch(Exception e) {
			logger.debug("生成逾期状况图表出错");
			context.errList.add("生成逾期状况图表出错");
			Output.errorPageOutput(e,context);
		}
		
		if(context.errList.isEmpty()) {
			outputMap.put("deptName",context.contextMap.get("DEPT_NAME"));
			outputMap.put("cmpyList",cmpyList);
			outputMap.put("chartResult1",chartResult1);
			outputMap.put("chartResult2",chartResult2);
			outputMap.put("dateFormat",context.contextMap.get("dateFormat")==null?"week":context.contextMap.get("dateFormat"));
			outputMap.put("fromDate",context.contextMap.get("fromDate"));
			outputMap.put("toDate",context.contextMap.get("toDate"));
			outputMap.put("dayList",HTMLUtil.getParameterValues(context.request,"dayList",""));
			outputMap.put("weekList",HTMLUtil.getParameterValues(context.request,"weekList","").length==0?weekList:HTMLUtil.getParameterValues(context.request,"weekList",""));
			outputMap.put("monthList",HTMLUtil.getParameterValues(context.request,"monthList",""));
			Output.jspOutput(outputMap,context,"/dun/dunCaseChart.jsp");
		}
	}
}
