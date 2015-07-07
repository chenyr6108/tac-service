package com.brick.report.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.batchjob.to.AchievementBatchJobTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;



/**
 * 业绩进度
 * @author cheng 
 *
 */

public class AchievementService extends AService {
	Log logger = LogFactory.getLog(AchievementService.class);

    /**
     * 员工业绩进度表
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void achievementView(Context context){
	/*List errorList = context.errList;
	HashMap  outputMap  = new HashMap();
	ArrayList   empInfo = new ArrayList();	 
	ArrayList   empOther = new ArrayList();	 
	ArrayList   company_count = new ArrayList();	//公司 数
	ArrayList   user_count = new ArrayList();	//公司 的旗下员工数
	Double amount=0.0;
	if(errorList.isEmpty()){
	    
	    try {
			context.contextMap.put("job_type", "业务员");
			context.contextMap.put("cust_type", "融资租赁合同类型");
			context.contextMap.put("cust_state", "客户来源");
			if(context.contextMap.get("start_date") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("start_date", sf.format(new Date())) ;
			}else {
				context.contextMap.put("start_date", context.contextMap.get("start_date") + "-01") ;
			}
		 	outputMap.put("start_date_date", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("start_date").toString())) ;
		 	2011/12/28 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件) -------
			Map<String, Object> rsMap = null;
			context.contextMap.put("id", context.contextMap.get("s_employeeId"));
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			if (rsMap == null || rsMap.get("NODE") == null) {
				throw new Exception("Session lost");
			}
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			--------------------------------------------------------------------------- 
    		empInfo =   (ArrayList) DataAccessor.getSession().queryForList("achievement.queryEmplAchieveNew",context.contextMap);
    		company_count =   (ArrayList)(ArrayList) DataAccessor.getSession().queryForList("achievement.queryEmployeeAndDeptNew_count", context.contextMap);
    		user_count=(ArrayList)(ArrayList) DataAccessor.getSession().queryForList("achievement.queryEmployeeAndEmpl_count", context.contextMap);
    		
    		//计算月目标金额总数,申请拨款金额总数,拨款额总数 add by ShenQi
    		for(int i=0;empInfo!=null&&i<empInfo.size();i++) {
    			if(i!=empInfo.size()-1) {
    				if(!((Map)(empInfo.get(i))).get("EMPL_ID").equals(((Map)(empInfo.get(i+1))).get("EMPL_ID"))) {
    					amount=Double.parseDouble(((Map)(empInfo.get(i))).get("EMPL_ACHIEVEMENT")==null?"0":((Map)(empInfo.get(i))).get("EMPL_ACHIEVEMENT").toString())+amount;
        			}
    			} else {
    				amount=Double.parseDouble(((Map)(empInfo.get(i))).get("EMPL_ACHIEVEMENT")==null?"0":((Map)(empInfo.get(i))).get("EMPL_ACHIEVEMENT").toString())+amount;
    			}
    		}
	    } catch (Exception e) {
		
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
		errorList.add(e);
	    }
	}
	
	if(errorList.isEmpty()){
	    
	    outputMap.put("comp", empInfo);
	    outputMap.put("amount", amount);
	    outputMap.put("company_count", company_count);
	    outputMap.put("user_count", user_count);
	    outputMap.put("start_date", context.contextMap.get("start_date")) ;
	   
	    Output.jspOutput(outputMap, context, "/achievement/achievementView.jsp");
	}*/
    	
    	//重写员工业绩进度功能 add by ShenQi
    	String log="employeeId="+context.contextMap.get("s_employeeId")+"......achievementView";
    	
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
    	Map<String,Object> resultMap=null;
    	List<Map<String,Object>> resultList=null;
    	Map<String,Object> outputMap=new HashMap<String,Object>();
		context.contextMap.put("id",context.contextMap.get("s_employeeId"));
		
