package com.brick.unnaturalCase.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.chartDirector.ChartDataSet;
import com.brick.chartDirector.ChartFactory;
import com.brick.chartDirector.ChartInfo;
import com.brick.chartDirector.ChartResult;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.unnaturalCase.service.UnnaturalCaseService;
import com.brick.unnaturalCase.to.UnnaturalCaseTO;
import com.brick.util.DateUtil;

public class UnnaturalCaseCommand extends BaseCommand {

	Log logger=LogFactory.getLog(UnnaturalCaseCommand.class);

	private UnnaturalCaseService unnaturalCaseService;

	public UnnaturalCaseService getUnnaturalCaseService() {
		return unnaturalCaseService;
	}

	public void setUnnaturalCaseService(UnnaturalCaseService unnaturalCaseService) {
		this.unnaturalCaseService = unnaturalCaseService;
	}

	//案件进度异常功能
	public void queryCaseCompare(Context context) {

		if(logger.isDebugEnabled()) {
			logger.debug("--------------案件进度异常--------------开始(employeeId:"+context.contextMap.get("s_employeed")+")");
		}

		Map<String,Object> outputMap=new HashMap<String,Object>();
		//获得时间下拉框
		List<Map<String,Object>> dateList=null;
		List deptList=null;
		PagingInfo<Object> pagingInfo=null;

		try {
			dateList=this.unnaturalCaseService.getDateList("1");

			deptList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);

			context.contextMap.put("CREATE_ON",(String)context.contextMap.get("DATE"));
			context.contextMap.put("DEPT_ID",(String)context.contextMap.get("DEPT_ID"));

			if(context.contextMap.get("__pageSize")==null) {
				context.contextMap.put("__pageSize","20");
			}
			pagingInfo=baseService.queryForListWithPaging("unnaturalCase.getUnnaturalCaseCompare",context.contextMap,"[order]",ORDER_TYPE.ASC);

		} catch (Exception e) {
			logger.debug("案件进度异常出错,请联系管理员!");
			context.errList.add("案件进度异常出错,请联系管理员!");
			e.printStackTrace();
		}

