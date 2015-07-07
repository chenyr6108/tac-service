package com.brick.report.service;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;

import com.brick.report.to.UnAllotAndDelayAllotChartTo;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.DeptMapListener;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.chartDirector.ChartDataSet;
import com.brick.chartDirector.ChartFactory;
import com.brick.chartDirector.ChartInfo;
import com.brick.chartDirector.ChartResult;
import com.brick.log.service.LogPrint;
import com.ibm.icu.math.BigDecimal;


public class ReportService extends BaseCommand {
	Log logger = LogFactory.getLog(ReportService.class);

	public static final Logger log = Logger.getLogger(ReportService.class);

	/**
	 * 查询所有应收账款信息
	 * 
	 * @param context
	 */
	public void queryNoDecomposeReport(Context context) {
		Map outputMap = new HashMap();
		List errorList = null;
		errorList = context.errList;
		Map constructAnalysisMap = new HashMap();
		Map timesAnalysisMap = new HashMap();
		List executePercentList = new ArrayList();
		String card = context.getRequest().getParameter("cardFlag");
		int cardFlag = card == null ? 0 : Integer.parseInt(card);
		if (errorList.isEmpty()) {
			try {
				constructAnalysisMap = (Map) DataAccessor.query(
						"report.noDecomposeConstructAnalysis",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				timesAnalysisMap = (Map) DataAccessor.query(
						"report.noDecomposeTimesAnalysis",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				executePercentList = (List) DataAccessor.query(
						"report.decomposeExecutePercent",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.report.service.ReportService.queryReport"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.report.service.ReportService.queryReport"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cardFlag", cardFlag);
		outputMap.put("constructAnalysisMap", constructAnalysisMap);
		outputMap.put("timesAnalysisMap", timesAnalysisMap);
		outputMap.put("executePercentList", executePercentList);
		Output.jspOutput(outputMap, context,
				"/report/reportMain.jsp");
	}
	
	/**
	 * 查询所有应收账款信息
	 * 
	 * @param context
	 */
	public void queryDunCondition(Context context) {
		Map outputMap = new HashMap();
		List errorList = null;
		errorList = context.errList;
		Map conditionMap = new HashMap();
		Map oneMap = new HashMap();
		Map twoMap = new HashMap();
		String card = context.getRequest().getParameter("cardFlag");
		int cardFlag = card == null ? 0 : Integer.parseInt(card);
		if (errorList.isEmpty()) {
			try {
				conditionMap = (Map) DataAccessor.query(
						"report.queryDunCondition",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				oneMap = (Map) DataAccessor.query(
						"report.queryDunOne",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				twoMap = (Map) DataAccessor.query(
						"report.queryDunTwo",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				log
						.error("com.brick.report.service.ReportService.queryDunCondition"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.report.service.ReportService.queryDunCondition"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cardFlag", cardFlag);
		outputMap.put("conditionMap", conditionMap);
		outputMap.put("oneMap", oneMap);
		outputMap.put("twoMap", twoMap);
		Output.jspOutput(outputMap, context,
				"/report/dunMain.jsp");
	}
	
	
	
	/**
	 * 查询  长期应收款余额变动表
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRealPriceReport(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> dw = null;
		if (errList.isEmpty()) {
			try {
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				
				if("caiwu".equals(context.contextMap.get("realPriceType"))){
					dw = baseService.queryForListWithPaging("priceReport.queryRealPriceCAIWU", context.contextMap, "LEASE_CODE", ORDER_TYPE.DESC);
					outputMap.put("realPriceType", "caiwu") ;
				} else {
					dw = baseService.queryForListWithPaging("priceReport.queryRealPrice", context.contextMap, "LEASE_CODE", ORDER_TYPE.DESC);
					outputMap.put("realPriceType", "shuiwu") ;
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if(errList.isEmpty()){
			outputMap.put("content", context.contextMap.get("content")) ;
			outputMap.put("dw", dw);
			outputMap.put("companyCode", context.contextMap.get("companyCode"));
			outputMap.put("companys", LeaseUtil.getCompanys());	
//			outputMap.put("startDate", context.contextMap.get("startDate")) ;
//			outputMap.put("endDate", context.contextMap.get("endDate")) ;
			Output.jspOutput(outputMap, context, "/report/queryRealPrice.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 导出 excel
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void expRealPriceToExcel(Context context){  
		List<Map> realPriceList=null;
		String realPriceType = "" ;
		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			if("caiwu".equals(context.contextMap.get("realPriceType"))){
				realPriceType = "财务" ;
				realPriceList=(List<Map>) DataAccessor.query("priceReport.queryRealPriceCAIWU", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} else {
				realPriceType = "税务" ;
				realPriceList=(List<Map>) DataAccessor.query("priceReport.queryRealPrice", context.contextMap, DataAccessor.RS_TYPE.LIST);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "长期应收款余额变动表("+realPriceType+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportRealPriceExcel(realPriceList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	
	/**
	 * 查询  保证金余额
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryPledgeReport(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;
		if (errList.isEmpty()) {
			try {	
				dw = (DataWrap) DataAccessor.query("priceReport.queryPledge", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if(errList.isEmpty()){
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("startDate", context.contextMap.get("startDate")) ;
			outputMap.put("endDate", context.contextMap.get("endDate")) ;
			Output.jspOutput(outputMap, context, "/report/queryPledge.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 查询  已核准未拨款
	 * modify by xuyuefei 增加公司别  2014/7/18
	 * @param context
	 */
	public void queryNotToAllot(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		String date="";
		PagingInfo<Object> dw = null;
		List<Map<String,String>> companys=null;
		String totalAmount=null;//所有拨款的总和
		String totalCount=null;//页面的count的总数
		if (errList.isEmpty()) {
			try {
				if(context.contextMap.get("date")!=null){
					date=(String)context.contextMap.get("date");			
				}else{
					DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
					date=format.format(new Date());
				}
				String validateStatus = (String) context.contextMap.get("validateStatus");
				String delayStatus = (String) context.contextMap.get("delayStatus");
				if (validateStatus == null) {
					validateStatus = "N";
				}
				if (delayStatus == null) {
					delayStatus = "N";
				}
				context.contextMap.put("validateStatus", validateStatus);
				context.contextMap.put("delayStatus", delayStatus);
				outputMap.put("validateStatus", validateStatus);
				outputMap.put("delayStatus", delayStatus);
				
				//准备查询参数For 'priceReport.queryNotToAllot'方法 add by Shen Qi
				Map paramMap = new HashMap();
				paramMap.put("id", context.contextMap.get("s_employeeId"));
				Map rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
						paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				//获得办事处的drop down List
				context.contextMap.put("decp_id", "2");//2代表的是拿分公司
				companys=(List<Map<String,String>>)DataAccessor.query("employee.getCompany", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				context.contextMap.put("date", date);
				dw = baseService.queryForListWithPaging("priceReport.queryNotToAllot", context.contextMap, "EFFECTDATE");
				
				//delayPay是控制页面是否显示缓拨
				boolean delayPay=false;
				//通过emplId获得用户的ResourceId.(add by ShenQi)
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			    /*ResourceId               Permission  
		         *217  						缓拨
			     * */
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					//below is hard code for ResourceId,we will enhance it in the future
					if("217".equals(resourceIdList.get(i))) {
						delayPay=true;
					}
				}
				
				outputMap.put("delayPay",delayPay);
				
				if(dw==null) {
					totalAmount="0";
					totalCount="0";
				} else {
					if(context.contextMap.get("totalAmount")!=null//避免每次都去数据库跑SQL计算总金额,把总金额隐藏在页面
							&&
							(!"".equals(context.contextMap.get("content"))//如果页面输入查询条件则需要重新从数据库计算金额总数
									&&!"".equals(context.contextMap.get("NAME"))&&!"".equals(context.contextMap.get("OFFICE")))) {
						totalAmount=(String)context.contextMap.get("totalAmount");
						totalCount=(String)context.contextMap.get("totalCount");
					} else {
						totalAmount=(String)DataAccessor.query("priceReport.queryNotToAllotSum", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
						totalCount=String.valueOf(dw.getTotalCount());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if(errList.isEmpty()){
			outputMap.put("dw", dw);
			outputMap.put("date", date);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("NAME", context.contextMap.get("NAME"));
			outputMap.put("OFFICE", context.contextMap.get("OFFICE"));
			outputMap.put("companys", companys);
			outputMap.put("companys1", LeaseUtil.getCompanys());
			outputMap.put("companyCode", context.contextMap.get("companyCode"));
			outputMap.put("totalCount", totalCount);
			outputMap.put("totalAmount", totalAmount);
			outputMap.put("validateStatus", context.contextMap.get("validateStatus"));
			outputMap.put("delayStatus", context.contextMap.get("delayStatus"));
			outputMap.put("vip_flag", context.contextMap.get("vip_flag"));
			Output.jspOutput(outputMap, context, "/report/queryNotToAllot.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 导出 excel 已核准未拨款
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void expNotToAllotToExcel(Context context){  
		List<Map> notToAllotList=null;
		String date="";
		String totalAmount=null;//所有拨款的总和
		String totalCount=null;//页面的count的总数
		try {
			if(context.contextMap.get("date")!=null){
				date=(String)context.contextMap.get("date");			
			}else{
				DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				date=format.format(new Date());
			}
			context.contextMap.put("date", date);
			
			//准备查询参数For 'priceReport.queryNotToAllot'方法 add by Shen Qi
			String node=(String) DataAccessor.query("priceReport.checkUserNode", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			//node=0,1,2
			//0代表功能标识全部,1代表部分,2代表区域
			if("0".equals(node)) {
				//遍历所有数据
			} else {
				//判断登录人是业务员还是业务经理
				String job=(String) DataAccessor.query("priceReport.checkIsUpperUser", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				//job=1 是普通业务员, job=2是业务主管
				if("2".equalsIgnoreCase(job)) {
					//业务主管设置ROLE=2
					context.contextMap.put("ROLE","2");
				} else if("1".equalsIgnoreCase(job)) {
					//普通业务员设置ROLE=1
					context.contextMap.put("ROLE","1");
				} else {//其他角色进入,通过他所在的区域(办事处)设定查看权限
					context.contextMap.put("ROLE","3");
				}
			}
			
			notToAllotList=(List<Map>) DataAccessor.query("priceReport.queryNotToAllot", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			totalAmount=(String) context.contextMap.get("totalAmountForExcel");//所有拨款的总和
			totalCount=(String) context.contextMap.get("totalCount");//页面的count的总数
			
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "已核准未拨款表("+DataUtil.StringUtil(context.contextMap.get("date"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportNotToAllotExcel(notToAllotList,totalCount,totalAmount);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	//财务报表-保证金余额 导出exl
	@SuppressWarnings("unchecked")
	public void exportPledgeBalance(Context context){
		List errList=context.errList;
		Map exportMap = new HashMap();
		String strFileName = "保证金余额表.xls";
		try{
			List<Map> content=(List<Map>) ((DataWrap)DataAccessor.query("priceReport.queryPledge", context.contextMap, DataAccessor.RS_TYPE.PAGED)).getRs();
			exportMap.put("content", content);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		ByteArrayOutputStream baos = null;
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportPledgeExcel(exportMap);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	}
	
	/**
	 * 2012/02/06 Yang Yun 
	 * 逾期状况统计表
	 */
	public void queryDunInfo(Context context){
		Map<String, Object> outMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> areaList = null;
		String search_dun_day = (String) context.contextMap.get("search_dun_day");
		String queryType = (String) context.contextMap.get("QUERY_TYPE");
		try {
			if (search_dun_day == null || "".equals(search_dun_day)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date now = new Date();
				Calendar ca = Calendar.getInstance();
				ca.set(2013, 11, 31);
//				ca.setTime(now);
//				ca.add(Calendar.DATE, -1);
				search_dun_day = sdf.format(ca.getTime());
			}
			if(queryType == null || "".equals(queryType)){
				//0代表全部
				queryType="0";
			}else if(("1").equals(queryType)){
				//1代表查询重车
				paramMap.put("TYPE1", "商用车");
				paramMap.put("TYPE2", "重车");
			}else if(("2").equals(queryType)){
				//2代表查询设备
				paramMap.put("TYPE1", "设备");
				paramMap.put("TYPE2", "设备");
			}else if(("3").equals(queryType)){
				//3代表查询乘用车
				paramMap.put("TYPE1", "乘用车");
				paramMap.put("TYPE2", "乘用车");
			}
			paramMap.put("search_dun_day", search_dun_day);
			paramMap.put("sub_total", "合计");
			paramMap.put("QUERY_TYPE", queryType);
			
			paramMap.put("dataType", "设备业务区域");
			areaList=(List<Map<String, Object>>) DataAccessor.query("dataDictionary.queryDataDictionary", paramMap, DataAccessor.RS_TYPE.LIST);
			outMap.put("areaList", areaList);
			
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
				paramMap.put("cmpyList",param.toString());
			}
			
			resultList = (List<Map<String, Object>>) DataAccessor.query("report.getDunInformation", paramMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outMap.put("area", context.contextMap.get("area"));
		outMap.put("type", context.contextMap.get("type"));
		outMap.put("dunInfoList", resultList);
		outMap.put("search_dun_day", search_dun_day);
		outMap.put("QUERY_TYPE", queryType);
		Output.jspOutput(outMap, context, "/report/dunInformation.jsp");
	}
	
	//缓拨案件
	@Transactional(rollbackFor=Exception.class)
	public void delayPay(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			DataAccessor.execute("priceReport.delayPay",context.contextMap,OPERATION_TYPE.UPDATE);
			
			String creditCode=(String)DataAccessor.query("creditReportManage.getCreditCode",context.contextMap,RS_TYPE.OBJECT);
			
			BusinessLog.addBusinessLogWithIp(Long.parseLong((String)context.contextMap.get("CREDIT_ID")),null,"已核准未拨款管理", 
					"缓拨",creditCode,context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在已核准未拨款功能下使用缓拨功能", 
					1,Long.parseLong(context.contextMap.get("s_employeeId").toString()), 
					null,(String)context.contextMap.get("IP"));
		} catch (Exception e) {
			context.errList.add("案件缓拨出错!(delayPay)");
			e.printStackTrace();
		}
		
		if(context.errList.isEmpty()) {
			this.queryNotToAllot(context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	//启用缓拨案件
	@Transactional(rollbackFor=Exception.class)
	public void enablePay(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			DataAccessor.execute("priceReport.enablePay",context.contextMap,OPERATION_TYPE.UPDATE);
			
			String creditCode=(String)DataAccessor.query("creditReportManage.getCreditCode",context.contextMap,RS_TYPE.OBJECT);
			
			BusinessLog.addBusinessLogWithIp(Long.parseLong((String)context.contextMap.get("CREDIT_ID")),null,"已核准未拨款管理", 
					"启用",creditCode,context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在已核准未拨款功能下使用启用功能", 
					1,Long.parseLong(context.contextMap.get("s_employeeId").toString()), 
					null,(String)context.contextMap.get("IP"));
		} catch (Exception e) {
			context.errList.add("案件启用出错!(enablePay)");
			e.printStackTrace();
		}
		
		if(context.errList.isEmpty()) {
			this.queryNotToAllot(context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	/**
	 * 审查第一次回复时间
	 * @param context
	 * @throws Exception 
	 */
	public void getRiskAvgTimeReport(Context context) throws Exception{
		try {
			Map<String,Object> outputMap = new HashMap<String,Object>();
			Map<String,Object> paraMap = new HashMap<String,Object>();
			List<Map<String,Object>> resultList = null;
			List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
			Map<String, String> dayMap = null;
			String year = (String) context.contextMap.get("year");
			if (StringUtils.isEmpty(year)) {
				year = DateUtil.getCurrentYear();
			}
			paraMap.put("year", year);
			String decp_name = null;
			String month = null;
			String avg_day = null;
			resultList = (List<Map<String, Object>>) baseService.queryForList("report.getRiskAvgTimeReport", paraMap);
			if (resultList != null && resultList.size() > 0) {
				Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();
				for (Map<String, Object> map : resultList) {
					decp_name = String.valueOf(map.get("DECP_NAME"));
					month = String.valueOf(map.get("MONTH"));
					avg_day = String.valueOf(map.get("AVG_DAY"));
					dayMap = dataMap.get(decp_name);
					if (dayMap == null) {
						dayMap = new HashMap<String, String>();
					}
					dayMap.put("month_" + month, avg_day);
					dataMap.put(decp_name, dayMap);
				}
				for (SelectionTo to : baseService.getAllOffice()) {
					for (String key : dataMap.keySet()) {
						if (key.equals(to.getDisplay_name())) {
							dataMap.get(key).put("decp_name", key);
							dataList.add(dataMap.get(key));
						}
					}
				}
				if (dataMap.get("绿色通道") != null) {
					dataMap.get("绿色通道").put("decp_name", "绿色通道");
					dataList.add(dataMap.get("绿色通道"));
				}
			}
			resultList = (List<Map<String, Object>>) baseService.queryForList("report.getRiskAvgTimeReportTotal", paraMap);
			if (resultList != null && resultList.size() > 0) {
				dayMap = new HashMap<String, String>();
				for (Map<String, Object> map : resultList) {
					month = String.valueOf(map.get("MONTH"));
					avg_day = String.valueOf(map.get("AVG_DAY"));
					dayMap.put("month_" + month, avg_day);
				}
				dayMap.put("decp_name", "合计");
				dataList.add(dayMap);
			}
			outputMap.put("dataList", dataList);
			outputMap.put("year", year);
			Output.jspOutput(outputMap, context, "/report/riskAvgDay.jsp");
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}
	
	//run job  add by xuyuefei 2014 5/23
	/*
	 * properties:
	 *   creditCount 案件总数
	 *   delayCount  缓拨件数
	 *   delayPay    缓拨金额
	 *   unAllotCount 已核未拨件数
	 *   unAllotPay   已核未拨金额
	 *   t1           抓取已核未缓拨数据的参数
	 *   t2        缓拨参数
	 *   t3         案件总数的参数
	 */
	public void addLog()throws Exception{
    	int creditCount;
    	int delayCount;
    	double delayPay;
    	int unAllotCount;
    	double unAllotPay;
    	Map<String,Object> param=new HashMap<String,Object>();
        Map<String,Object> t1=new HashMap<String,Object>();
        Map<String,Object> t2=new HashMap<String,Object>();
        Map<String,Object> t3=new HashMap<String,Object>();
        t1.put("validateStatus", "N");
        t1.put("delayStatus", "N");
        unAllotPay=Double.parseDouble((String)DataAccessor.query("priceReport.queryNotToAllotSum", 
        		t1,DataAccessor.RS_TYPE.OBJECT));
        t2.put("validateStatus", "N");
        t2.put("delayStatus", "Y");
        delayPay=Double.parseDouble((String)DataAccessor.query("priceReport.queryNotToAllotSum", 
        		t2,DataAccessor.RS_TYPE.OBJECT));
        t3.put("validateStatus", "");
        t3.put("delayStatus", "");
        List lst1=(List)DataAccessor.query("priceReport.queryNotToAllot", t1, DataAccessor.RS_TYPE.LIST);
        List lst2=(List)DataAccessor.query("priceReport.queryNotToAllot", t2, DataAccessor.RS_TYPE.LIST);
        List lst3=(List)DataAccessor.query("priceReport.queryNotToAllot", t3, DataAccessor.RS_TYPE.LIST);
        if(lst1.isEmpty()){
        	unAllotCount=0;
        }else{
        	unAllotCount=lst1.size();
        }
        if(lst2.isEmpty()){
        	delayCount=0;
        }else{
        	delayCount=lst2.size();
        }
        if(lst3.isEmpty()){
        	creditCount=0;
        }else{
        	creditCount=lst3.size();
        }
        param.put("id", String.valueOf(System.currentTimeMillis()));
        param.put("creditCount", creditCount);
        param.put("delayAllotCount", delayCount);
        param.put("unAllotCount", unAllotCount+delayCount);
        param.put("unAllotPay", unAllotPay+delayPay);
        param.put("delayAllotPay", delayPay);
        DataAccessor.execute("priceReport.addLog", param, DataAccessor.OPERATION_TYPE.INSERT);
	}
	
	/*
	 * 
	 * 生成图表
	 * add by xuyuefei 2014/6/4
	 */
	public void exportChart(Context context){
		
		Calendar backcal=Calendar.getInstance();
		backcal.add(Calendar.DATE, -1);
		Map<String,Object> param=new HashMap<String,Object>();
		Map<String,Object> outputMap = new HashMap<String,Object>();
		ChartResult chartResult1=null;
		ChartResult chartResult2=null;
		String fromDate=null;
		String toDate=null;
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal=Calendar.getInstance();
		List<String> dayList=new ArrayList<String>();
		List<String> monthList=new ArrayList<String>();
		List<String> weekList=new ArrayList<String>();
		List<UnAllotAndDelayAllotChartTo> resultSet=null;
		List<UnAllotAndDelayAllotChartTo> backResultSet=new ArrayList<UnAllotAndDelayAllotChartTo>();
	   try{
		   //进入默认页面
		if(context.contextMap.get("fromDate")==null
				&&context.contextMap.get("toDate")==null){
		ChartInfo chartInfo1=new ChartInfo();
		ChartInfo chartInfo2=new ChartInfo();
		chartInfo1.chartHeight=350;
		chartInfo1.xLableFontAngle=0;
		chartInfo1.setChartTitle("已核未拨与缓拨件数统计图");
		chartInfo2.chartHeight=350;
		chartInfo2.xLableFontAngle=0;
		chartInfo2.setChartTitle("已核未拨与缓拨金额统计图");
		
		chartInfo1.setyTitle("拨款件数(单位:件)");
		chartInfo1.setChartName("count");
		chartInfo2.setyTitle("拨款金额(单位:K)");
		chartInfo2.setChartName("money");
		
		List<ChartDataSet> chartDataSet1=new ArrayList<ChartDataSet>();
		ChartDataSet dataSet1=null;
		List<Double> yData1=null;
		
		List<ChartDataSet> chartDataSet2=new ArrayList<ChartDataSet>();
		ChartDataSet dataSet2=null;
		List<Double> yData2=null;
		
		toDate=df.format(cal.getTime());
		dayList.add(toDate);
		for(int i=1;i<30;i++){
			cal.add(Calendar.DAY_OF_MONTH, -1);
			dayList.add(df.format(cal.getTime()));
			fromDate=df.format(cal.getTime());
		}
		//对日期倒序排列
		Collections.reverse(dayList);
		chartInfo1.chartWidth=dayList.size()<=30?1024:(dayList.size()/14+1)*400;
		chartInfo2.chartWidth=dayList.size()<=30?1024:(dayList.size()/14+1)*400;		
		
		chartInfo1.setxLabel(dayList.toArray(new String [0]));
		chartInfo2.setxLabel(dayList.toArray(new String [0]));
		
		//设定 查询条件
		param.put("begin", fromDate);
		param.put("end", toDate);
		List<String> dayCondition=(List<String>)DataAccessor
				.query("priceReport.getDayCondition",param,DataAccessor.RS_TYPE.LIST);
		resultSet=(List<UnAllotAndDelayAllotChartTo>)DataAccessor
				.query("priceReport.getDayChart",param,DataAccessor.RS_TYPE.LIST);
		for(UnAllotAndDelayAllotChartTo t:resultSet){
			DecimalFormat a = new DecimalFormat("#.0");
			String up=a.format(t.getUnAllot_pay());
			String dp=a.format(t.getDelayAllot_pay());
			t.setUnAllotPayWithDisplay(up);
			t.setDelayAllotPayWithDisplay(dp);
			t.setUnAllotPay(new BigDecimal(t.getUnAllot_pay()).divide(new BigDecimal(1000),4,BigDecimal.ROUND_HALF_UP));
			t.setDelayAllotPay(new BigDecimal(t.getDelayAllot_pay()).divide(new BigDecimal(1000),4,BigDecimal.ROUND_HALF_UP));
		}
		

		for(int ii=0;ii<2;ii++){
			dataSet1=new ChartDataSet();
			yData1=new ArrayList<Double>();
			
			dataSet2=new ChartDataSet();
			yData2=new ArrayList<Double>();
			if(ii==0){
				dataSet1.setTitle("未拨");
				dataSet2.setTitle("未拨");
				
				int k=0;
				for(int i=0;i<dayList.size();i++){
					
					if(!dayCondition.contains(dayList.get(i))){
					   yData1.add(new BigDecimal(0).doubleValue());
					   yData2.add(new BigDecimal(0).doubleValue());
					   backResultSet.add(i,new UnAllotAndDelayAllotChartTo(new BigDecimal(0),new BigDecimal(0),0,0,dayList.get(i),0,0,"0","0"));
					}else{
					   yData1.add((double)resultSet.get(k).getUnAllotCount());
					   yData2.add(resultSet.get(k).getUnAllotPay().doubleValue());
					   backResultSet.add(i,new UnAllotAndDelayAllotChartTo
							   (new BigDecimal(resultSet.get(k).getUnAllotPay().doubleValue()),new BigDecimal(0)
							   ,resultSet.get(k).getUnAllotCount(),0,dayList.get(i),resultSet.get(k).getUnAllot_pay(),resultSet.get(k).getDelayAllot_pay(),
							   resultSet.get(k).getUnAllotPayWithDisplay(),resultSet.get(k).getDelayAllotPayWithDisplay()));
					   k++;
					}
				  
				}
			}else{
				dataSet1.setTitle("缓拨");
				dataSet2.setTitle("缓拨");
				
				int k=0;
				for(int i=0;i<dayList.size();i++){
					if(!dayCondition.contains(dayList.get(i))){
						   yData1.add(new BigDecimal(0).doubleValue());
						   yData2.add(new BigDecimal(0).doubleValue());
						}else{
						   yData1.add((double)resultSet.get(k).getDelayAllotCount());
						   yData2.add(resultSet.get(k).getDelayAllotPay().doubleValue());
						   backResultSet.get(i).setDelayAllotCount(resultSet.get(k).getDelayAllotCount());
						   backResultSet.get(i).setDelayAllotPay(new BigDecimal(resultSet.get(k).getDelayAllotPay().doubleValue()));
						   k++;
						}
					
				}
			}
			dataSet1.setyData(yData1);
			chartDataSet1.add(dataSet1);
			
			dataSet2.setyData(yData2);
			chartDataSet2.add(dataSet2);
		}
		
		chartInfo1.setChartDataList(chartDataSet1);
		chartInfo1.setAlertTitle("{xLabel}日的拨款件数是{value}");
		chartInfo2.setChartDataList(chartDataSet2);
		chartInfo2.setAlertTitle("{xLabel}日的拨款金额是{value}K");
		
			chartResult1=ChartFactory.getLineChart(context.getRequest(), chartInfo1);
			chartResult2=ChartFactory.getLineChart(context.getRequest(), chartInfo2);
		  }else{//进入查询页面
			  if("day".equals(context.contextMap.get("dateFormat"))) {            //按天显示
					ChartInfo chartInfo1=new ChartInfo();
					ChartInfo chartInfo2=new ChartInfo();
					chartInfo1.chartHeight=350;
					chartInfo1.xLableFontAngle=0;
					chartInfo1.setChartTitle("已核未拨与缓拨件数统计图");
					chartInfo2.chartHeight=350;
					chartInfo2.xLableFontAngle=0;
					chartInfo2.setChartTitle("已核未拨与缓拨金额统计图");
					
					chartInfo1.setyTitle("拨款件数(单位:件)");
					chartInfo1.setChartName("count");
					chartInfo2.setyTitle("拨款金额(单位:K)");
					chartInfo2.setChartName("money");
					
					List<ChartDataSet> chartDataSet1=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet1=null;
					List<Double> yData1=null;
					
					List<ChartDataSet> chartDataSet2=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet2=null;
					List<Double> yData2=null;
					
					fromDate=(String)context.contextMap.get("fromDate");
					toDate=(String)context.contextMap.get("toDate");
					Date begin=df.parse(fromDate);
					Date end=df.parse(toDate);
					cal.setTime(begin);
					dayList.add(fromDate);
					while(!df.format(cal.getTime()).equals(df.format(end.getTime()))){
						cal.setTime(cal.getTime());
						cal.add(Calendar.DAY_OF_MONTH, 1);
						dayList.add(df.format(cal.getTime()));
					}
					
					chartInfo1.chartWidth=dayList.size()<=30?1024:(dayList.size()/14+1)*400;
					chartInfo2.chartWidth=dayList.size()<=30?1024:(dayList.size()/14+1)*400;		
					
					chartInfo1.setxLabel(dayList.toArray(new String [0]));
					chartInfo2.setxLabel(dayList.toArray(new String [0]));
					
					//设定 查询条件
					param.put("begin", fromDate);
					param.put("end", toDate);
					List<String> dayCondition=(List<String>)DataAccessor
							.query("priceReport.getDayCondition",param,DataAccessor.RS_TYPE.LIST);
					resultSet=(List<UnAllotAndDelayAllotChartTo>)DataAccessor
							.query("priceReport.getDayChart",param,DataAccessor.RS_TYPE.LIST);
					for(UnAllotAndDelayAllotChartTo t:resultSet){
						DecimalFormat a = new DecimalFormat("#.0");
						String up=a.format(t.getUnAllot_pay());
						String dp=a.format(t.getDelayAllot_pay());
						t.setUnAllotPayWithDisplay(up);
						t.setDelayAllotPayWithDisplay(dp);
						t.setUnAllotPay(new BigDecimal(t.getUnAllot_pay()).divide(new BigDecimal(1000),4,BigDecimal.ROUND_HALF_UP));
						t.setDelayAllotPay(new BigDecimal(t.getDelayAllot_pay()).divide(new BigDecimal(1000),4,BigDecimal.ROUND_HALF_UP));
					}
					

					for(int ii=0;ii<2;ii++){
						dataSet1=new ChartDataSet();
						yData1=new ArrayList<Double>();
						
						dataSet2=new ChartDataSet();
						yData2=new ArrayList<Double>();
						if(ii==0){
							dataSet1.setTitle("未拨");
							dataSet2.setTitle("未拨");
							
							int k=0;
							for(int i=0;i<dayList.size();i++){
								
								if(!dayCondition.contains(dayList.get(i))){
								   yData1.add(new BigDecimal(0).doubleValue());
								   yData2.add(new BigDecimal(0).doubleValue());
								   backResultSet.add(i,new UnAllotAndDelayAllotChartTo(new BigDecimal(0),new BigDecimal(0),0,0,dayList.get(i),0,0,"0","0"));
								}else{
								   yData1.add((double)resultSet.get(k).getUnAllotCount());
								   yData2.add(resultSet.get(k).getUnAllotPay().doubleValue());
								   backResultSet.add(i,new UnAllotAndDelayAllotChartTo
										   (new BigDecimal(resultSet.get(k).getUnAllotPay().doubleValue()),new BigDecimal(0)
										   ,resultSet.get(k).getUnAllotCount(),0,dayList.get(i),resultSet.get(k).getUnAllot_pay(),resultSet.get(k).getDelayAllot_pay()
										   ,resultSet.get(k).getUnAllotPayWithDisplay(),resultSet.get(k).getDelayAllotPayWithDisplay()));
								   k++;
								}
							  
							}
						}else{
							dataSet1.setTitle("缓拨");
							dataSet2.setTitle("缓拨");
							
							int k=0;
							for(int i=0;i<dayList.size();i++){
								if(!dayCondition.contains(dayList.get(i))){
									   yData1.add(new BigDecimal(0).doubleValue());
									   yData2.add(new BigDecimal(0).doubleValue());
									}else{
									   yData1.add((double)resultSet.get(k).getDelayAllotCount());
									   yData2.add(resultSet.get(k).getDelayAllotPay().doubleValue());
									   backResultSet.get(i).setDelayAllotCount(resultSet.get(k).getDelayAllotCount());
									   backResultSet.get(i).setDelayAllotPay(new BigDecimal(resultSet.get(k).getDelayAllotPay().doubleValue()));
									   k++;
									}
								
							}
						}
						dataSet1.setyData(yData1);
						chartDataSet1.add(dataSet1);
						
						dataSet2.setyData(yData2);
						chartDataSet2.add(dataSet2);
					}
					
					chartInfo1.setChartDataList(chartDataSet1);
					chartInfo1.setAlertTitle("{xLabel}日的拨款件数是{value}");
					chartInfo2.setChartDataList(chartDataSet2);
					chartInfo2.setAlertTitle("{xLabel}日的拨款金额是{value}K");
					
						chartResult1=ChartFactory.getLineChart(context.getRequest(), chartInfo1);
						chartResult2=ChartFactory.getLineChart(context.getRequest(), chartInfo2);
			  }else if("week".equals(context.contextMap.get("dateFormat"))){                  //按周显示图表
					ChartInfo chartInfo1=new ChartInfo();
					ChartInfo chartInfo2=new ChartInfo();
					chartInfo1.chartHeight=350;
					chartInfo1.xLableFontAngle=0;
					chartInfo1.setChartTitle("已核未拨与缓拨件数统计图");
					chartInfo2.chartHeight=350;
					chartInfo2.xLableFontAngle=0;
					chartInfo2.setChartTitle("已核未拨与缓拨金额统计图");
					
					chartInfo1.setyTitle("拨款件数(单位:件)");
					chartInfo1.setChartName("count");
					chartInfo2.setyTitle("拨款金额(单位:K)");
					chartInfo2.setChartName("money");
					
					List<ChartDataSet> chartDataSet1=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet1=null;
					List<Double> yData1=null;
					
					List<ChartDataSet> chartDataSet2=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet2=null;
					List<Double> yData2=null;
					
					fromDate=(String)context.contextMap.get("fromDate");
					toDate=(String)context.contextMap.get("toDate");
					Date begin=df.parse(fromDate);
					Date end=df.parse(toDate);
					weekList=getWeekList(fromDate, toDate);
					
					chartInfo1.chartWidth=weekList.size()<=30?1024:(weekList.size()/14+1)*400;
					chartInfo2.chartWidth=weekList.size()<=30?1024:(weekList.size()/14+1)*400;		
					
					chartInfo1.setxLabel(weekList.toArray(new String [0]));
					chartInfo2.setxLabel(weekList.toArray(new String [0]));
					
					List<Date> newWeekList=new ArrayList<Date>();
					for(int i=0;i<weekList.size();i++){
						newWeekList.add(df.parse(weekList.get(i)));
					}
					param.put("weekList", newWeekList);
					
					List<String> weekCondition=(List<String>)DataAccessor
							.query("priceReport.getWeekCondition",param,DataAccessor.RS_TYPE.LIST);
					resultSet=(List<UnAllotAndDelayAllotChartTo>)DataAccessor
							.query("priceReport.getWeekChart",param,DataAccessor.RS_TYPE.LIST);
					
					for(UnAllotAndDelayAllotChartTo t:resultSet){
						DecimalFormat a = new DecimalFormat("#.0");
						String up=a.format(t.getUnAllot_pay());
						String dp=a.format(t.getDelayAllot_pay());
						t.setUnAllotPayWithDisplay(up);
						t.setDelayAllotPayWithDisplay(dp);
						t.setUnAllotPay(new BigDecimal(t.getUnAllot_pay()).divide(new BigDecimal(1000),4,BigDecimal.ROUND_HALF_UP));
						t.setDelayAllotPay(new BigDecimal(t.getDelayAllot_pay()).divide(new BigDecimal(1000),4,BigDecimal.ROUND_HALF_UP));
					}
					

					for(int ii=0;ii<2;ii++){
						dataSet1=new ChartDataSet();
						yData1=new ArrayList<Double>();
						
						dataSet2=new ChartDataSet();
						yData2=new ArrayList<Double>();
						if(ii==0){
							dataSet1.setTitle("未拨");
							dataSet2.setTitle("未拨");
							
							int k=0;
							for(int i=0;i<weekList.size();i++){
								
								if(!weekCondition.contains(weekList.get(i))){
								   yData1.add(new BigDecimal(0).doubleValue());
								   yData2.add(new BigDecimal(0).doubleValue());
								   backResultSet.add(i,new UnAllotAndDelayAllotChartTo(new BigDecimal(0),new BigDecimal(0),0,0,weekList.get(i),0,0,"0","0"));
								}else{
								   yData1.add((double)resultSet.get(k).getUnAllotCount());
								   yData2.add(resultSet.get(k).getUnAllotPay().doubleValue());
								   backResultSet.add(i,new UnAllotAndDelayAllotChartTo
										   (new BigDecimal(resultSet.get(k).getUnAllotPay().doubleValue()),new BigDecimal(0)
										   ,resultSet.get(k).getUnAllotCount(),0,weekList.get(i),resultSet.get(k).getUnAllot_pay(),resultSet.get(k).getDelayAllot_pay()
										   ,resultSet.get(k).getUnAllotPayWithDisplay(),resultSet.get(k).getDelayAllotPayWithDisplay()));
								   k++;
								}
							  
							}
						}else{
							dataSet1.setTitle("缓拨");
							dataSet2.setTitle("缓拨");
							
							int k=0;
							for(int i=0;i<weekList.size();i++){
								if(!weekCondition.contains(weekList.get(i))){
									   yData1.add(new BigDecimal(0).doubleValue());
									   yData2.add(new BigDecimal(0).doubleValue());
									}else{
									   yData1.add((double)resultSet.get(k).getDelayAllotCount());
									   yData2.add(resultSet.get(k).getDelayAllotPay().doubleValue());
									   backResultSet.get(i).setDelayAllotCount(resultSet.get(k).getDelayAllotCount());
									   backResultSet.get(i).setDelayAllotPay(new BigDecimal(resultSet.get(k).getDelayAllotPay().doubleValue()));
									   k++;
									}
								
							}
						}
						dataSet1.setyData(yData1);
						chartDataSet1.add(dataSet1);
						
						dataSet2.setyData(yData2);
						chartDataSet2.add(dataSet2);
					}
					
					chartInfo1.setChartDataList(chartDataSet1);
					chartInfo1.setAlertTitle("{xLabel}日的拨款件数是{value}");
					chartInfo2.setChartDataList(chartDataSet2);
					chartInfo2.setAlertTitle("{xLabel}日的拨款金额是{value}K");
					
						chartResult1=ChartFactory.getLineChart(context.getRequest(), chartInfo1);
						chartResult2=ChartFactory.getLineChart(context.getRequest(), chartInfo2);
					
			  }else if("month".equals(context.contextMap.get("dateFormat"))){                 //按月显示图表
					ChartInfo chartInfo1=new ChartInfo();
					ChartInfo chartInfo2=new ChartInfo();
					chartInfo1.chartHeight=350;
					chartInfo1.xLableFontAngle=0;
					chartInfo1.setChartTitle("已核未拨与缓拨件数统计图");
					chartInfo2.chartHeight=350;
					chartInfo2.xLableFontAngle=0;
					chartInfo2.setChartTitle("已核未拨与缓拨金额统计图");
					
					chartInfo1.setyTitle("拨款件数(单位:件)");
					chartInfo1.setChartName("count");
					chartInfo2.setyTitle("拨款金额(单位:K)");
					chartInfo2.setChartName("money");
					
					List<ChartDataSet> chartDataSet1=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet1=null;
					List<Double> yData1=null;
					
					List<ChartDataSet> chartDataSet2=new ArrayList<ChartDataSet>();
					ChartDataSet dataSet2=null;
					List<Double> yData2=null;
					
					fromDate=(String)context.contextMap.get("fromDate");
					toDate=(String)context.contextMap.get("toDate");
					Date begin=df.parse(fromDate);
					Date end=df.parse(toDate);
					monthList=getMonthList(fromDate, toDate);
					
					chartInfo1.chartWidth=monthList.size()<=30?1024:(monthList.size()/14+1)*400;
					chartInfo2.chartWidth=monthList.size()<=30?1024:(monthList.size()/14+1)*400;		
					
					chartInfo1.setxLabel(monthList.toArray(new String [0]));
					chartInfo2.setxLabel(monthList.toArray(new String [0]));
					
					List<Date> newMonthList=new ArrayList<Date>();
					for(int i=0;i<monthList.size();i++){
						newMonthList.add(df.parse(monthList.get(i)));
					}
					param.put("monthList", newMonthList);
					
					List<String> monthCondition=(List<String>)DataAccessor
							.query("priceReport.getMonthCondition",param,DataAccessor.RS_TYPE.LIST);
					resultSet=(List<UnAllotAndDelayAllotChartTo>)DataAccessor
							.query("priceReport.getMonthChart",param,DataAccessor.RS_TYPE.LIST);
					
					for(UnAllotAndDelayAllotChartTo t:resultSet){
						DecimalFormat a = new DecimalFormat("#.0");
						String up=a.format(t.getUnAllot_pay());
						String dp=a.format(t.getDelayAllot_pay());
						t.setUnAllotPayWithDisplay(up);
						t.setDelayAllotPayWithDisplay(dp);
						t.setUnAllotPay(new BigDecimal(t.getUnAllot_pay()).divide(new BigDecimal(1000),4,BigDecimal.ROUND_HALF_UP));
						t.setDelayAllotPay(new BigDecimal(t.getDelayAllot_pay()).divide(new BigDecimal(1000),4,BigDecimal.ROUND_HALF_UP));
					}
					

					for(int ii=0;ii<2;ii++){
						dataSet1=new ChartDataSet();
						yData1=new ArrayList<Double>();
						
						dataSet2=new ChartDataSet();
						yData2=new ArrayList<Double>();
						if(ii==0){
							dataSet1.setTitle("未拨");
							dataSet2.setTitle("未拨");
							
							int k=0;
							for(int i=0;i<monthList.size();i++){
								
								if(!monthCondition.contains(monthList.get(i))){
								   yData1.add(new BigDecimal(0).doubleValue());
								   yData2.add(new BigDecimal(0).doubleValue());
								   backResultSet.add(i,new UnAllotAndDelayAllotChartTo(new BigDecimal(0),new BigDecimal(0),0,0,monthList.get(i),0,0,"0","0"));
								}else{
								   yData1.add((double)resultSet.get(k).getUnAllotCount());
								   yData2.add(resultSet.get(k).getUnAllotPay().doubleValue());
								   backResultSet.add(i,new UnAllotAndDelayAllotChartTo
										   (new BigDecimal(resultSet.get(k).getUnAllotPay().doubleValue()),new BigDecimal(0)
										   ,resultSet.get(k).getUnAllotCount(),0,dayList.get(i),resultSet.get(k).getUnAllot_pay(),resultSet.get(k).getDelayAllot_pay()
										   ,resultSet.get(k).getUnAllotPayWithDisplay(),resultSet.get(k).getDelayAllotPayWithDisplay()));
								   k++;
								}
							  
							}
						}else{
							dataSet1.setTitle("缓拨");
							dataSet2.setTitle("缓拨");
							
							int k=0;
							for(int i=0;i<monthList.size();i++){
								if(!monthCondition.contains(monthList.get(i))){
									   yData1.add(new BigDecimal(0).doubleValue());
									   yData2.add(new BigDecimal(0).doubleValue());
									}else{
									   yData1.add((double)resultSet.get(k).getDelayAllotCount());
									   yData2.add(resultSet.get(k).getDelayAllotPay().doubleValue());
									   backResultSet.get(i).setDelayAllotCount(resultSet.get(k).getDelayAllotCount());
									   backResultSet.get(i).setDelayAllotPay(new BigDecimal(resultSet.get(k).getDelayAllotPay().doubleValue()));
									   k++;
									}
								
							}
						}
						dataSet1.setyData(yData1);
						chartDataSet1.add(dataSet1);
						
						dataSet2.setyData(yData2);
						chartDataSet2.add(dataSet2);
					}
					
					chartInfo1.setChartDataList(chartDataSet1);
					chartInfo1.setAlertTitle("{xLabel}日的拨款件数是{value}");
					chartInfo2.setChartDataList(chartDataSet2);
					chartInfo2.setAlertTitle("{xLabel}日的拨款金额是{value}K");
					
						chartResult1=ChartFactory.getLineChart(context.getRequest(), chartInfo1);
						chartResult2=ChartFactory.getLineChart(context.getRequest(), chartInfo2);
			  }
		  }

		} catch (Exception e) {
			logger.debug("生成图表出错");
			context.errList.add("生成图表出错");
			Output.errorPageOutput(e,context);
		}
		if(context.errList.isEmpty()) {
			outputMap.put("dateFormat",context.contextMap.get("dateFormat")==null?"day":context.contextMap.get("dateFormat"));
			outputMap.put("result", backResultSet);
			outputMap.put("size", backResultSet.size());
			outputMap.put("fromDate", fromDate);
			outputMap.put("toDate", toDate);
			outputMap.put("backDate", df.format(backcal.getTime()));
			outputMap.put("chartResult1", chartResult1);
			outputMap.put("chartResult2", chartResult2);
			Output.jspOutput(outputMap, context, "/report/unAllotAndDelayAllotChart.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	//获得月列表(每个月的最后一天)
	public List<String> getMonthList(String fromDate,String toDate){
        Calendar cal=Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        List<String> list=new ArrayList<String>();
        try{
        	Date end=df.parse(toDate);
        	int monWithEnd=df.parse(toDate).getMonth();
        	int monWithbegin=df.parse(fromDate).getMonth();
        	cal.setTime(df.parse(fromDate));
    		if(monWithEnd==monWithbegin                                         //表示同一个月
    				&&(df.parse(toDate).getDate())!=cal.getActualMaximum(Calendar.DATE)
    				&&(df.parse(toDate).getDate())<cal.getActualMaximum(Calendar.DATE)){
    			list.add(df.format(cal.getTime()));
    		}else{
    			list.add(fromDate);
        		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));  
        		list.add(df.format(cal.getTime()));
    		}
        	while(cal.getTime().before(end)||cal.getTime().equals(end)){
            	cal.set(Calendar.DATE, 1);
            	cal.add(Calendar.MONTH, 1);
            	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); 
               if(cal.getTime().before(end)||cal.getTime().equals(end)){
            	   list.add(df.format(cal.getTime()));
               }
        	}
        }catch(Exception er){
        	er.printStackTrace();
        }
        return list;
	}
	
	//获取周列表
	public List<String> getWeekList(String fromDate,String toDate){
        Calendar cal=Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        List<String> weekList=new ArrayList<String>();
        try{
        	Date begin=df.parse(fromDate);
        	Date end=df.parse(toDate);
        	cal.setTime(begin);
            while(cal.getTime().before(end)||cal.getTime().equals(end)){
            	switch(cal.get(Calendar.DAY_OF_WEEK)){
            	case 1:
            		weekList.add(df.format(cal.getTime()));
            		cal.add(Calendar.DATE, 5);
            		break;
            	case 2:
            		weekList.add(df.format(cal.getTime()));
            		cal.add(Calendar.DATE, 4);
            		break;
            	case 3:
            		weekList.add(df.format(cal.getTime()));
            		cal.add(Calendar.DATE, 3);
            		break;
            	case 4:
            		weekList.add(df.format(cal.getTime()));
            		cal.add(Calendar.DATE, 2);
            		break;
            	case 5:
            		weekList.add(df.format(cal.getTime()));
            		cal.add(Calendar.DATE, 1);
            		break;
            	case 6:
            		weekList.add(df.format(cal.getTime()));
            		cal.add(Calendar.DATE, 7);
            		break;
            	case 7:
            		weekList.add(df.format(cal.getTime()));
            		cal.add(Calendar.DATE, 6);
            		break;
            	}
            }
        }catch(Exception er){
        	er.printStackTrace();
        }
        return weekList;
	}
	
}
