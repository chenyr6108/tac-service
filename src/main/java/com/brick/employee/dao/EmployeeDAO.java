package com.brick.employee.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.dao.BaseDAO;
import com.brick.employee.to.EmployeeTO;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;

public class EmployeeDAO extends BaseDAO {
	
	public void updateUserDept(int userId,int department){
		Map map = new HashMap();
		map.put("userId", userId);
		map.put("department", department);
		this.getSqlMapClientTemplate().update("employee.updateUserDept",map);
		
	}

	public List<EmployeeTO> queryDeptName() {
		
		List<EmployeeTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("employee.queryDeptName");
		
		if(resultList==null) {
			resultList=new ArrayList<EmployeeTO>();
		}
		
		return resultList;
	}
	
	public void updateEmployeeStatus(Context context) {
		this.getSqlMapClientTemplate().update("employee.updateEmployeeStatus",context.contextMap);
	}
	
	public List<Map<String,String>> getDeptList(Context context) {
		List<Map<String,String>> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("employee.getDeptList",context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<Map<String,String>>();
		}
		
		return resultList;
	}
	
	public List<Map<String,String>> getDeptList(Map<String,Object> param) {
		List<Map<String,String>> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("employee.getDeptList",param);
		
		if(resultList==null) {
			resultList=new ArrayList<Map<String,String>>();
		}
		
		return resultList;
	}
	
	public long addEmployee(Context context) {
		
		context.contextMap.put("DEPT",HTMLUtil.getParameterValues(context.request,"DEPT_ID","")[0]);
		long id=(Long)this.getSqlMapClientTemplate().insert("employee.addEmployee",context.contextMap);
		
		return id;
	}
	
	public void addDept(Map<String,Object> param) {
		this.getSqlMapClientTemplate().insert("employee.addDept",param);
	}
	
	public EmployeeTO queryEmployeeDetail(Map<String,String> param) {
		
		return (EmployeeTO)this.getSqlMapClientTemplate().queryForObject("employee.queryEmployeeDetail",param);
	}
	
	public List<EmployeeTO> queryEmployeeDeptCmpy(Map<String,String> param) {
		
		List<EmployeeTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("employee.queryEmployeeDeptCmpy",param);
		
		if(resultList==null) {
			resultList=new ArrayList<EmployeeTO>();
		}
		
		return resultList;
	}
	
	public void updateEmployee(Context context) {
		
		context.contextMap.put("DEPT",HTMLUtil.getParameterValues(context.request,"DEPT_ID","")[0]);
		this.getSqlMapClientTemplate().update("employee.updateEmployee",context.contextMap);
	}
	
	public void deleteDeptIds(Context context) {
		this.getSqlMapClientTemplate().delete("employee.deleteDeptIds",context.contextMap);
	}
	
	public List<EmployeeTO> batchQueryEmployee(Map<String,Object> param) {
		
		List<EmployeeTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("employee.batchQueryEmployee",param);
		
		if(resultList==null) {
			resultList=new ArrayList<EmployeeTO>();
		}
		
		return resultList;
		
	}
	
	public List<EmployeeTO> getDeptCmpyList() {
		
		List<EmployeeTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("employee.getDeptCmpyList");
		
		if(resultList==null) {
			resultList=new ArrayList<EmployeeTO>();
		}
		
		return resultList;
	}
	
	public List<EmployeeTO> getUpperEmployee() {
		
		List<EmployeeTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("employee.getUpperEmployee");
		
		if(resultList==null) {
			resultList=new ArrayList<EmployeeTO>();
		}
		
		return resultList;
	}
	
	public void batchUpdateEmployeeInfo(Map<String,Object> param) {
		this.getSqlMapClientTemplate().update("employee.batchUpdateEmployeeInfo",param);
	}
	
	public void batchDelDeptIds(Map<String,Object> param) {
		this.getSqlMapClientTemplate().delete("employee.batchDelDeptIds",param);
	}
	
	public void batchUpdateDeptCode(Map<String,Object> param) {
		this.getSqlMapClientTemplate().update("employee.batchUpdateDeptCode",param);
	}
	
	public void resetPassword(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("employee.resetPassword",context.contextMap);
	}
	
	public Map<String,Object> getEmployeeById(Context context) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("employee.getEmployeeById",context.contextMap);
	}
	
	public Map<String,Integer> checkUserId(Context context) throws Exception {
		return (Map<String,Integer>)this.getSqlMapClientTemplate().queryForObject("employee.checkUserId",context.contextMap);
	}
}
