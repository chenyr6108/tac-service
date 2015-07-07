package com.brick.employee.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.employee.dao.EmployeeDAO;
import com.brick.employee.to.EmployeeTO;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;

public class EmployeeService extends BaseService {
	
	private EmployeeDAO employeeDAO;

	public EmployeeDAO getEmployeeDAO() {
		return employeeDAO;
	}

	public void setEmployeeDAO(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}
	
	public List<EmployeeTO> queryDeptName() {
		return this.employeeDAO.queryDeptName();
	}
	
	public void updateEmployeeStatus(Context context) {
		this.employeeDAO.updateEmployeeStatus(context);
	}
	
	public List<Map<String,String>> getDeptList(Context context) {
		return this.employeeDAO.getDeptList(context);
	}
	
	public List<Map<String,String>> getDeptList(Map<String,Object> param) {
		return this.employeeDAO.getDeptList(param);
	}
	
	public void updateUserDept(int userId,int department){
		employeeDAO.updateUserDept(userId, department);
	}
	
	
	
	@Transactional
	public void addEmployee(Context context) throws Exception {
		
		try {
			context.contextMap.put("telephone",/*context.contextMap.get("TELPHONE1")+"-"+*/context.contextMap.get("TELPHONE2"));
			long id=this.employeeDAO.addEmployee(context);
			
			String [] companyId=HTMLUtil.getParameterValues(context.request,"COMPANY_ID","");
			String [] deptId=HTMLUtil.getParameterValues(context.request,"DEPT_ID","");
			
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("USER_ID",id);
			param.put("s_employeeId",context.contextMap.get("s_employeeId"));
			for(int i=0;i<companyId.length;i++) {
				param.put("DEPT_ID",deptId[i]);
				this.employeeDAO.addDept(param);
			}
		} catch(Exception e) {
			throw e;
		}
	}
	
	public EmployeeTO queryEmployeeDetail(Map<String,String> param) {
		return this.employeeDAO.queryEmployeeDetail(param);
	}
	
	public List<EmployeeTO> queryEmployeeDeptCmpy(Map<String,String> param) {
		return this.employeeDAO.queryEmployeeDeptCmpy(param);
	}
	
	@Transactional
	public void updateEmployee(Context context) throws Exception {
		
		try {
			context.contextMap.put("telephone",/*context.contextMap.get("TELPHONE1")+"-"+*/context.contextMap.get("TELPHONE2"));
			this.employeeDAO.updateEmployee(context);
			
			this.employeeDAO.deleteDeptIds(context);
			
			String [] companyId=HTMLUtil.getParameterValues(context.request,"COMPANY_ID","");
			String [] deptId=HTMLUtil.getParameterValues(context.request,"DEPT_ID","");
			
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("USER_ID",context.contextMap.get("employeeId"));
			param.put("s_employeeId",context.contextMap.get("s_employeeId"));
			for(int i=0;i<companyId.length;i++) {
				param.put("DEPT_ID",deptId[i]);
				this.employeeDAO.addDept(param);
			}
		} catch(Exception e) {
			throw e;
		}
	}
	
	public List<EmployeeTO> batchQueryEmployee(Map<String,Object> param) {
		return this.employeeDAO.batchQueryEmployee(param);
	}
	
	public List<EmployeeTO> getDeptCmpyList() {
		return this.employeeDAO.getDeptCmpyList();
	}
	
	public List<EmployeeTO> getUpperEmployee() {
		return this.employeeDAO.getUpperEmployee();
	}
	
	@Transactional
	public void batchUpdate(Map<String,Object> param) throws Exception {
		
		try {
			if(param.get("jobCode")==null&&param.get("upperId")!=null) {
				param.put("flag","2");
				this.employeeDAO.batchUpdateEmployeeInfo(param);
			} else if(param.get("jobCode")!=null&&param.get("upperId")==null) {
				param.put("flag","1");
				this.employeeDAO.batchUpdateEmployeeInfo(param);
			} else if(param.get("jobCode")!=null&&param.get("upperId")!=null) {
				param.put("flag","3");
				this.employeeDAO.batchUpdateEmployeeInfo(param);
			}
			
			String [] empIds=(String [])param.get("employees");
			String [] deptIds=(String [])param.get("deptIds");
			
			if(deptIds!=null&&deptIds.length!=0) {
				this.employeeDAO.batchDelDeptIds(param);
				param.put("deptId",deptIds[0]);
				this.employeeDAO.batchUpdateDeptCode(param);
			}
			Map<String,Object> dept=new HashMap<String,Object>();
			dept.put("s_employeeId",param.get("s_employeeId"));
			for(int i=0;i<empIds.length;i++) {
				
				for(int j=0;deptIds!=null&&j<deptIds.length;j++) {
					dept.put("USER_ID",empIds[i]);
					dept.put("DEPT_ID",deptIds[j]);
					this.employeeDAO.addDept(dept);
				}
			}
		} catch(Exception e) {
			throw e;
		}
	}
	
	public void resetPassword(Context context) throws Exception {
		this.employeeDAO.resetPassword(context);
	}
	
	public Map<String,Object> getEmployeeById(Context context) throws Exception {
		return this.employeeDAO.getEmployeeById(context);
	}
	
	public Map<String,Integer> checkUserId(Context context) throws Exception {
		return this.employeeDAO.checkUserId(context);
	}
}
