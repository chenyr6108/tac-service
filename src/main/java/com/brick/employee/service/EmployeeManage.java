package com.brick.employee.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 员工管理
 * 
 * @author li shaojie
 * @date Apr 16, 2010
 * 
 */

public class EmployeeManage extends BaseCommand {
	Log logger = LogFactory.getLog(EmployeeManage.class);
	
	/**
	 * 添加员工信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void preAdd(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List jobs=null;
		try {
			context.contextMap.put("dataType", "员工职位");
			jobs = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("jobs", jobs);  

		} catch (Exception e) {
			errList.add("添加员工失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		} 
		Output.jspOutput(outputMap, context, "/employee/employeeCreate.jsp");
	}

	/**
	 * 添加员工信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void add(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList; 
		SqlMapClient sqlMapper = null ;
		try {  
			
			/* 2012/1/10 Yang Yun 新增区域控制. */
			HttpServletRequest request = context.getRequest();
			String company[] = request.getParameterValues("decp_id");
			String area[] = request.getParameterValues("decp_id2");
			String department[] = request.getParameterValues("dept_id");
			int dept_div_count = Integer.parseInt((String)context.contextMap.get("dept_div_count"));
			List<String> depts = new ArrayList<String>();
			for (int i = 0; i < dept_div_count; i++) {
				if (!StringUtils.isEmpty(company[i].trim()) && !StringUtils.isEmpty(area[i].trim()) && !StringUtils.isEmpty(department[i].trim())) {
					depts.add(department[i]);
				}
			}
			context.contextMap.put("dept_id", depts.size() > 0 ? depts.get(0) : 0);
			/* 2012/1/10 Yang Yun 新增区域控制.End */
			
			 sqlMapper= DataAccessor.getSession() ;
			 sqlMapper.startTransaction();
			 	
			 
			 	//Long Id=(Long)DataAccessor.execute("employee.create", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
			 	Long Id=(Long)sqlMapper.insert("employee.create", context.contextMap);
			 	
			 	/* 2012/1/10 Yang Yun 新增区域控制. */
				 Map<String, Object> paraMap = new HashMap<String, Object>();
				 paraMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
				 paraMap.put("id", Id);
				 if (depts.size() > 0) {
					 sqlMapper.delete("employee.deleteuser2dept", context.contextMap);
					 //System.out.println("delete id = " + context.contextMap.get("id"));
					 for (String dept_id : depts) {
						 //System.out.println("insert dept_id = " + string);
						 paraMap.put("dept_id", dept_id);
						 sqlMapper.insert("employee.insertuser2dept", paraMap);
					}
				 }
				 /* 2012/1/10 Yang Yun 新增区域控制.End */
			 	
				//公司基本账户
				sqlMapper.update("employee.deleteUserBankAccountById", context.contextMap);
				Map baseBankAccount=new HashMap();
				@SuppressWarnings("unused")
				String B_PCCBA_ID=(String)context.contextMap.get("B_PCCBA_ID");
				baseBankAccount.put("BANK_NAME", context.contextMap.get("B_BANK_NAME"));
				baseBankAccount.put("BANK_ACCOUNT", context.contextMap.get("B_BANK_ACCOUNT"));
				baseBankAccount.put("STATE","0");
				baseBankAccount.put("STATUS","0");
				baseBankAccount.put("USER_ID", Id);
				baseBankAccount.put("USER_NAME", context.contextMap.get("name"));
				
				sqlMapper.insert("employee.createUserBankAccount", baseBankAccount);
				
				
				//公司其他账户
				
				if(context.request.getParameterValues("BANK_NAME")!=null){
					String[] BANK_NAME=HTMLUtil.getParameterValues(context.request, "BANK_NAME", "");
					String[] BANK_ACCOUNT=HTMLUtil.getParameterValues(context.request, "BANK_ACCOUNT", "");
					for (int i = 0; i < BANK_NAME.length; i++) {
						Map bankAccount=new HashMap();
						bankAccount.put("BANK_NAME", BANK_NAME[i]);
						bankAccount.put("BANK_ACCOUNT", BANK_ACCOUNT[i]);
						bankAccount.put("STATE","1");
						bankAccount.put("STATUS","0");
						bankAccount.put("USER_ID", Id);
						bankAccount.put("USER_NAME", context.contextMap.get("name"));
						sqlMapper.insert("employee.createUserBankAccount", bankAccount);
					}
				}
			 