		//设置页面的查询条件
		if(context.contextMap.get("DATE")==null||"".equals(context.contextMap.get("DATE"))) {
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			String date=DateUtil.dateToString(cal.getTime(), "yyyy-MM-dd");
			context.contextMap.put("DATE",date);
			outputMap.put("DATE",date);
		} else {
			outputMap.put("DATE",context.contextMap.get("DATE"));
		}
		if(context.contextMap.get("SORT")==null||"".equals(context.contextMap.get("SORT"))) {
			context.contextMap.put("SORT","ASC");
			outputMap.put("SORT","ASC");
		} else {
			outputMap.put("SORT",context.contextMap.get("SORT"));
		}
		
		try {
			resultMap=(Map<String,Object>)DataAccessor.query("employee.getEmpInforById",context.contextMap,DataAccessor.RS_TYPE.MAP);
			
			//获得办事处与目标
			context.contextMap.put("USER_NODE",resultMap.get("NODE"));
			context.contextMap.put("USER_ID",context.contextMap.get("s_employeeId"));
			resultList=(List<Map<String,Object>>)DataAccessor.query("achievement.getAchievementByDept",
																							context.contextMap,DataAccessor.RS_TYPE.LIST);
			if(resultMap==null||resultMap.get("NODE")==null) {
				throw new Exception("Session lost");
			}
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add(e);
		}
		
		if(context.errList.isEmpty()) {
			
			outputMap.put("resultList",resultList);
			Output.jspOutput(outputMap,context,"/achievement/achievementView.jsp");
			
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
    }
    
