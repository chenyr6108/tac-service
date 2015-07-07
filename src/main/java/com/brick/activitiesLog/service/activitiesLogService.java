package com.brick.activitiesLog.service;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.report.service.InsuranceUtil;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class activitiesLogService extends AService
{
	Log logger = LogFactory.getLog(this.getClass());
	//查询所有客户
	public void query(Context context) {

		//super.commonQuery("customer.query", context, DataAccessor.RS_TYPE.PAGED, Output.OUTPUT_TYPE.JSP, "/customer/query.jsp");

		Map outputMap = new HashMap();
		DataWrap dw = null;
		List companyList = null;
		List provinces = null;
		List logTypeList=null;
		List casesunType=null;//当搜索时查询出选中的状况
		List cityType=null;//查询出所有城市
		List custLevel = null ;//承租人类型 数据字典中查出
		String type="案件状况分类";
		context.contextMap.put("type", type);
		
		String decp_id="";
		String province_id="";
		String city_id="";
		String sorUserId="";
		String casesun="";
		String QSTART_DATE="";
		String QEND_DATE="";
		String caseFather="";
	
		
		decp_id=context.request.getParameter("decp_id");
		
		province_id=context.request.getParameter("province_id");
		
		city_id=context.request.getParameter("city_id");
		
		sorUserId=context.request.getParameter("sorUserId");
		
		casesun=context.request.getParameter("casesun");
		
		caseFather=context.request.getParameter("caseFather");
		
		QSTART_DATE=context.request.getParameter("QSTART_DATE");
		
		QEND_DATE=context.request.getParameter("QEND_DATE");
		

		/*-------- data access --------*/
		try {
			companyList = (List) DataAccessor.query("companyManage.queryCompanyAlias", null, DataAccessor.RS_TYPE.LIST);
			logTypeList=(List) DataAccessor.query("activitiesLog.logTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			provinces = (List) DataAccessor.query("area.getProvinces",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(("".equals(decp_id) || decp_id==null) && ("".equals(province_id) || province_id==null) && ("".equals(city_id) || city_id==null) && ("".equals(sorUserId) || sorUserId==null) && ("".equals(casesun) || casesun==null) && ("".equals(QSTART_DATE) || QSTART_DATE==null) && ("".equals(QEND_DATE) || QEND_DATE==null))
			{
				dw = (DataWrap) DataAccessor.query("activitiesLog.query", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			}
			else
			{
				//查询出具体的市
				if(!"".equals(province_id) && province_id!=null)
				{
					Map city=new HashMap();
					city.put("provinceId", context.contextMap.get("province_id"));
					cityType=(List)DataAccessor.query("area.getCitysByProvinceId", city, DataAccessor.RS_TYPE.LIST);
				}
				//查询出具体的案件状况
				if(!"".equals(caseFather) && caseFather!=null)
				{
					casesunType=(List)DataAccessor.query("activitiesLog.logreportbycode", context.contextMap, DataAccessor.RS_TYPE.LIST);
				}
			
				Map logName=null;
				String actLogName=null;
				if(context.contextMap.get("casesun")!=null && !"".equals(context.contextMap.get("casesun")))
				{
					try{
						context.contextMap.put("reportactlogid", context.contextMap.get("casesun"));
						logName=(Map)DataAccessor.query("activitiesLog.getActlogNameByActlogId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					}
					catch(Exception e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
						//添加详细错误信息
					}
					Object actLogName1=logName.get("ACTLOG_NAME");
					if(actLogName1!=null && !"".equals(actLogName1))
					{
						actLogName=actLogName1.toString();
					}
				}
				
				context.contextMap.put("logName", actLogName);
				dw = (DataWrap) DataAccessor.query("activitiesLog.queryList", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			}
			// 数据字典 承租人级别
			context.contextMap.put("dataType", "承租人级别");
			custLevel = (List) DataAccessor.query("customer.getCustLevelDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("province_id",  context.contextMap.get("province_id"));
		outputMap.put("city_id",  context.contextMap.get("city_id"));
		outputMap.put("sorUserId", context.contextMap.get("sorUserId"));
		outputMap.put("casesun",  context.contextMap.get("casesun"));
		outputMap.put("QSTART_DATE",  context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE",  context.contextMap.get("QEND_DATE"));
		outputMap.put("caseFather",context.contextMap.get("caseFather"));
		outputMap.put("decp_id",  context.contextMap.get("decp_id"));
		outputMap.put("searchValue", context.contextMap.get("searchValue"));
		outputMap.put("cust_type", context.contextMap.get("cust_type"));
		outputMap.put("cityType", cityType);
		outputMap.put("casesunType", casesunType);
		outputMap.put("companyList", companyList);
		outputMap.put("provinces", provinces);
		outputMap.put("logTypeList", logTypeList);
		outputMap.put("custLevel", custLevel);
		outputMap.put("content", context.contextMap.get("content"));

		Output.jspOutput(outputMap, context, "/activitiesLog/activitiesCustmorManager.jsp");

	}
	
	
	//为新建主档提供信息
	public void createFilesShow(Context context)
	{
		Map outputMap = new HashMap();
		Map customer=new HashMap();
		List errList = context.errList ;
		try {
			customer = (Map) DataAccessor.query("customer.readInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			//添加详细错误信息
			errList.add("活动日志管理--新建文档初始化错误!请联系管理员") ;
		}
		//判断是否出错后分别执行
		if(errList.isEmpty()){
			customer.put("ID", context.request.getSession().getAttribute("s_employeeId"));
			customer.put("USERNAME",context.request.getSession().getAttribute("s_employeeName"));
			outputMap.put("customer", customer);
			Output.jspOutput(outputMap, context, "/activitiesLog/createFiles.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
	}
	
	//新建主档
	public void createFiles(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;	
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("activitiesLog.createActivitiesLog", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				//添加详细错误信息
				errList.add("活动日志管理--新建文档错误!请联系管理员");
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=activitiesLog.query");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	//查询出承租人下的所有主档
	public void activitiesLogByCustId(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;	
		List dw = null;
		if(errList.isEmpty())
		{
			try {
				dw = (List) DataAccessor.query("activitiesLog.activitiesLogByCustId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				//添加详细错误信息
				errList.add("活动日志管理--查询承租人所有主档错误!请联系管理员") ;
			}
		}
		
		//判断是否出错后分别执行
		if(errList.isEmpty())
		{
			outputMap.put("dw", dw);
			Output.jspOutput(outputMap, context, "/activitiesLog/activitiesCustmorDetil.jsp");
		}
		else
		{	
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
	}
	
	
	
	
	//查询出一个主档下的所有明细档
	public void activitiesLogByLog(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;	
		List dw = null;
		if(errList.isEmpty())
		{
			try {
				dw = (List) DataAccessor.query("activitiesLog.activitiesLogByLog", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				//添加详细错误信息
				errList.add("活动日志管理--查询明细错误!请联系管理员") ;  
			}
		}
		
		
		if(errList.isEmpty())
		{
			outputMap.put("dw", dw);
			Output.jspOutput(outputMap, context, "/activitiesLog/activitiesLogDetil.jsp");
		}
		else
		{	
			outputMap.put("errList", errList);
			//错误后跳入错误页
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
	}
	
	
	//为新建明细提供信息
	public void createLogShow(Context context)
	{
		Map outputMap = new HashMap();
		Map customers=new HashMap();
		Map activitiesEntity=new HashMap();
		//添加errList
		List errList = context.errList ;
		String actilog=context.request.getParameter("actilog");
		
		//查询所有活动日志类别
		String type="活动日志";
		String type1="案件状况分类";
		List logTypeList=null;
		List logTypeList1=null;
		context.contextMap.put("type", type);
		context.contextMap.put("type1", type1);
		try {
			customers = (Map) DataAccessor.query("customer.readInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
			logTypeList=(List) DataAccessor.query("activitiesLog.logTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			//logTypeList1=(List) DataAccessor.query("activitiesLog.logTypeList1", context.contextMap, DataAccessor.RS_TYPE.LIST);
			logTypeList1=(List) DataAccessor.query("activitiesLog.logTypeList2", context.contextMap, DataAccessor.RS_TYPE.LIST);
			activitiesEntity=(Map)DataAccessor.query("activitiesLog.readActivitiesInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			//添加详细错误信息
			errList.add("活动日志管理--新建明细初始化错误!请联系管理员") ;
		}
		//判断是否出错后分别执行
		if(errList.isEmpty()){
			outputMap.put("ID", context.request.getSession().getAttribute("s_employeeId"));
			outputMap.put("USERNAME", context.request.getSession().getAttribute("s_employeeName"));
			outputMap.put("actilog",actilog);
			outputMap.put("customer",customers);
			outputMap.put("activitiesEntity",activitiesEntity);
			outputMap.put("logTypeList", logTypeList);
			outputMap.put("logTypeList1", logTypeList1);
			Output.jspOutput(outputMap, context, "/activitiesLog/createLog.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	//查询案件狀況細項
	public void getLogType(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;	
		List logSunList=null;
		
		if(errList.isEmpty())
		{
			try {
				logSunList=(List) DataAccessor.query("activitiesLog.logSunList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				//添加详细错误信息
				errList.add("活动日志管理--查询案件状况细项错误!请联系管理员") ;
			}
		}
		
		if(errList.isEmpty())
		{
			outputMap.put("logSunList", logSunList);
			Output.jsonOutput(outputMap, context);
		}
		else
		{
			outputMap.put("errList", errList);
		}
	}
	
	//查询客户拜访与维护
	public void getLogType1(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;	
		List logSunList1=null;
		
		if(errList.isEmpty())
		{
			try {
				logSunList1=(List) DataAccessor.query("activitiesLog.logSunList1", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				//添加详细错误信息
				errList.add("活动日志管理--查询案件状况细项错误!请联系管理员") ;
			}
		}
		
		if(errList.isEmpty())
		{
			outputMap.put("logSunList1", logSunList1);
			Output.jsonOutput(outputMap, context);
		}
		else
		{
			outputMap.put("errList", errList);
		}
	}
	
	//新建主档明细
	public void createLog(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;	
		
		//查询出在数据字典中的案件狀況細項下的提案的主键
		
		String prvLogSun1="已访厂";
		String prvLogSun2="首次拜访";
		
		
		
		int casesun=Integer.parseInt(context.contextMap.get("casesun").toString());
		//通过casesun（明细档Id），查询出name
		Map logName=null;
		try{
			context.contextMap.put("reportactlogid", context.contextMap.get("casesun"));
			logName=(Map)DataAccessor.query("activitiesLog.getActlogNameByActlogId", context.contextMap, DataAccessor.RS_TYPE.MAP);
		}
		catch(Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			//添加详细错误信息
			errList.add("活动日志管理--新建主档明细错误!请联系管理员");
		}
		Object actLogName1=logName.get("ACTLOG_NAME");
		String actLogName=null;
		if(actLogName1!=null && !"".equals(actLogName1))
		{
			actLogName=actLogName1.toString();
		}
		
		int casesun1=Integer.parseInt(context.request.getParameter("CASESUN1"));
		
		SqlMapClient sqlMapper=null;
		if(errList.isEmpty()) {
			try {
				sqlMapper=DataAccessor.getSession();
				sqlMapper.startTransaction();
				context.contextMap.put("logName", actLogName);
				sqlMapper.insert("activitiesLog.createLog", context.contextMap);
				//DataAccessor.execute("activitiesLog.createLog", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				
				//System.out.println("actLogName11111111111="+actLogName);
				if(actLogName.equals(prvLogSun1))
				{
					sqlMapper.update("activitiesLog.updateCaseStateByVisitFactoryDate", context.contextMap);
				}
				else if(actLogName.equals(prvLogSun2))
				{
					sqlMapper.update("activitiesLog.updateCaseStateByVisitFirst", context.contextMap);
				}
				else
				{
					sqlMapper.update("activitiesLog.updateCaseState", context.contextMap);
				}
				
				
				
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("活动日志管理--新建主档明细错误!请联系管理员");
			}
			finally { 
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) { 
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add("活动日志管理--新建主档明细错误!请联系管理员") ;
				} 
			}
		}
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=activitiesLog.query");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	//根据Id查询出具体一条主档
	public void showActitFiles(Context context)
	{
		Map outputMap = new HashMap();
		Map activitiesLog=new HashMap();
		int num=Integer.parseInt(context.request.getParameter("num"));
		List errList = context.errList ;
		try {
			
			activitiesLog=(Map) DataAccessor.query("activitiesLog.readActivitiesInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("活动日志管理--查询主档错误!请联系管理员") ;
		}
		
		if(errList.isEmpty()){
			outputMap.put("ID", context.request.getSession().getAttribute("s_employeeId"));
			outputMap.put("USERNAME", context.request.getSession().getAttribute("s_employeeName"));
			outputMap.put("activitiesLog",activitiesLog);
			if(num==0)
			{
				Output.jspOutput(outputMap, context, "/activitiesLog/showFiles.jsp");
			}
			else if(num==1)
			{
				Output.jspOutput(outputMap, context, "/activitiesLog/updateFiles.jsp");
			}
		} else {
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	//修改主档
	public void updateFiles(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;		
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("activitiesLog.updateFiles", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("活动日志管理--修改文档错误! 请联系管理员");
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=activitiesLog.query");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//删除主档
	public void deleteFiles(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;		
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("activitiesLog.deteleFiles", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("活动日志管理--删除文档错误!请联系管理员");
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=activitiesLog.query");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//删除主档明细
	public void deleteLog(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;		
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("activitiesLog.deteleLog", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=activitiesLog.query");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//查询出主档下的一个明细档
	public void LogByDet(Context context)
	{
		Map outputMap = new HashMap();
		Map LogByDet=new HashMap();
		int num=Integer.parseInt(context.request.getParameter("num"));
		String type="活动日志";
		String type1="案件状况分类";
		List logTypeList=null;
		List logTypeList1=null;
		context.contextMap.put("type", type);
		context.contextMap.put("type1", type1);
		List errList = context.errList ;
		try {
			
			LogByDet=(Map) DataAccessor.query("activitiesLog.LogByDet", context.contextMap, DataAccessor.RS_TYPE.MAP);
			logTypeList=(List) DataAccessor.query("activitiesLog.logTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			//logTypeList1=(List) DataAccessor.query("activitiesLog.logTypeList1", context.contextMap, DataAccessor.RS_TYPE.LIST);
			logTypeList1=(List) DataAccessor.query("activitiesLog.logTypeList2", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("活动日志管理--查询明细档错误!请联系管理员") ;
		}
		
		if(errList.isEmpty()){
			outputMap.put("ID", context.request.getSession().getAttribute("s_employeeId"));
			outputMap.put("USERNAME", context.request.getSession().getAttribute("s_employeeName"));
			outputMap.put("LogByDet",LogByDet);
			outputMap.put("logTypeList",logTypeList);
			outputMap.put("logTypeList1", logTypeList1);
			if(num==0)
			{
				Output.jspOutput(outputMap, context, "/activitiesLog/showLog.jsp");
			}
			else if(num==1)
			{
				Output.jspOutput(outputMap, context, "/activitiesLog/updateLog.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
		
	}
	
	//修改主档明细
	public void updateLog(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;	
		
		
		String prvLogSun1="已访厂";
		String prvLogSun2="首次拜访";
		
		int casesun=Integer.parseInt(context.request.getParameter("casesun"));
		//通过casesun（明细档Id），查询出name
		Map logName=null;
		try{
			context.contextMap.put("reportactlogid", context.contextMap.get("casesun"));
			logName=(Map)DataAccessor.query("activitiesLog.getActlogNameByActlogId", context.contextMap, DataAccessor.RS_TYPE.MAP);
		}
		catch(Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			//添加详细错误信息
			errList.add("活动日志管理--修改主档明细错误!请联系管理员");
		}
		Object actLogName1=logName.get("ACTLOG_NAME");
		String actLogName=null;
		if(actLogName1!=null && !"".equals(actLogName1))
		{
			actLogName=actLogName1.toString();
		}
		int casesun1=Integer.parseInt(context.request.getParameter("CASESUN1"));
		
		SqlMapClient sqlMapper=null;
		if(errList.isEmpty()) {
			try {
				sqlMapper=DataAccessor.getSession();
				sqlMapper.startTransaction();
				sqlMapper.update("activitiesLog.updateLog", context.contextMap);
				context.contextMap.put("logName", actLogName);
				if(actLogName.equals(prvLogSun1))
				{
					sqlMapper.update("activitiesLog.updateCaseStateByVisitFactoryDate", context.contextMap);
				}
				else if(actLogName.equals(prvLogSun2))
				{
					sqlMapper.update("activitiesLog.updateCaseStateByVisitFirst", context.contextMap);
				}
				else
				{
					sqlMapper.update("activitiesLog.updateCaseState", context.contextMap);
				}
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("活动日志管理--修改主档明细错误!请联系管理员");
			}
			finally { 
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) { 
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} 
			}
		}
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=activitiesLog.query");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
		
	}

	
	
	public void activitiesStatisitce(Context context)
	{
		List errorList = context.errList;
		HashMap  outputMap  = new HashMap();
		ArrayList   empInfo = new ArrayList();	 
		ArrayList   empOther = new ArrayList();	 
		ArrayList   company_count = new ArrayList();	//公司 数
		ArrayList   user_count = new ArrayList();	//公司 的旗下员工数
		if(errorList.isEmpty()){
		    
		    try {
//				context.contextMap.put("job_type", "业务员");
//				context.contextMap.put("type1", "勘厂");
//				context.contextMap.put("type2", "客户拜访与维护");
//				context.contextMap.put("type3", "已报价");
//				context.contextMap.put("type4", "入保证金");
				
				String Query_DATE=context.request.getParameter("Query_DATE");
				String decp_id=context.request.getParameter("decp_Id");
				String Id=context.request.getParameter("Id");
				
				if((Query_DATE==null || "".equals(Query_DATE)) && (decp_id==null || "".equals(decp_id)) && (Id==null || "".equals(Id)))
				{
					
					empInfo =   (ArrayList) DataAccessor.getSession().queryForList("activitiesLog.activitiesStatistics",context.contextMap);
					company_count =   (ArrayList)(ArrayList) DataAccessor.getSession().queryForList("activitiesLog.activitiesStatisticsCountss", context.contextMap);
				}
				else
				{
					
					empInfo =   (ArrayList) DataAccessor.getSession().queryForList("activitiesLog.activitiesStatisticsByDate",context.contextMap);
					company_count =   (ArrayList)(ArrayList) DataAccessor.getSession().queryForList("activitiesLog.activitiesStatisticsCounts", context.contextMap);
				}
				
				//加入HAB的金额
				List<Map> habAmount=null;
	        	//获得首次拜访的统计数 add by ShenQi see mantis 24
				List<Map> countOfFirstAccess=null;
				if(empInfo.size()!=0) {
					countOfFirstAccess=DataAccessor.getSession().queryForList("activitiesLog.getCountOfFirstAccess",context.contextMap);
					context.contextMap.put("DATE",Query_DATE);
					habAmount=DataAccessor.getSession().queryForList("activitiesLog.getHABAmount",context.contextMap);
				}
				for(int j=0;j<empInfo.size();j++) {
					for(int i=0;countOfFirstAccess!=null&&i<countOfFirstAccess.size();i++) {
						if(((Map)empInfo.get(j)).get("USER_ID").equals(countOfFirstAccess.get(i).get("USER_ID"))&&
								((Map)empInfo.get(j)).get("CREATE_DATE").equals(countOfFirstAccess.get(i).get("CREATE_DATE"))) {
							((Map)empInfo.get(j)).put("TOTAL",countOfFirstAccess.get(i).get("TOTAL"));
							break;
						}
					}
					
					for(int i=0;habAmount!=null&&i<habAmount.size();i++) {
						if(String.valueOf(((Map)empInfo.get(j)).get("USER_ID")).equals(String.valueOf(habAmount.get(i).get("SENSOR_ID")))) {
							if("H".equals(habAmount.get(i).get("HAB"))) {
								((Map)empInfo.get(j)).put("H",habAmount.get(i).get("AMOUNT"));
							} else if("A".equals(habAmount.get(i).get("HAB"))) {
								((Map)empInfo.get(j)).put("A",habAmount.get(i).get("AMOUNT"));
							} else if("B".equals(habAmount.get(i).get("HAB"))) {
								((Map)empInfo.get(j)).put("B",habAmount.get(i).get("AMOUNT"));
							}
						}
					}
				}
		    } catch (SQLException e) {
			
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errorList.add(e);
		    }
		}
		
		if(errorList.isEmpty()){
			
			if(context.contextMap.get("Query_DATE")!=null && !"".equals(context.contextMap.get("Query_DATE")))
			{
				outputMap.put("Query_DATE", context.contextMap.get("Query_DATE"));
			}
			else
			{
				Date time=new Date();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				
				String yar=sf.format(time).toString();
				outputMap.put("Query_DATE", yar);
			}
		    outputMap.put("comp", empInfo);
		    outputMap.put("company_count", company_count);
		    Output.jspOutput(outputMap, context, "/activitiesLog/activitiesStatisticsView.jsp");
		}
	}
	
	
	
	public void activitiesStatisitceExcel(Context context)
	{
		List errorList = context.errList;
		HashMap  outputMap  = new HashMap();
		ArrayList   empInfo = new ArrayList();	 
		ArrayList   empOther = new ArrayList();	 
		ArrayList   company_count = new ArrayList();	//公司 数
		ArrayList   user_count = new ArrayList();	//公司 的旗下员工数
		String Query_DATE=null;
		if(errorList.isEmpty()){
		    
		    try {
//				context.contextMap.put("job_type", "业务员");
//				context.contextMap.put("type1", "勘厂");
//				context.contextMap.put("type2", "客户拜访与维护");
//				context.contextMap.put("type3", "已报价");
//				context.contextMap.put("type4", "入保证金");
				
				Query_DATE=context.request.getParameter("Query_DATE");
				String decp_id=context.request.getParameter("decp_Id");
				String Id=context.request.getParameter("Id");
				
				if((Query_DATE==null || "".equals(Query_DATE)) && (decp_id==null || "".equals(decp_id)) && (Id==null || "".equals(Id)))
				{
					empInfo =   (ArrayList) DataAccessor.getSession().queryForList("activitiesLog.activitiesStatistics",context.contextMap);
				}
				else
				{
					empInfo =   (ArrayList) DataAccessor.getSession().queryForList("activitiesLog.activitiesStatisticsByDate",context.contextMap);
				}
				
	        		company_count =   (ArrayList)(ArrayList) DataAccessor.getSession().queryForList("activitiesLog.activitiesStatisticsCounts", context.contextMap);
	        	
	        		//获得首次拜访的统计数 add by ShenQi see mantis 24
					List<Map> countOfFirstAccess=null;
					if(empInfo.size()!=0) {
						countOfFirstAccess=DataAccessor.getSession().queryForList("activitiesLog.getCountOfFirstAccess",context.contextMap);
					}
					for(int j=0;j<empInfo.size();j++) {
						for(int i=0;countOfFirstAccess!=null&&i<countOfFirstAccess.size();i++) {
							if(((Map)empInfo.get(j)).get("USER_ID").equals(countOfFirstAccess.get(i).get("USER_ID"))&&
									((Map)empInfo.get(j)).get("CREATE_DATE").equals(countOfFirstAccess.get(i).get("CREATE_DATE"))) {
								((Map)empInfo.get(j)).put("TOTAL",countOfFirstAccess.get(i).get("TOTAL"));
								break;
							}
						}
					}
		    } catch (SQLException e) {
			
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errorList.add(e);
		    }
		}
		
		if(errorList.isEmpty()){
		    ActivitiesStatisticsExcel excel=new ActivitiesStatisticsExcel();
		   excel.activitiesStatisticExcelJoin(empInfo, company_count, context,Query_DATE);
		}
	}
	/**
	 * 
	 * 
	 * @param context
	 */
	public void activitiesStatisitceTotal(Context context)
	{
		List errorList = context.errList;
		HashMap  outputMap  = new HashMap();
		DataWrap rs = null ;
		if(errorList.isEmpty()){
		    try {
		    	if(context.contextMap.get("startDate") == null){
		    		context.contextMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date())) ;
		    	}
		    	String year = context.contextMap.get("startDate").toString() ;
	    		outputMap.put("year", year.substring(0,4)+"年") ;
				rs =  (DataWrap) DataAccessor.query("activitiesLog.activitiesStatisticsTotal", context.contextMap, DataAccessor.RS_TYPE.PAGED) ;
		    } catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
		    }
		}
		if(errorList.isEmpty()){
		    outputMap.put("dw", rs);
		    outputMap.put("startDate", context.contextMap.get("startDate")) ;
		    outputMap.put("endDate", context.contextMap.get("endDate")) ;
		    Output.jspOutput(outputMap, context, "/activitiesLog/activitiesStatisticsTotal.jsp");
		}
	}
	
	/**
	 * 导出 excel
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void activitiesStatisitceTotalExcel(Context context){  
		List<Map> activitTotalList=null;
		Map content = new HashMap() ;
		try {
			if(context.contextMap.get("startDate") == null){
	    		context.contextMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date())) ;
	    	}
	    	String year = context.contextMap.get("startDate").toString() ;
	    	content.put("year", year.substring(0,4)+"年") ;
	    	activitTotalList=(List<Map>)  ((DataWrap) DataAccessor.query("activitiesLog.activitiesStatisticsTotal", context.contextMap, DataAccessor.RS_TYPE.PAGED)).getRs();
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}		
		content.put("activitTotalList", activitTotalList) ;
		
		ByteArrayOutputStream baos = null;
		String strFileName = "每日业务活动统计表("+DataUtil.StringUtil(context.contextMap.get("date"))+").xls";
		
		ActivitiesStatisticTotalExcel activitiesStatisticTotalExcel = new ActivitiesStatisticTotalExcel();
		activitiesStatisticTotalExcel.createexl();
		baos = activitiesStatisticTotalExcel.exportactivitTotalExcel(content);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			activitiesStatisticTotalExcel.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	
	
	
	//修改HAB值
	public void changeHAB(Context context){
		List errList = context.errList;
		HashMap  outputMap  = new HashMap();
		if(errList.isEmpty()){
		    SqlMapClient sqlMapper=DataAccessor.getSession();
		    	
		    try {
		    	sqlMapper.startTransaction();
		    	sqlMapper.update("activitiesLog.modifyHAB",context.getContextMap());	
		    	sqlMapper.commitTransaction();
		    } catch (SQLException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		    }finally{
		    	try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
		    }
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
		if(errList.isEmpty()){
			//
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

}
