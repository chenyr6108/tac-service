package com.brick.employee.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.employee.service.EmployeeService;
import com.brick.employee.to.EmployeeTO;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.PasswordUtil;
import com.brick.util.web.HTMLUtil;
import com.tac.company.service.CompanyService;

public class EmployeeCommand extends BaseCommand {

	Log logger=LogFactory.getLog(EmployeeCommand.class);
	
	private EmployeeService employeeService;
	private MailUtilService mailUtilService;
	private CompanyService companyService;
	
	public EmployeeService getEmployeeService() {
		return employeeService;
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void queryEmployee(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List deptsList=null;
		PagingInfo<Object> pagingInfo=null;
		
		try {
			//获得页面部门搜索条件的List
			deptsList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);

			//获得所有职位
			context.contextMap.put("dataType","员工职位");
			List<Map<String,Object>> jobs = (List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("jobs",jobs); 
			
			if(context.contextMap.get("STATUS")==null) {
				context.contextMap.put("STATUS",0);
			}
			context.contextMap.put("DATA_TYPE","员工职位");
			pagingInfo=baseService.queryForListWithPaging("employee.queryEmployee",context.contextMap,"createDate",ORDER_TYPE.DESC);
			
			List<EmployeeTO> deptList=this.employeeService.queryDeptName();
			
			for(int j=0;j<deptList.size();j++) {
				for(int i=0;pagingInfo.getResultList()!=null&&i<pagingInfo.getResultList().size();i++) {
					EmployeeTO to=(EmployeeTO)pagingInfo.getResultList().get(i);
					if(deptList.get(j).getId()==to.getId()) {
						if("".equals(to.getDeptName())||to.getDeptName()==null) {
							to.setDeptName(deptList.get(j).getDeptName());
						} else {
							to.setDeptName(to.getDeptName()+",<br>"+deptList.get(j).getDeptName());
						}
						break;
					}
				}
			}
			List companys = companyService.getAllCompany();
			
			outputMap.put("STATUS",context.contextMap.get("STATUS"));
			outputMap.put("JOB_ID",context.contextMap.get("JOB_ID"));
			outputMap.put("CONTENT",context.contextMap.get("CONTENT"));
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("deptList",deptsList);		
			outputMap.put("companys",companys);
			outputMap.put("pagingInfo",pagingInfo);
		} catch (Exception e) {
			logger.debug("员工查询出错!");
			context.errList.add("员工查询出错!");
		}
		
		if(context.errList.isEmpty()){
			Output.jspOutput(outputMap,context,"/employee/queryEmployee.jsp");
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	
	public void updateUserDept(Context context){
		int userId  = HTMLUtil.getIntParam(context.request, "userId", 0);
		int newDeptId  = HTMLUtil.getIntParam(context.request, "newDeptId", 0);
		
		if(userId==0||newDeptId==0){
			Output.jsonFlageOutput(false, context);
		}else{			
			employeeService.updateUserDept(userId,newDeptId);
			Output.jsonFlageOutput(true, context);
		}
		
	}
	public void updateEmployeeStatus(Context context) {
		
		this.employeeService.updateEmployeeStatus(context);
		this.queryEmployee(context);
	}
	
	public void addEmployee(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			this.employeeService.addEmployee(context);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			
			mailSettingTo.setEmailSubject("租赁系统帐号开通");//主题
			mailSettingTo.setEmailContent("<font style='font-family:微软雅黑'>"+context.contextMap.get("NAME")+"您好:<br>您的租赁系统帐号已开通<br>用户名:"+"<font color='red' style='font-family:微软雅黑'>"+context.contextMap.get("code")+"</font><br>密码:<font color='red' style='font-family:微软雅黑'>"+context.contextMap.get("randomPWD")+"</font><br><font color='red' style='font-family:微软雅黑'>安全提醒:您在第一次登录系统或者3个月未修改密码,系统会强制要求修改密码!</font><br>技术支持:<a href='mailto:IT@tacleasing.cn'>资讯部</a></font>");
			mailSettingTo.setEmailFrom(Constants.EMAIL_FROM);//email from
			mailSettingTo.setEmailTo((String)context.contextMap.get("EMAIL_ADDRESS"));//email to开通用户
			
			context.contextMap.put("id",context.contextMap.get("upper_user"));
			Map<String,Object> user=this.employeeService.getEmployeeById(context);
			if(user==null) {
				user=new HashMap<String,Object>();
			}
			String cc="IT@tacleasing.cn;";
			cc=cc+(user.get("EMAIL")==null?"":user.get("EMAIL").toString());//email cc资讯课和开通用户主管
			mailSettingTo.setEmailCc(cc);
			mailUtilService.sendMail(mailSettingTo);
		} catch (Exception e) {
			logger.debug("添加新员工出错");
			context.errList.add("添加新员工出错!");
		}
		
		if(context.errList.isEmpty()){
			context.contextMap.put("STATUS",0);
			context.contextMap.put("DEPT_ID","");
			this.queryEmployee(context);
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	public void getJobList(Context context) {
		
		context.contextMap.put("dataType","员工职位");
		List<Map<String,String>> jobs=null;
		try {
			jobs=(List<Map<String,String>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			logger.debug("获取职位列表出错!");
		}
		Output.jsonArrayListOutput(jobs,context);
	}
	
	public void getCompanyList(Context context) {
		
		List companyList=null;
		try {
			//获得页面部门搜索条件的List
			companyList=this.baseService.queryForList("customerVisit.getDeptList",new HashMap());
		} catch (ServiceException e) {
			logger.debug("获取办事处列表出错!");
		}
		Output.jsonArrayListOutput(companyList,context);
	}
	
	public void getDeptList(Context context) {
		
		List<Map<String,String>> deptList=null;
		
		deptList=this.employeeService.getDeptList(context);
		
		Output.jsonArrayListOutput(deptList,context);
	}
	
	public void queryEmployeeDetail(Context context) {
		
		Map<String,String> param=new HashMap<String,String>();
		Map<String,Object> outputMap=new HashMap<String,Object>();
		param.put("id",(String)context.contextMap.get("employeeId"));
		EmployeeTO employeeTO=null;
		List companyList=null;
		List newCompanyList = null;
		try {
			newCompanyList = companyService.getAllCompany();
			
			employeeTO=this.employeeService.queryEmployeeDetail(param);
			
			List<EmployeeTO> deptCmpyList=this.employeeService.queryEmployeeDeptCmpy(param);
			
			employeeTO.setDeptCmpyList(deptCmpyList);
			
			//获得页面部门搜索条件的List
			companyList=this.baseService.queryForList("customerVisit.getDeptList",new HashMap());
			
		} catch(Exception e) {
			logger.debug("获得员工信息出错");
			context.errList.add("获得员工信息出错!");
		}
		
		outputMap.put("employeeId",(String)context.contextMap.get("employeeId"));
		outputMap.put("employeeTO",employeeTO);
		outputMap.put("companyList",companyList);
		outputMap.put("newCompanyList",newCompanyList);
		
		
		if(context.errList.isEmpty()){
			Output.jspOutput(outputMap,context,"/employee/updateEmployee.jsp");
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	public void updateEmployee(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			this.employeeService.updateEmployee(context);
		} catch (Exception e) {
			logger.debug("更新员工出错");
			context.errList.add("更新员工出错!");
		}
		
		if(context.errList.isEmpty()){
			context.contextMap.put("STATUS",0);
			context.contextMap.put("DEPT_ID","");
			this.queryEmployee(context);
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	public void batchEmployee(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> companyList=null;
		List<Map<String,String>> deptList=null;
		List<Map<String,String>> jobList=null;
		List<EmployeeTO> upperList=null;
		
		Map<String,Object> param=new HashMap<String,Object>();
		
		try {
			
			upperList=this.employeeService.getUpperEmployee();
					
			companyList=(List<Map<String,Object>>)this.baseService.queryForList("customerVisit.getDeptList",new HashMap());
			Map<String,Object> map=new HashMap<String,Object>();
			
			if("".equals(context.contextMap.get("COMPANY_ID"))||context.contextMap.get("COMPANY_ID")==null) {
				param.put("COMPANY_ID",1);//初始化拿苏州总公司的员工
			} else {
				param.put("COMPANY_ID",context.contextMap.get("COMPANY_ID"));
			}
			
			deptList=this.employeeService.getDeptList(param);
			
			context.contextMap.put("dataType","员工职位");
			jobList=(List<Map<String,String>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,DataAccessor.RS_TYPE.LIST);
			
			param.put("DEPT_ID",context.contextMap.get("DEPT_ID"));//加入页面部门条件
			param.put("JOB_ID",context.contextMap.get("JOB_ID"));//加入页面职位条件
			param.put("UPPER_ID",context.contextMap.get("UPPER_ID"));//加入页面主管条件
			param.put("DATA_TYPE","员工职位");
			
			List<EmployeeTO> resultList=this.employeeService.batchQueryEmployee(param);
			
			List<EmployeeTO> deptCmpyList=this.employeeService.getDeptCmpyList();

			for(int i=0;i<resultList.size();i++) {
				List<EmployeeTO> empDeptList=new ArrayList<EmployeeTO>();
				for(int j=0;j<deptCmpyList.size();j++) {
					if(resultList.get(i).getId()==deptCmpyList.get(j).getId()) {
						empDeptList.add(deptCmpyList.get(j));
					}
				}
				resultList.get(i).setDeptCmpyList(empDeptList);
			}
			
			outputMap.put("COMPANY_ID",context.contextMap.get("COMPANY_ID"));
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("JOB_ID",context.contextMap.get("JOB_ID"));
			outputMap.put("UPPER_ID",context.contextMap.get("UPPER_ID"));
			outputMap.put("companyList",companyList);
			outputMap.put("deptList",deptList);
			outputMap.put("jobList",jobList);
			outputMap.put("upperList",upperList);
			
			outputMap.put("resultList",resultList);
		} catch (Exception e) {
			logger.debug("批量获得员工列表出错");
			context.errList.add("批量获得员工列表出错");
		}
		
		if(context.errList.isEmpty()){
			Output.jspOutput(outputMap,context,"/employee/batchQueryEmployee.jsp");
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	public void batchUpdate(Context context) {
		
		Map<String,Object> param=new HashMap<String,Object>();
		String [] employees=HTMLUtil.getParameterValues(context.request,"EMPLOYEE_ID","");
		param.put("s_employeeId",context.contextMap.get("s_employeeId"));
		param.put("employees",employees);
		param.put("deptIds",HTMLUtil.getParameterValues(context.request,"NEW_DEPT_ID",""));
		StringBuffer ids=new StringBuffer();
		for(int i=0;i<employees.length;i++) {
			if(i!=employees.length-1) {
				ids.append("'"+employees[i]+"',");
			} else {
				ids.append("'"+employees[i]+"'");
			}
		}
		param.put("ids",ids);
		param.put("jobCode",context.contextMap.get("NEW_JOB"));
		param.put("upperId",context.contextMap.get("NEW_UPPER_ID"));
		
		try {
			this.employeeService.batchUpdate(param);
		} catch (Exception e) {
			logger.debug("批量更新出错!");
			context.errList.add("批量更新出错");
		}
		
		if(context.errList.isEmpty()){
			this.batchEmployee(context);
		} else {
			Map<String,Object> outputMap=new HashMap<String,Object>();
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	public void getPassword(Context context) {
		
		String pwd=PasswordUtil.resetPassword();
		
		Map<String,String> outputMap=new HashMap<String,String>();
		outputMap.put("pwd",pwd);
		Output.jsonOutput(outputMap,context);
	}
	
	public void resetPassword(Context context) {
		
		try {
			Map<String,Object> user=this.employeeService.getEmployeeById(context);
			
			this.employeeService.resetPassword(context);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			
			mailSettingTo.setEmailSubject("密码重置");
			if("userReset".equals(context.contextMap.get("userReset"))) {//判断是用户自己忘记密码重置,还是在员工管理下管理员重置密码
				//忘记密码重置
				mailSettingTo.setEmailContent("<font style='font-family:微软雅黑'>"
				+user.get("NAME")+"您好:<br>您的租赁系统密码已经重置为"
				+"<font color='red' style='font-family:微软雅黑'>"
				+context.contextMap.get("password")
				+"</font><br><font color='red'>*</font>IP地址为:"+context.getRequest().getRemoteAddr()+"的用户使用忘记密码重置功能<br>技术支持:<a href='mailto:IT@tacleasing.cn'>资讯部</a>"+"</font>");
			} else {
				mailSettingTo.setEmailContent("<font style='font-family:微软雅黑'>"+user.get("NAME")+"您好:<br>您的租赁系统密码已经重置为"+"<font color='red' style='font-family:微软雅黑'>"+context.contextMap.get("password")+"</font><br>技术支持:<a href='mailto:IT@tacleasing.cn'>资讯部</a>"+"</font>");
			}
			mailSettingTo.setEmailFrom(Constants.EMAIL_FROM);
			mailSettingTo.setEmailTo((String)user.get("EMAIL"));
			mailSettingTo.setEmailCc("IT@tacleasing.cn");
			mailUtilService.sendMail(mailSettingTo);
		} catch (Exception e) {
			Output.jsonFlageOutput(false,context);
		}
		
		Output.jsonFlageOutput(true,context);
	}
	
	public void checkUserId(Context context) {
		
		Map<String,Integer> result=new HashMap<String,Integer>();
		try {
			result=this.employeeService.checkUserId(context);
		} catch (Exception e) {
		}
		
		Output.jsonOutput(result,context);
	}
	
	public void getAllEmp(Context context){
		List<SelectionTo> data = null;
		try {
			data = (List<SelectionTo>) baseService.queryForList("employee.getAllEmp");
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getAllEmpWithoutStatus(Context context){
		List<SelectionTo> data = null;
		try {
			data = (List<SelectionTo>) baseService.queryForList("employee.getAllEmpWithoutStatus");
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getAllEmpInfo(Context context){
		List<SelectionTo> data = null;
		try {
			data = (List<SelectionTo>) baseService.queryForList("employee.getAllEmpInfo", context.contextMap);
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getAllEmpInfoWithoutStatus(Context context){
		List<SelectionTo> data = null;
		try {
			data = (List<SelectionTo>) baseService.queryForList("employee.getAllEmpInfoWithoutStatus", context.contextMap);
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonArrayOutputForObject(data, context);
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}
	
}