				sqlMapper.commitTransaction();
			 
			 
		} catch (Exception e) {
			errList.add("添加员工失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		finally { 
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) { 
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		outputMap.put("errList", errList);
		Output.jspSendRedirect(context,"defaultDispatcher?__action=employee.getEmployees");
	}

	/**
	 * 查询员工列表
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getEmployees(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;
		List jobs=null;
		try {
		    
			dw = (DataWrap) DataAccessor.query("employee.getEmployees",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
			
			context.contextMap.put("dataType", "员工职位");
			jobs = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			List rr =    (List) dw.getRs();
			
			for(int i =0;i< rr.size();i++){
			    
			    Map rm  =	(Map) rr.get(i);
			    String jj= ""+  rm.get("JOB") ;
			    
        			    for (int j =0 ;j<jobs.size(); j++){
        				
            				Map mn = (Map) jobs.get(j);
            				String code=""+  mn.get("CODE");
            				
                				if(jj.equals(code)){
                				    
                				    rm.put("FLAG",  mn.get("FLAG"));
                				}
        			    }
			}

			
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		Output.jspOutput(outputMap, context, "/employee/employeeManage.jsp");
	}

	/**
	 * 作废一条员工记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void invalid(Context context) {
		@SuppressWarnings("unused")
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		try {
			sqlMap.startTransaction();
			sqlMap.update("employee.invalid", context.contextMap);
			sqlMap.update("employee.deleteUser2RolForUser", context.contextMap);
			baseService.insertActionLog(context, "员工作废", 
					"作废员工：[" + context.contextMap.get("userName") + "],并删除对应的‘人员-角色’权限明细。");
			sqlMap.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				sqlMap.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Output.jspSendRedirect(context, "defaultDispatcher?__action=employee.getEmployees");
	}
	
	/**
	 * 根据ID获取一条员工记录 更新使用
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getEmployeeById(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map employee=null;
		Map comType=new HashMap();
		List companys=null;
		List depts=null;
		List jobs=null;
		List suplBankAccount=null;
		List<Map<String, Object>> dept_comps = null;
		try {
			
			context.contextMap.put("dataType", "员工职位");
			jobs = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("jobs", jobs);  

			//查询员工的信息
			employee=(Map)DataAccessor.query("employee.getEmployeeById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			/* 2012/1/9 Yang Yun 查询所有部门和对应的区域. */
			dept_comps=(List<Map<String, Object>>)DataAccessor.query("employee.getAllDepts", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("dept_comps", dept_comps);
			/* 2012/1/9 Yang Yun 查询所有部门和对应的区域. */
			
			//得到部门所属公司类型及公司ID
			comType=(Map)DataAccessor.query("employee.getComType", context.contextMap, DataAccessor.RS_TYPE.MAP);	
			//查询所属公司类型下的公司
			context.contextMap.put("decp_id", comType.get("LEGELR"));
			companys=(List)DataAccessor.query("employee.getCompany1", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("companys", companys);
			//公司下的部门
			context.contextMap.put("decp_id2", comType.get("DECP_ID"));
			depts=(List)DataAccessor.query("employee.getDept", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("depts", depts);
			
			suplBankAccount=(List) DataAccessor.query(
					"employee.getUserBankAccountByCreditId", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("suplBankAccount", suplBankAccount);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("employee", employee);
		outputMap.put("comType", comType);	
		Output.jspOutput(outputMap, context, "/employee/employeeUpdate.jsp");
	}
	
	/**
	 * 根据ID获取一条员工记录，查看使用
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getEmployeeByIdForShow(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map employee=null;
		List suplBankAccount=null;
		List jobs=null;
		
		try {
			
			context.contextMap.put("dataType", "员工职位");
			jobs = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("jobs", jobs);  

			
			employee=(Map)DataAccessor.query("employee.getEmployeeById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			//查看银行
			suplBankAccount=(List) DataAccessor.query(
					"employee.getUserBankAccountByCreditId", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("suplBankAccount", suplBankAccount);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("employee", employee);
		Output.jspOutput(outputMap, context, "/employee/employeeShow.jsp");
	}
	
	/**
	 * 更新
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void update(Context context){
		List errList = context.errList;
		SqlMapClient sqlMapper = null ;
		try {
			
			/* 2012/1/10 Yang Yun 新增区域控制. */
			HttpServletRequest request = context.getRequest();
			String company[] = request.getParameterValues("decp_id");
			String area[] = request.getParameterValues("decp_id2");
			String department[] = request.getParameterValues("dept_id");
			int dept_div_count = Integer.parseInt((String)context.contextMap.get("dept_div_count"));
			List<String> depts = new ArrayList<String>();
			for (int i = 0; i < dept_div_count; i++) {
				if (!StringUtils.isEmpty(company[i].trim()) && !StringUtils.isEmpty(area[i].trim()) && !StringUtils.isEmpty(department[i].trim())) {
					depts.add(department[i]);
				}
			}
			context.contextMap.put("dept_id", depts.size() > 0 ? depts.get(0) : 0);
			/* 2012/1/10 Yang Yun 新增区域控制.End */
			
			 sqlMapper= DataAccessor.getSession() ;
			 sqlMapper.startTransaction();
			 
			 /* 2012/1/10 Yang Yun 新增区域控制. */
			 Map<String, Object> paraMap = new HashMap<String, Object>();
			 if (depts.size() > 0) {
				 sqlMapper.delete("employee.deleteuser2dept", context.contextMap);
				 //System.out.println("delete id = " + context.contextMap.get("id"));
				 for (String dept_id : depts) {
					 //System.out.println("insert dept_id = " + string);
					 paraMap.put("dept_id", dept_id);
					 paraMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
					 paraMap.put("id", context.contextMap.get("id"));
					 sqlMapper.insert("employee.insertuser2dept", paraMap);
				}
			 }
			 /* 2012/1/10 Yang Yun 新增区域控制.End */
			 
				//DataAccessor.execute("employee.update", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			 	sqlMapper.insert("employee.update", context.contextMap);
				//公司基本账户
				sqlMapper.update("employee.deleteUserBankAccountById", context.contextMap);
				Map baseBankAccount=new HashMap();
				@SuppressWarnings("unused")
				String B_PCCBA_ID=(String)context.contextMap.get("B_PCCBA_ID");
				baseBankAccount.put("BANK_NAME", context.contextMap.get("B_BANK_NAME"));
				baseBankAccount.put("BANK_ACCOUNT", context.contextMap.get("B_BANK_ACCOUNT"));
				baseBankAccount.put("STATE","0");
				baseBankAccount.put("STATUS","0");
				baseBankAccount.put("USER_ID",context.contextMap.get("id"));
				baseBankAccount.put("USER_NAME", context.contextMap.get("name"));
				
				sqlMapper.insert("employee.createUserBankAccount", baseBankAccount);
				
				
				//公司其他账户
				
				if(context.request.getParameterValues("BANK_NAME")!=null){
					String[] BANK_NAME=HTMLUtil.getParameterValues(context.request, "BANK_NAME", "");
					String[] BANK_ACCOUNT=HTMLUtil.getParameterValues(context.request, "BANK_ACCOUNT", "");
					for (int i = 0; i < BANK_NAME.length; i++) {
						Map bankAccount=new HashMap();
						bankAccount.put("BANK_NAME", BANK_NAME[i]);
						bankAccount.put("BANK_ACCOUNT", BANK_ACCOUNT[i]);
						bankAccount.put("STATE","1");
						bankAccount.put("STATUS","0");
						bankAccount.put("USER_ID", context.contextMap.get("id"));
						bankAccount.put("USER_NAME", context.contextMap.get("name"));
						sqlMapper.insert("employee.createUserBankAccount", bankAccount);
					}
				}
			 
				sqlMapper.commitTransaction();
				
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		finally { 
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) { 
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		Output.jspSendRedirect(context, "defaultDispatcher?__action=employee.getEmployees");
	}
	
	/**
	 * 根据员工职位，查询员工
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getEmployeesByJob(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		List employees=null;
		try {
			employees=(List)DataAccessor.query("employee.getEmployeesByJob", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("employees", employees);
		Output.jsonOutput(outputMap, context);
	}
	
	
	/**
	 * 根据公司类型，查询公司
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCompany(Context context){
		Map outputMap = new HashMap();
		List companys=null;
		try {
			companys=(List)DataAccessor.query("employee.getCompany1", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("companys", companys);
		Output.jsonOutput(outputMap, context);
	}	
	
	/**
	 * 根据公司ID，查询部门
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getDept(Context context){
		Map outputMap = new HashMap();
		List depts=null;
		try {
			depts=(List)DataAccessor.query("employee.getDept", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("depts", depts);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 重置密码
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void resetPass(Context context){
		Map outputMap = new HashMap();
		try {
			DataAccessor.execute("employee.resetPass", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context);
	}	
	
	@SuppressWarnings("unchecked")
	public void getZhuguan(Context context){
		Map outputMap = new HashMap();
		List zhuguan=null;
		try {
			zhuguan=(List)DataAccessor.query("employee.getZhuguan", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("zhuguan", zhuguan);
		Output.jsonOutput(outputMap, context);
	}
	
	
	public void getAllEmployee(Context context){
		Map outputMap = new HashMap();
		List employeeList=null;
		try {
			employeeList=(List)DataAccessor.query("employee.getAllEmployee", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("employeeList", employeeList);
		Output.jsonOutput(outputMap, context);
	}
	/**
	 * 验证登陆账号是否重复
	 * @param context
	 */
	public void checkCodeIsRepeat(Context context){
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		Integer count = 0 ;
		try{
			count = (Integer) DataAccessor.query("employee.findByCode_count", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
		}
		outputMap.put("count", count) ;
		Output.jsonOutput(outputMap, context) ;
	}
	
	//Add by Michael 2012 12-04  查询所有业管文审人员
	public void getAllAuditorEmployees(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo dw =null;
		List userList=null;
		List jobs=null;
		try {
		    
			dw = (PagingInfo) baseService.queryForListWithPaging("employee.getAllAuditorEmployees", context.contextMap, "USER_ID",ORDER_TYPE.ASC);
			userList =baseService.queryForList("employee.getAllDispatchUserList", context.contextMap);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		
		outputMap.put("dw", dw);
		outputMap.put("userList", userList);
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/employee/dispatchEmployeeManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//添加处理人员
	public void addDispatchUser(Context context) {
		try {
			baseService.insert("employee.insertDispatchUser", context.contextMap);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		getAllAuditorEmployees(context);
	}
	//更新处理人员状态
	@SuppressWarnings("unchecked")
	public void updateDispatchUser(Context context) {
		try {
			for (int i=0;i<=DataUtil.intUtil(context.contextMap.get("statusLengthC"));i++){
				context.contextMap.put("USER_ID",context.contextMap.get("USER_ID"+String.valueOf(i)));
				context.contextMap.put("PROPORTION",context.contextMap.get("PROPORTION"+String.valueOf(i)));
				context.contextMap.put("PROPORTION_AUTO",context.contextMap.get("PROPORTION_AUTO"+String.valueOf(i)));
				context.contextMap.put("PROPORTION_CAR",context.contextMap.get("PROPORTION_CAR"+String.valueOf(i)));
				baseService.update("employee.updateDispatchUserProportion", context.contextMap);
			}
			
			baseService.update("employee.updateDispatchStatus", context.contextMap);
			if (!"".equals(context.contextMap.get("TYPE_USER_IDS"))){
				baseService.update("employee.updateDispatchUserType", context.contextMap);
			}
			if (!"".equals(context.contextMap.get("RENT_TYPE_USER_IDS"))){
				baseService.update("employee.updateDispatchUserRentType", context.contextMap);
			}
			if (!"".equals(context.contextMap.get("AUTO_TYPE_USER_IDS"))){
				baseService.update("employee.updateDispatchUserAutoType", context.contextMap);
			}
			if (!"".equals(context.contextMap.get("CAR_TYPE_USER_IDS"))){
				baseService.update("employee.updateDispatchUserCarType", context.contextMap);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		getAllAuditorEmployees(context);
	}
	//
	public void getEmployeeForVistNew(Context context){
		Map outputMap = new HashMap();
		List employeeList=null;
		try {
			employeeList=(List)DataAccessor.query("employee.getEmployeeForVistNew", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("employeeList", employeeList);
		Output.jsonOutput(outputMap, context);
	}
}
