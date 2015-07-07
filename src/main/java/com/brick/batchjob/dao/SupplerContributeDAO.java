package com.brick.batchjob.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.batchjob.to.SupplerContributeTo;

public class SupplerContributeDAO extends BaseDAO {

	public List<SupplerContributeTo> getSuplUnitPriceAccrualTRByContract() throws DaoException{
		
		List<SupplerContributeTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getSuplUnitPriceAccrualTRByContract");
		
		if(resultList==null) {
			resultList=new ArrayList<SupplerContributeTo>();
		}
		return resultList;
	}
	
	public List<SupplerContributeTo> getSuplUnitPriceSumByContract() throws DaoException{
		
		List<SupplerContributeTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getSuplUnitPriceSumByContract");
		
		if(resultList==null) {
			resultList=new ArrayList<SupplerContributeTo>();
		}
		return resultList;
	}
	
	public List<SupplerContributeTo> getPayMoneyByContract() throws DaoException{
		
		List<SupplerContributeTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getPayMoneyByContract");
		
		if(resultList==null) {
			resultList=new ArrayList<SupplerContributeTo>();
		}
		return resultList;
	}
	
	public List<SupplerContributeTo> getLeaseCountBySupl() throws DaoException{
		
		List<SupplerContributeTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getLeaseCountBySupl");
		
		if(resultList==null) {
			resultList=new ArrayList<SupplerContributeTo>();
		}
		return resultList;
	}
	
	public List<SupplerContributeTo> getEquipmentCountBySupl() throws DaoException{
		
		List<SupplerContributeTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getEquipmentCountBySupl");
		
		if(resultList==null) {
			resultList=new ArrayList<SupplerContributeTo>();
		}
		return resultList;
	}
	
	public List<SupplerContributeTo> getGrantPrice() throws DaoException{
		
		List<SupplerContributeTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getGrantPrice");
		
		if(resultList==null) {
			resultList=new ArrayList<SupplerContributeTo>();
		}
		return resultList;
	}
	
	public void insertSuplContribute(SupplerContributeTo supplerContributeTo) {
		
		this.getSqlMapClientTemplate().insert("businessReport.insertSuplContribute",supplerContributeTo);
	}
	
	public List<SupplerContributeTo> getDetailBySuplId(Map<String,String> param) throws DaoException{
		
		List<SupplerContributeTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDetailBySuplId",param);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplerContributeTo>();
		}
		return resultList;
	}
	
	public List<SupplerContributeTo> getDunCountResult() throws DaoException{
		
		List<SupplerContributeTo> resultList=null;
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("DUN_TYPE","租金");
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDunCountResult",param);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplerContributeTo>();
		}
		return resultList;
	}
}
