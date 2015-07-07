package com.tac.company.service;

import java.util.List;
import java.util.Map;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.to.PagingInfo;
import com.tac.company.dao.CompanyDAO;
import com.tac.company.to.CompanyTo;


public class CompanyService extends BaseService {
	private CompanyDAO companyDAO;
	
	public PagingInfo queryForListWithPaging(Map paramMap) throws  ServiceException{		
		return this.queryForListWithPaging("company.getCompanys", paramMap, "ID",ORDER_TYPE.ASC);
	}
	
	public List<CompanyTo> getAllCompany(){
		return companyDAO.getAllCompany();
	}
	public void saveCompany(CompanyTo company) throws Exception{
		companyDAO.insertCompany(company);
	}
	public void updateCompany(CompanyTo company) throws Exception{
		companyDAO.updateCompany(company);
	}
	
	public CompanyTo getCompanyById(int id){
		return companyDAO.getCompanyById(id);
	}
	
	public void deleteCompany(int id){
		companyDAO.deleteCompany(id);
	}

	public CompanyDAO getCompanyDAO() {
		return companyDAO;
	}

	public void setCompanyDAO(CompanyDAO companyDAO) {
		this.companyDAO = companyDAO;
	}
}

