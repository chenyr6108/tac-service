package com.brick.special.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;
import com.brick.special.to.CreditSpecialTO;

public class CreditSpecialDAO extends BaseDAO {

	public List<CreditSpecialTO> getCreditSpecialPropertyList() throws Exception {
		
		List<CreditSpecialTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("creditSpecial.getCreditSpecialPropertyList");
		
		if(resultList==null) {
			resultList=new ArrayList<CreditSpecialTO>();
		}
		
		return resultList;
	}
	
	public List<CreditSpecialTO> queryCreditSpecialGroup() throws Exception {
		
		List<CreditSpecialTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("creditSpecial.queryCreditSpecialGroup");
		
		if(resultList==null) {
			resultList=new ArrayList<CreditSpecialTO>();
		}
		
		return resultList;
	}
	
	public boolean checkIsDuplicate(Context context) throws Exception {
		
		String result=(String)this.getSqlMapClientTemplate().queryForObject("creditSpecial.checkIsDuplicate",context.contextMap);
		
		if("Y".equals(result)) {
			return true;
		}
		
		return false;
	}
	
	public boolean checkIsDuplicate1(Context context) throws Exception {
		
		String result=(String)this.getSqlMapClientTemplate().queryForObject("creditSpecial.checkIsDuplicate1",context.contextMap);
		
		if("Y".equals(result)) {
			return false;
		}
		
		return true;
	}
	
	public void addCreditSpecialProperty(Context context) throws Exception {
		
		this.getSqlMapClientTemplate().insert("creditSpecial.addCreditSpecialProperty",context.contextMap);
	}
	
	public void updateCreditSpecialProperty(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("creditSpecial.updateCreditSpecialProperty",context.contextMap);
	}
	
	public void configCreditSpecialProperty(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("creditSpecial.configCreditSpecialProperty",context.contextMap);
	}
	
	public void addCreditSpecialGroup(Context context) throws Exception {
		this.getSqlMapClientTemplate().insert("creditSpecial.addCreditSpecialGroup",context.contextMap);
	}
	
	public void addCreditSpecialGroupMap(Context context) throws Exception {
		this.getSqlMapClientTemplate().insert("creditSpecial.addCreditSpecialGroupMap",context.contextMap);
	}
	
	public List<CreditSpecialTO> queryCreditSpecialGroupMap(Context context) throws Exception {
		
		List<CreditSpecialTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("creditSpecial.queryCreditSpecialGroupMap",context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<CreditSpecialTO>();
		}
		
		return resultList;
		
	}
	
	public List<CreditSpecialTO> getAreaList() throws Exception {
		
		List<CreditSpecialTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("creditSpecial.getAreaList");
		
		if(resultList==null) {
			resultList=new ArrayList<CreditSpecialTO>();
		}
		
		return resultList;
		
	}
	
	public void updateCreditSpecialGroup(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("creditSpecial.updateCreditSpecialGroup",param);
	}
	
	public void updateCreditSpecialGroupMap(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("creditSpecial.updateCreditSpecialGroupMap",param);
	}
	
	public void updateCreditSpecialGroupMap1(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("creditSpecial.updateCreditSpecialGroupMap1",param);
	}
	
	public void deleteCreditSpecialGroup(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("creditSpecial.deleteCreditSpecialGroup",context.contextMap);
	}
	
	public void deleteCreditSpecialGroupMap(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("creditSpecial.deleteCreditSpecialGroupMap",context.contextMap);
	}
	
	public List<Map<String,String>> getMaintaninceType() throws Exception {
		return this.getSqlMapClientTemplate().queryForList("prdcKind.getMaintaninceType2");
	}
	
	public List<Map<String,String>> getBrandList() throws Exception {
		return this.getSqlMapClientTemplate().queryForList("creditSpecial.getBrandList");
	}
}
