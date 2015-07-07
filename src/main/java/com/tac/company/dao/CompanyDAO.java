package com.tac.company.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.tac.company.to.CompanyTo;

public class CompanyDAO extends BaseDAO{

	public void insertCompany(CompanyTo company) throws DaoException{
		this.getSqlMapClientTemplate().insert("company.insertCompany", company);		
	}
	public void deleteCompany(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		this.getSqlMapClientTemplate().update("company.deleteCompany", param);
	}
	
	public void updateCompany(CompanyTo company){
		this.getSqlMapClientTemplate().update("company.updateCompany", company);
	}
	
	public CompanyTo getCompanyById(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return (CompanyTo)this.getSqlMapClientTemplate().queryForObject("company.getCompanyById", param);
	}
	
	public List<CompanyTo> getAllCompany(){
		return this.getSqlMapClientTemplate().queryForList("company.getAllCompany");
	}
	
}