		if(context.contextMap.get("DATE")==null) {
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			outputMap.put("DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
		} else {
			outputMap.put("DATE",context.contextMap.get("DATE"));
		}

		//生成图表-----------------------------开始
		ChartResult chartResult=null;
		ChartInfo chartInfo=new ChartInfo();
		chartInfo.chartWidth=1200;
		chartInfo.chartHeight=600;
		chartInfo.xLableFontAngle=0;
		chartInfo.setChartTitle("案件进度异常("+outputMap.get("DATE")+")");

		chartInfo.setyTitle("件数(单位:件)");
		String[] xLabel=new String[]{"进件~访厂(>5)","访厂~初次风控(>5)","初次风控~最终风控(>5)","最终风控~审查核准(>3)","审查核准~业管初审(>30)","业管初审~拨款(>5)","进件~拨款(>50)"};
		chartInfo.setxLabel(xLabel);

		List<ChartDataSet> chartDataSet=new ArrayList<ChartDataSet>();
		for(int i=0;pagingInfo!=null&&i<pagingInfo.getResultList().size();i++) {
			ChartDataSet dataSet=new ChartDataSet();
			dataSet.setTitle(((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getDeptName());
			dataSet.setyData(new double[]{Double.valueOf(String.valueOf(((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getA_B())),
					Double.valueOf(String.valueOf(((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getB_C())),
					Double.valueOf(String.valueOf(((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getC_D())),
					Double.valueOf(String.valueOf(((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getD_E())),
					Double.valueOf(String.valueOf(((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getE_F())),
					Double.valueOf(String.valueOf(((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getF_G())),
					Double.valueOf(String.valueOf(((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getA_G()))});
			chartDataSet.add(dataSet);
		}

		chartInfo.setChartDataList(chartDataSet);
		chartInfo.setAlertTitle(outputMap.get("DATE")+" {dataSetName}之{xLabel}的件数是{value}");
		try {
			chartResult=ChartFactory.getCylinderChart(context.getRequest(),chartInfo);
		} catch(Exception e) {
			e.printStackTrace();
		}

		outputMap.put("chartResult",chartResult);
		//生成图表-----------------------------结束
		if(context.errList.isEmpty()) {
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("dateList",dateList);
			outputMap.put("deptList",deptList);
			outputMap.put("pagingInfo",pagingInfo);
			Output.jspOutput(outputMap,context,"/unnaturalCase/unnaturalCaseCompare.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug("--------------案件进度异常--------------结束(employeeId:"+context.contextMap.get("s_employeed")+")");
		}
	}

	//逾期25天以上(含),前3期逾期15天以上未回访
	public void queryDunCase(Context context) {

		if(logger.isDebugEnabled()) {
			logger.debug("--------------案件逾期未回访--------------开始(employeeId:"+context.contextMap.get("s_employeed")+")");
		}

		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> pagingInfo=null;
		List<Map<String,Object>> dateList=null;
		List deptList=null;
		Map<String,Object> resultMap=new HashMap<String,Object>();

		try {
			//获得页面部门搜索条件的List
			deptList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);
			//获得页面日期搜索条件的List
			dateList=this.unnaturalCaseService.getDateList("2");

			pagingInfo=baseService.queryForListWithPaging("unnaturalCase.getUnnaturalDunCase",context.contextMap,"dunDay",ORDER_TYPE.DESC);

			resultMap=this.unnaturalCaseService.getUnnaturalDunCaseCount(context.contextMap);
			//页面点击供应商跳转到供应商贡献度,由于1个案子可能有多个供应商,所以需要把供应商放入pagingInfo.resultList中的一个suplNameList继续在页面遍历
			Map<String,String> suplMap=null;
			for(int i=0;pagingInfo!=null&&i<pagingInfo.getResultList().size();i++) {
				String suplName=((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getSuplName();
				if(suplName.indexOf(",")!=-1) {
					for(int j=0;j<suplName.split(",").length;j++) {
						suplMap=new HashMap<String,String>();
						if(j!=suplName.split(",").length-1) {
							//供应商加入换行
							suplMap.put("code",suplName.split(",")[j]);
							suplMap.put("descr",suplName.split(",")[j]+"<br>");
							((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getSuplList().add(suplMap);
						} else {
							suplMap.put("code",suplName.split(",")[j]);
							suplMap.put("descr",suplName.split(",")[j]);
							((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getSuplList().add(suplMap);
						}
					}
				} else {
					suplMap=new HashMap<String,String>();
					suplMap.put("code",suplName);
					suplMap.put("descr",suplName);
					((UnnaturalCaseTO)pagingInfo.getResultList().get(i)).getSuplList().add(suplMap);
				}
			}
		} catch(Exception e) {
			logger.debug("案件逾期未回访出错,请联系管理员!");
			context.errList.add("案件逾期未回访出错,请联系管理员!");
			e.printStackTrace();
		}

		if(context.errList.isEmpty()) {
			outputMap.put("CONTENT",context.contextMap.get("CONTENT"));
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("DATE",context.contextMap.get("DATE"));
			outputMap.put("IS_UNNATURAL_CASE",context.contextMap.get("IS_UNNATURAL_CASE"));
			outputMap.put("dateList",dateList);
			outputMap.put("deptList",deptList);
			outputMap.put("pagingInfo",pagingInfo);
			outputMap.put("resultMap",resultMap);
			Output.jspOutput(outputMap,context,"/unnaturalCase/unnaturalDunCase.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		if(logger.isDebugEnabled()) {
			logger.debug("--------------案件逾期未回访--------------结束(employeeId:"+context.contextMap.get("s_employeed")+")");
		}
	}

	public void queryUncompletedFileCase(Context context) {

		if(logger.isDebugEnabled()) {
			logger.debug("--------------案件拨款后待补文件--------------开始(employeeId:"+context.contextMap.get("s_employeed")+")");
		}

		PagingInfo<Object> pagingInfo=null;
		Map<String, Object> outputMap=new HashMap<String, Object>();
		List deptList=null;
		List<Map<String,Object>> dateList=null;

		try {
			dateList=this.unnaturalCaseService.getDateList("3");

			deptList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);

			if(context.contextMap.get("CONTENT")==null) {
				// modify by Zhangyizhou on 2014-06-13 Begin
				//note : 查询内容的默认文本 由  '清偿文件' 变更为  '购置凭证' 
				context.contextMap.put("CONTENT","购置凭证");
				/*
				context.contextMap.put("CONTENT","清偿文件");
				*/
				// modify by Zhangyizhou on 2014-06-13 End
			}
			pagingInfo=baseService.queryForListWithPaging("unnaturalCase.getUnnaturalUncompletedFileCase",context.contextMap,"delayDay",ORDER_TYPE.DESC);
		} catch(Exception e) {
			logger.debug("案件拨款后待补文件出错,请联系管理员!");
			context.errList.add("案件拨款后待补文件出错,请联系管理员!");
			e.printStackTrace();
		}

		if(context.errList.isEmpty()) {
			outputMap.put("DATE",context.contextMap.get("DATE"));
			outputMap.put("dateList",dateList);
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("deptList",deptList);
			outputMap.put("pagingInfo",pagingInfo);
			outputMap.put("content",context.contextMap.get("CONTENT"));
			// add by ZhangYizhou on 2014-06-13 Begin
	        // NOTE：增加拨款方式查询条件
			outputMap.put("TYPE",context.contextMap.get("TYPE"));
			// add by ZhangYizhou on 2014-06-13 End
			Output.jspOutput(outputMap,context,"/unnaturalCase/unnaturalCompletedFile.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug("--------------案件拨款后待补文件--------------结束(employeeId:"+context.contextMap.get("s_employeed")+")");
		}
	}

	public void queryOnGoingInsuranceCase(Context context) {

		if(logger.isDebugEnabled()) {
			logger.debug("--------------出险逾期60天未理赔结案--------------开始(employeeId:"+context.contextMap.get("s_employeed")+")");
		}

		PagingInfo<Object> pagingInfo=null;
		Map<String, Object> outputMap=new HashMap<String, Object>();
		List deptList=null;
		List<Map<String,Object>> dateList=null;

		try {
			dateList=this.unnaturalCaseService.getDateList("4");

			deptList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);

			pagingInfo=baseService.queryForListWithPaging("unnaturalCase.getUnnaturalOnGoingInsuranceCase",context.contextMap,"dayDiff",ORDER_TYPE.DESC);
		} catch(Exception e) {
			logger.debug("出险逾期60天未理赔结案出错,请联系管理员!");
			context.errList.add("出险逾期60天未理赔结案出错,请联系管理员!");
			e.printStackTrace();
		}

		if(context.errList.isEmpty()) {
			outputMap.put("DATE",context.contextMap.get("DATE"));
			outputMap.put("dateList",dateList);
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("deptList",deptList);
			outputMap.put("pagingInfo",pagingInfo);
			outputMap.put("content",context.contextMap.get("CONTENT"));
			Output.jspOutput(outputMap,context,"/unnaturalCase/unnaturalOnGoingInsurance.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug("--------------出险逾期60天未理赔结案--------------结束(employeeId:"+context.contextMap.get("s_employeed")+")");
		}
	}

	public void queryDynamicCase(Context context) {

		if(logger.isDebugEnabled()) {
			logger.debug("--------------未拨款案件进度异常--------------开始(employeeId:"+context.contextMap.get("s_employeed")+")");
		}

		Map<String, Object> outputMap=new HashMap<String, Object>();

		//获得时间下拉框
		List<Map<String,Object>> dateList=null;
		List deptList=null;
		PagingInfo<Object> pagingInfo=null;

		try {
			dateList=this.unnaturalCaseService.getDateList("5");

			deptList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);

			context.contextMap.put("CREATE_ON",(String)context.contextMap.get("DATE"));
			context.contextMap.put("DEPT_ID",(String)context.contextMap.get("DEPT_ID"));

			if(context.contextMap.get("__pageSize")==null) {
				context.contextMap.put("__pageSize","20");
			}
			pagingInfo=baseService.queryForListWithPaging("unnaturalCase.getDynamicCase",context.contextMap,"[order]",ORDER_TYPE.ASC);

		} catch (Exception e) {
			logger.debug("未拨款案件进度异常出错,请联系管理员!");
			context.errList.add("未拨款案件进度异常出错,请联系管理员!");
			e.printStackTrace();
		}

		if(context.contextMap.get("DATE")==null) {
			Calendar cal=Calendar.getInstance();
			outputMap.put("DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
		} else {
			outputMap.put("DATE",context.contextMap.get("DATE"));
		}

		if(context.errList.isEmpty()) {
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("dateList",dateList);
			outputMap.put("deptList",deptList);
			outputMap.put("pagingInfo",pagingInfo);
			Output.jspOutput(outputMap,context,"/unnaturalCase/unnaturalDynamicCase.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug("--------------未拨款案件进度异常--------------结束(employeeId:"+context.contextMap.get("s_employeed")+")");
		}
	}

	public void queryDynamicCaseDetail(Context context) {

		if(logger.isDebugEnabled()) {
			logger.debug("--------------未拨款案件进度异常查看详细--------------开始(employeeId:"+context.contextMap.get("s_employeed")+")");
		}

		Map<String, Object> outputMap=new HashMap<String, Object>();

		PagingInfo<Object> pagingInfo=null;

		try {
			if("a".equals(context.contextMap.get("flag"))) {
				context.contextMap.put("CODE","进件~未访厂");
			} else if("b".equals(context.contextMap.get("flag"))) {
				context.contextMap.put("CODE","已访厂~未提交风控");
			} else if("c".equals(context.contextMap.get("flag"))) {
				context.contextMap.put("CODE","初次提交风控~未最终提交风控");
			} else if("d".equals(context.contextMap.get("flag"))) {
				context.contextMap.put("CODE","最终提交风控~审查未核准");
			} else if("e".equals(context.contextMap.get("flag"))) {
				context.contextMap.put("CODE","审查核准~业管未初审");
			} else if("f".equals(context.contextMap.get("flag"))) {
				context.contextMap.put("CODE","业管初审~未拨款");
			} else if("g".equals(context.contextMap.get("flag"))) {
				context.contextMap.put("CODE","进件~未拨款");
			}
			pagingInfo=baseService.queryForListWithPaging("unnaturalCase.getDynamicCaseDetail",context.contextMap,"dayDiff",ORDER_TYPE.DESC);
		} catch (ServiceException e) {
			logger.debug("未拨款案件进度异常查看详细出错,请联系管理员!");
			context.errList.add("未拨款案件进度异常查看详细出错,请联系管理员!");
			e.printStackTrace();
		}

		if(context.errList.isEmpty()) {
			outputMap.put("pagingInfo",pagingInfo);
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("DATE",context.contextMap.get("DATE"));
			outputMap.put("flag",context.contextMap.get("flag"));

			Output.jspOutput(outputMap,context,"/unnaturalCase/unnaturalDynamicCaseDetail.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		if(logger.isDebugEnabled()) {
			logger.debug("--------------未拨款案件进度异常查看详细--------------结束(employeeId:"+context.contextMap.get("s_employeed")+")");
		}
	}

	public void queryPendingApproveCase(Context context) {

		Map<String,Object> outputMap=new HashMap<String,Object>();
		//获得时间下拉框
		List<Map<String,Object>> dateList=null;
		List deptList=null;
		PagingInfo<Object> pagingInfo=null;

		try {
			dateList=this.unnaturalCaseService.getDateList("6");

			deptList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);

			pagingInfo=baseService.queryForListWithPaging("unnaturalCase.queryPendingApproveCase",context.contextMap,"dayDiff",ORDER_TYPE.DESC);
			
		} catch(Exception e) {
			logger.debug("提交审查逾10天未核准出错,请联系管理员!");
			context.errList.add("提交审查逾10天未核准出错,请联系管理员!");
			e.printStackTrace();
		}
		
		if(context.errList.isEmpty()) {
			outputMap.put("DATE",context.contextMap.get("DATE"));
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("dateList",dateList);
			outputMap.put("deptList",deptList);
			outputMap.put("pagingInfo",pagingInfo);
			Output.jspOutput(outputMap,context,"/unnaturalCase/queryPendingApproveCase.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}

	public void queryPendingCommitCase(Context context) {

		Map<String,Object> outputMap=new HashMap<String,Object>();
		//获得时间下拉框
		List<Map<String,Object>> dateList=null;
		List deptList=null;
		PagingInfo<Object> pagingInfo=null;
		
		try {
			dateList=this.unnaturalCaseService.getDateList("7");

			deptList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);

			pagingInfo=baseService.queryForListWithPaging("unnaturalCase.queryPendingCommitCase",context.contextMap,"dayDiff",ORDER_TYPE.DESC);
		} catch(Exception e) {
			logger.debug("访厂逾14天未提交审查出错,请联系管理员!");
			context.errList.add("访厂逾14天未提交审查出错,请联系管理员!");
			e.printStackTrace();
		}
		
		if(context.errList.isEmpty()) {
			outputMap.put("DATE",context.contextMap.get("DATE"));
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("dateList",dateList);
			outputMap.put("deptList",deptList);
			outputMap.put("pagingInfo",pagingInfo);
			Output.jspOutput(outputMap,context,"/unnaturalCase/queryPendingCommitCase.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	public void queryDunVisitCase(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		//获得时间下拉框
		List<Map<String,Object>> dateList=null;
		List deptList=null;
		PagingInfo<Object> pagingInfo=null;
		
		try {
			dateList=this.unnaturalCaseService.getDateList("8");

			deptList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);

			pagingInfo=baseService.queryForListWithPaging("unnaturalCase.queryDunVisitCase",context.contextMap,"dayDiff",ORDER_TYPE.DESC);
		} catch(Exception e) {
			logger.debug("访厂报告生成逾11天未提交出错,请联系管理员!");
			context.errList.add("访厂报告生成逾11天未提交出错,请联系管理员!");
			e.printStackTrace();
		}
		
		if(context.errList.isEmpty()) {
			outputMap.put("DATE",context.contextMap.get("DATE"));
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("dateList",dateList);
			outputMap.put("deptList",deptList);
			outputMap.put("pagingInfo",pagingInfo);
			Output.jspOutput(outputMap,context,"/unnaturalCase/queryDunVisitCase.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
	}
}
