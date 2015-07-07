package com.brick.customerVisit.DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.customerVisit.to.CustomerTO;

public class CustomerVisitDAO extends BaseDAO {

	public Map<String,Object> getEmpInfoById(Map<String,Object> param) throws Exception {
		
		Map<String,Object> result=new HashMap<String,Object>();
		
		result=(Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("employee.getEmpInforById",param);
		
		return result;
	}
	
	public List<CustomerTO> getWeekList() throws Exception {
		
		List<CustomerTO> result=null;
		result=this.getSqlMapClientTemplate().queryForList("customerVisit.getWeekList");
		
		if(result==null) {
			result=new ArrayList<CustomerTO>();
		}
		
		return result;
	}
	
	public List<Map<String,Object>> getProvincesList() throws Exception {
		
		List<Map<String,Object>> result=null;
		result=this.getSqlMapClientTemplate().queryForList("area.getProvinces");
		
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		
		return result;
	}
	
	public void addCustomerVisit(CustomerTO customerTO) throws Exception {
		
		this.getSqlMapClientTemplate().insert("customerVisit.addCustomerVisit",customerTO);
	}
	
	public List<CustomerTO> getCustomerVisit(Map<String,Object> param) throws Exception {
		
		List<CustomerTO> result=null;
		result=this.getSqlMapClientTemplate().queryForList("customerVisit.getCustomerVisit",param);
		
		if(result==null) {
			result=new ArrayList<CustomerTO>();
		}
		
		return result;
		
	}
	
	public int getMaxRow(Map<String,Object> param) throws Exception {
		
		int result=(Integer)this.getSqlMapClientTemplate().queryForObject("customerVisit.getMaxRowNum",param);
		
		return result;
	}
	
	public void deleteCustomerVisit(Map<String,String> param) throws Exception {
		
		this.getSqlMapClientTemplate().delete("customerVisit.deleteCustomerVisit",param);
	}
	
	public void updateCustomerVisit(CustomerTO customerTO) throws Exception {
		
		this.getSqlMapClientTemplate().update("customerVisit.updateCustomerVisit",customerTO);
	}
	
	public List<CustomerTO> getStaffList(Map<String,String> param) throws Exception {
		
		List<CustomerTO> result=null;
		result=this.getSqlMapClientTemplate().queryForList("customerVisit.getStaffList",param);
		
		if(result==null) {
			result=new ArrayList<CustomerTO>();
		}
		
		return result;
	}
	
	public String getRemark(Map<String,String> param) throws Exception {
		
		return (String)this.getSqlMapClientTemplate().queryForObject("customerVisit.getRemark",param);
		
	}
	
	public List<CustomerTO> getEmployeeList(Map<String,String> param) throws Exception {
		
		List<CustomerTO> result=null;
		result=this.getSqlMapClientTemplate().queryForList("customerVisit.getEmployeeList",param);
		
		if(result==null) {
			result=new ArrayList<CustomerTO>();
		}
		
		return result;
	}
	
	public List<Map<String,String>> queryDataDictionary1(Map<String,String> param) {
		
		List<Map<String,String>> result=null;
		result=(List<Map<String,String>>)this.getSqlMapClientTemplate().queryForList("customerVisit.queryDataDictionary",param);
		
		if(result==null) {
			result=new ArrayList<Map<String,String>>();
		}
		
		return result;
	}
}