    public void achievementByUser(Context context) {
    	
    	String log="employeeId="+context.contextMap.get("s_employeeId")+"......achievementByUser";
    	
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<Map<String,Object>> resultList=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("achievement.getAchievementByUser",context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add(e);
		}
		
		if(context.errList.isEmpty()) {
			
			outputMap.put("resultList",resultList);
			Output.jspOutput(outputMap,context,"/achievement/achievementByUser.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
    }
    
    public void achievementDetail(Context context) {
    	
    	String log="employeeId="+context.contextMap.get("s_employeeId")+"......achievementDetail";
    	
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<AchievementBatchJobTo> resultList=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			resultList=(List<AchievementBatchJobTo>)DataAccessor.query("achievement.getAchievementDetail",context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add(e);
		}
		
		if(context.errList.isEmpty()) {
			outputMap.put("resultList",resultList);
			Output.jspOutput(outputMap,context,"/achievement/achievementDetail.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
    }
    /**
     * 员工业绩目标录入页面
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void achievementPage(Context context){
	
	HashMap  outputMap  = new HashMap();
	ArrayList   employee_count = new ArrayList(); //员工
	ArrayList   company_count = new ArrayList();	//公司 与旗下员工数
	
	
	
	
	String deptName1  = "" ;
	String deptName2 = "" ;
	context.contextMap.put("job_type", "业务员");
	
	try {
		/*2011/12/28 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件) -------*/
		Map<String, Object> rsMap = null;
		context.contextMap.put("id", context.contextMap.get("s_employeeId"));
		rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
		if (rsMap == null || rsMap.get("NODE") == null) {
			throw new Exception("Session lost");
		}
		context.contextMap.put("p_usernode", rsMap.get("NODE"));
		/*--------------------------------------------------------------------------- */
	    employee_count = (ArrayList) DataAccessor.query("achievement.queryEmployeeAndDept", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
	    company_count =   (ArrayList) DataAccessor.query("achievement.queryEmployeeAndDept_count", context.contextMap, DataAccessor.RS_TYPE.LIST);
	    
	    double mon1=0.0;
	    double mon2=0.0;
	    double mon3=0.0;
	    double mon4=0.0;
	    double mon5=0.0;
	    double mon6=0.0;
	    double mon7=0.0;
	    double mon8=0.0;
	    double mon9=0.0;
	    double mon10=0.0;
	    double mon11=0.0;
	    double mon12=0.0;
	    for(int i=0;employee_count!=null&&i<employee_count.size();i++) {
	    	if("*SUM_ALL".equalsIgnoreCase((String)((Map)employee_count.get(i)).get("NAME"))) {
	    		mon1=mon1+((BigDecimal)((Map)employee_count.get(i)).get("MON1")).doubleValue();
	    		mon2=mon2+((BigDecimal)((Map)employee_count.get(i)).get("MON2")).doubleValue();
	    		mon3=mon3+((BigDecimal)((Map)employee_count.get(i)).get("MON3")).doubleValue();
	    		mon4=mon4+((BigDecimal)((Map)employee_count.get(i)).get("MON4")).doubleValue();
	    		mon5=mon5+((BigDecimal)((Map)employee_count.get(i)).get("MON5")).doubleValue();
	    		mon6=mon6+((BigDecimal)((Map)employee_count.get(i)).get("MON6")).doubleValue();
	    		mon7=mon7+((BigDecimal)((Map)employee_count.get(i)).get("MON7")).doubleValue();
	    		mon8=mon8+((BigDecimal)((Map)employee_count.get(i)).get("MON8")).doubleValue();
	    		mon9=mon9+((BigDecimal)((Map)employee_count.get(i)).get("MON9")).doubleValue();
	    		mon10=mon10+((BigDecimal)((Map)employee_count.get(i)).get("MON10")).doubleValue();
	    		mon11=mon11+((BigDecimal)((Map)employee_count.get(i)).get("MON11")).doubleValue();
	    		mon12=mon12+((BigDecimal)((Map)employee_count.get(i)).get("MON12")).doubleValue();
	    	}
	    }
	    outputMap.put("mon1", mon1);
	    outputMap.put("mon2", mon2);
	    outputMap.put("mon3", mon3);
	    outputMap.put("mon4", mon4);
	    outputMap.put("mon5", mon5);
	    outputMap.put("mon6", mon6);
	    outputMap.put("mon7", mon7);
	    outputMap.put("mon8", mon8);
	    outputMap.put("mon9", mon9);
	    outputMap.put("mon10", mon10);
	    outputMap.put("mon11", mon11);
	    outputMap.put("mon12", mon12);
	    
		int count = company_count.size();
		if (count == 1) {
			Map job = (Map) company_count.get(1);
			job.put("DEPT_COUNT", company_count.size());
		} else if (count > 1) {
			for (int j = 0; j < employee_count.size(); j++) {
				Map decp = (Map) employee_count.get(j);
				deptName1 = decp.get("DECP_NAME_CN") + "";
				if (deptName1.equals(deptName2)) {
				} else {
					String DEPT_COUNT = returnCount(company_count,
							deptName1);
					deptName2 = deptName1;
					decp.put("DEPT_COUNT", DEPT_COUNT);
				}
			}
		}
	    outputMap.put("DAYFLAGE", ((Map)employee_count.get(0)).get("DAYFLAGE"));
	    outputMap.put("EMPLOYEE2JOB",employee_count );
	    outputMap.put("COMPANY2JOB",company_count );
	    outputMap.put("NO_COUNT2JOB",count );
	    
	    
		//获取年份
	    GregorianCalendar g=new GregorianCalendar();
	    int year=g.get(Calendar.YEAR);
	    if(context.contextMap.get("YEAR")!=null&&!"".equals(context.contextMap.get("YEAR"))) {
	    	year=Integer.valueOf((String)context.contextMap.get("YEAR"));
	    	outputMap.put("YEAR", (String)context.contextMap.get("YEAR")) ;
	    } else {
	    	outputMap.put("YEAR", year) ;
	    }
	    int mon = g.get(Calendar.MONTH)+1;
	    List monthList = new ArrayList() ;
	    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM") ;
		for(int i = 1;i<=12 ;i++){
			Date date = sf.parse(year + "-" + i) ;
			monthList.add(date) ;
		}
		
		outputMap.put("monthList", monthList) ;
		outputMap.put("mon", mon) ;
		outputMap.put("getDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date())) ;
	} catch (Exception e) {
	     
	    e.printStackTrace();
	    LogPrint.getLogStackTrace(e, logger);
	}
    Output.jspOutput(outputMap, context, "/achievement/achievementPage.jsp");
    }
    
    
	/**
	 * 返回相对应的公司个数
	 * 
	 * @param company_count
	 * @return DEPT_COUNT
	 */
	@SuppressWarnings("unchecked")
	String returnCount(List company_count, String deptName1) {
		int size = company_count.size();
		String count = null;
		for (int i = 0; i < size; i++) {
			String deptName = null;
			Map element = (Map) company_count.get(i);
			deptName = element.get("DECP_NAME_CN") + "";
			if (deptName1.equals(deptName)) {
				count = element.get("DEPT_COUNT") + "";
			}
		}
		return count;
	}



	/**
	 * 保存员工业绩
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void saveorUpdateEmplAchievement(Context context) {
		Map dataMap = new HashMap();
		Map outputMap = new HashMap();

		List errList = context.getErrList();
		// 全年获取
		List emplList = new ArrayList();
		List monList = new ArrayList();
		List decpList = new ArrayList();
		List deptList = new ArrayList();
		List idList = new ArrayList();
		List monFlagList = new ArrayList();
		for (int i = 1; i <= 12; i++) {
			String empl[] = HTMLUtil.getParameterValues(context.request,
					"empl_achievement" + i, "0");
			String mon[] = HTMLUtil.getParameterValues(context.getRequest(),
					"empl_month" + i, "0");
			String decp[] = HTMLUtil.getParameterValues(context.getRequest(),
					"decp_id" + i, "0");
			String dept[] = HTMLUtil.getParameterValues(context.getRequest(),
					"dept_id" + i, "0");
			String id[] = HTMLUtil.getParameterValues(context.getRequest(),
					"empl_id" + i, "0");
			String mon_flag[] = HTMLUtil.getParameterValues(
					context.getRequest(), "mon_flag" + i, "0");

			emplList.add(empl);
			monList.add(mon);
			decpList.add(decp);
			deptList.add(dept);
			idList.add(id);
			monFlagList.add(mon_flag);
		}

		String user_id = context.contextMap.get("s_employeeId") + "";
		SqlMapClient sqlMapper = null;

		if (errList.isEmpty()) {

			try {
				sqlMapper = DataAccessor.getSession();

				if (emplList.size() == monList.size()
						&& monList.size() == decpList.size()
						&& decpList.size() == monFlagList.size()) {
					sqlMapper.startTransaction();

					/* 2012/01/13 Yang Yun 录入业绩目标是在GOAL_LINE表中添加记录. */
					List<Map<String, Object>> goalLines = new ArrayList<Map<String,Object>>();
					Map<String, Object> goalLine = null;
					Double goalMoney = 0D;
					String tempDept = null;
					boolean flag = true;//控制月份循环时，月份+1前的最后一个部门的月份。
					/* ********************************************************* */
					for (int j = 0; j < 12; j++) {
						String empl[] = (String[]) emplList.get(j);
						String mon[] = (String[]) monList.get(j);
						String decp[] = (String[]) decpList.get(j);
						String dept[] = (String[]) deptList.get(j);
						String id[] = (String[]) idList.get(j);
						String mon_flag[] = (String[]) monFlagList.get(j);
						for (int i = 0; i < empl.length; i++) {
							/* 2012/01/13 Yang Yun 录入业绩目标是在GOAL_LINE表中添加记录. */
							if (tempDept != null && !dept[i].equals(tempDept)) {
								goalLine = new HashMap<String, Object>();
								goalLine.put("month", flag ? j + 1 : j);
								goalLine.put("dept", tempDept);
								goalLine.put("goal_money", goalMoney);
								goalLines.add(goalLine);
								goalMoney = 0D;
								flag = true;
							} else {
								if (i == empl.length - 1) {
									flag = false;
								}
							}
							goalMoney += Double.parseDouble(empl[i]);
							tempDept = dept[i];
							/* ********************************************************* */
							dataMap.clear();
							dataMap.put("empl_achievement", empl[i]);
							dataMap.put("empl_month", mon[i]);
							dataMap.put("decp_id", decp[i]);
							dataMap.put("empl_id", id[i]);
							dataMap.put("user_id", user_id);
							dataMap.put("YEAR", context.contextMap.get("YEAR"));
							
							sqlMapper.update("achievement.updateAchievement",
									dataMap);
							sqlMapper.insert("achievement.createAchievement",
									dataMap);
						}
					}
					/* 2012/01/13 Yang Yun 录入业绩目标是在GOAL_LINE表中添加记录. */
					//记录最后一个月的最后一个部门的目标业绩
					goalLine = new HashMap<String, Object>();
					goalLine.put("month", 12);
					goalLine.put("dept", tempDept);
					goalLine.put("goal_money", goalMoney);
					goalLines.add(goalLine);
					//将拼组好的目标业绩 插入 T_GOAL_MONEY 表中。
					Map<String, Object> paramMap = null;
					for (Map<String, Object> map : goalLines) {
						paramMap = new HashMap<String, Object>();
						paramMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
						paramMap.put("month", map.get("month"));
						paramMap.put("dept_id", map.get("dept"));
						paramMap.put("goal_money", map.get("goal_money"));
						paramMap.put("YEAR", context.contextMap.get("YEAR"));
						sqlMapper.delete("achievement.deleteOldGoalMoney", paramMap);
						sqlMapper.insert("achievement.insertNewGoalMoney", paramMap);
					}
					/* ********************************************************* */
					sqlMapper.commitTransaction();
				}

			} catch (Exception e) {

				errList.add("保存员工业绩记录出错: " + e);
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);

			} finally {

				try {
					sqlMapper.endTransaction();

				} catch (SQLException e) {

					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}

			}

			if (errList.isEmpty()) {
				// Output.jspSendRedirect(context,
				// context.request.getContextPath()+"/achievement/achievementPage.jsp");
				// Output.jspOutput(outputMap,context,
				// "/achievement/achievementPage.jsp");
				Output.jspSendRedirect(context,
						"defaultDispatcher?__action=achievement.achievementPage");
			} else {

				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}

		}
	}
       
       
       
       //导出业务员业绩进度
       public void achievementExcel(Context context)
       {
    	   List errorList = context.errList;
    		HashMap  outputMap  = new HashMap();
    		ArrayList   empInfo = new ArrayList();
    		ArrayList   company_count = new ArrayList();	//公司 数
    		ArrayList   user_count = new ArrayList();	//公司 的旗下员工数
    		if(errorList.isEmpty()){
    		    
    		    try {
    				context.contextMap.put("job_type", "业务员");
    				context.contextMap.put("cust_type", "融资租赁合同类型");
    				context.contextMap.put("cust_state", "客户来源");
    				if(context.contextMap.get("start_date") == null){
    					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
    					context.contextMap.put("start_date", sf.format(new Date())) ;
    				}else {
    					context.contextMap.put("start_date", context.contextMap.get("start_date") + "-01") ;
    				}
    				/*2011/12/28 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件) -------*/
    				Map<String, Object> rsMap = null;
    				context.contextMap.put("id", context.contextMap.get("s_employeeId"));
    				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
    				if (rsMap == null || rsMap.get("NODE") == null) {
    					throw new Exception("Session lost");
    				}
    				context.contextMap.put("p_usernode", rsMap.get("NODE"));
    				/*--------------------------------------------------------------------------- */
	        		empInfo =   (ArrayList) DataAccessor.getSession().queryForList("achievement.queryEmplAchieveNew",context.contextMap);
	        		company_count =   (ArrayList)(ArrayList) DataAccessor.getSession().queryForList("achievement.queryEmployeeAndDeptNew_count", context.contextMap);
	        		user_count=(ArrayList)(ArrayList) DataAccessor.getSession().queryForList("achievement.queryEmployeeAndEmpl_count", context.contextMap);
    		    } catch (Exception e) {
    			
    			e.printStackTrace();
    			LogPrint.getLogStackTrace(e, logger);
    			errorList.add(e);
    		    }
    		}
    		
    		if(errorList.isEmpty()){
    		    AchievementExcel excel=new AchievementExcel();
    		    excel.achievementExcelJoin(empInfo,company_count,user_count,context);
    		}
       }
       
       
       

}
