package com.tac.dept.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.base.dao.BaseDAO;
import com.brick.base.to.PagingInfo;
import com.tac.dept.to.DeptTo;
import com.tac.user.to.UserTo;


public class DeptDAO extends BaseDAO{

	public Integer insertDept(DeptTo dept){
		return (Integer) this.getSqlMapClientTemplate().insert("dept.insertDept", dept);		
	}
	public void deleteDept(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		this.getSqlMapClientTemplate().update("dept.deleteDept", param);
	}
	
	public void updateDept(DeptTo dept){
		this.getSqlMapClientTemplate().update("dept.updateDept", dept);
	}
	
	public List<UserTo> getUsersByDeptId(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return this.getSqlMapClientTemplate().queryForList("dept.getUsersByDeptId",param);
	}
	
	public DeptTo getDeptById(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return (DeptTo)this.getSqlMapClientTemplate().queryForObject("dept.getDeptById", param);
	}
	
	public PagingInfo queryForListWithPaging(PagingInfo pagingInfo)throws Exception{
		return queryForListWithPaging("dept.getDepts",pagingInfo);
	}
	
	public Integer getDeptLeaderById(int id){	
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("dept.getDeptLeaderById",param);
	}
	
	public Integer getDeptLeaderByUserId(int userId){	
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("userId", userId);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("dept.getDeptLeaderByUserId",param);
	}
	
	public Integer getParentDeptById(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("dept.getParentDeptById",param);
	}
	
	public DeptTo getUserParentDeptDetailById(int userId){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("userId", userId);
		return (DeptTo) this.getSqlMapClientTemplate().queryForObject("dept.getUserParentDeptDetailById",param);
	}
	
	public DeptTo getParentDeptDetailById(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return (DeptTo) this.getSqlMapClientTemplate().queryForObject("dept.getParentDeptDetailById",param);
	}
	
	public String getDeptNameById(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return (String) this.getSqlMapClientTemplate().queryForObject("dept.getDeptNameById",param);
	}
	
	public List<DeptTo> getDeptsByCompanyId(int companyId){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("companyId", companyId);
		return this.getSqlMapClientTemplate().queryForList("dept.getDeptsByCompanyId",param);
	}
}
