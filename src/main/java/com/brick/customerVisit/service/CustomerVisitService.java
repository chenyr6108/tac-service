package com.brick.customerVisit.service;

import java.util.List;
import java.util.Map;

import com.brick.base.service.BaseService;
import com.brick.customerVisit.DAO.CustomerVisitDAO;
import com.brick.customerVisit.to.CustomerTO;

public class CustomerVisitService extends BaseService {

	private CustomerVisitDAO customerVisitDAO;

	public CustomerVisitDAO getCustomerVisitDAO() {
		return customerVisitDAO;
	}

	public void setCustomerVisitDAO(CustomerVisitDAO customerVisitDAO) {
		this.customerVisitDAO = customerVisitDAO;
	}
	
	public Map<String,Object> getEmpInfoById(Map<String,Object> param) throws Exception {
		
		return this.customerVisitDAO.getEmpInfoById(param);
	}
	
	public List<CustomerTO> getWeekList() throws Exception {
		
		return this.customerVisitDAO.getWeekList();
	}
	
	public List<Map<String,Object>> getProvincesList() throws Exception {
		
		return this.customerVisitDAO.getProvincesList();
	}
	
	public List<Map<String,Object>> queryDataDictionary(Map<String,String> param) throws Exception {
		
		return super.baseDAO.queryDataDictionary(param);
	}
	
	public void addCustomerVisit(CustomerTO customerTO) throws Exception {
		
		this.customerVisitDAO.addCustomerVisit(customerTO);
	}
	
	public List<CustomerTO> getCustomerVisit(Map<String,Object> param) throws Exception {
		
		return this.customerVisitDAO.getCustomerVisit(param);
	}
	
	public int getMaxRow(Map<String,Object> param) throws Exception {
		
		return this.customerVisitDAO.getMaxRow(param);
	}
	
	public void deleteCustomerVisit(Map<String,String> param) throws Exception {
		
		this.customerVisitDAO.deleteCustomerVisit(param);
	}
	
	public void updateCustomerVisit(CustomerTO customerTO) throws Exception {
		
		this.customerVisitDAO.updateCustomerVisit(customerTO);
	}
	
	public List<Map<String,Object>> queryDept(Map<String,String> param) throws Exception {
		
		return super.baseDAO.queryDept(param);
	}
	
	public List<CustomerTO> getStaffList(Map<String,String> param) throws Exception {
		
		return this.customerVisitDAO.getStaffList(param);
	}
	
	public String getRemark(Map<String,String> param) throws Exception {
		
		return this.customerVisitDAO.getRemark(param);
	}
	
	public List<CustomerTO> getEmployeeList(Map<String,String> param) throws Exception {
		
		return this.customerVisitDAO.getEmployeeList(param);
	}
	
	public List<Map<String,String>> queryDataDictionary1(Map<String,String> param) throws Exception {
		
		return this.customerVisitDAO.queryDataDictionary1(param);
	}
}
