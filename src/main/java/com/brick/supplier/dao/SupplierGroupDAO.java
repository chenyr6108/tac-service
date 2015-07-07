package com.brick.supplier.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;
import com.brick.supplier.to.SupplierGroupTO;

public class SupplierGroupDAO extends BaseDAO {

	public List<SupplierGroupTO> getSuplGroupInfo(Context context) throws Exception {
		
		List<SupplierGroupTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("supplier.getSuplGroupInfo", context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplierGroupTO>();
		}
		
		return resultList;
	}
	
	public void addSuplGroup(Context context) throws Exception {
		this.getSqlMapClientTemplate().insert("supplier.addSuplGroup", context.contextMap);
	}
	
	public List<SupplierGroupTO> getSupl(Context context) throws Exception {
		
		List<SupplierGroupTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("supplier.getSupl", context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplierGroupTO>();
		}
		
		return resultList;
	}
	
	public List<SupplierGroupTO> getSuplList(Context context) throws Exception {
		
		List<SupplierGroupTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("supplier.getSuplList", context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplierGroupTO>();
		}
		
		return resultList;
	}
	
	public void removeSuplGroupMap(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("supplier.removeSuplGroupMap", context.contextMap);
	}
	
	public void insertSuplGroupMap(Map<String,String> param) throws Exception {
		this.getSqlMapClientTemplate().insert("supplier.insertSuplGroupMap", param);
	}
	
	public String checkHasMaping(Map<String,String> param) throws Exception {
		String result=(String)this.getSqlMapClientTemplate().queryForObject("supplier.checkHasMaping", param);
		if(result==null||"".equals(result)) {
			result="N";
		}
		return result;
	}
	
	public List<SupplierGroupTO> getNotLoopCreditAmount(Context context) throws Exception {
		List<SupplierGroupTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("supplier.getNotLoopCreditAmount",context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplierGroupTO>();
		}
		
		return resultList;
	}
	
	public List<SupplierGroupTO> getLoopCreditAmountSum(Context context) throws Exception {
		List<SupplierGroupTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("supplier.getLoopCreditAmountSum",context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplierGroupTO>();
		}
		
		return resultList;
	}
	
	public List<SupplierGroupTO> getNotLoopCreditAmountSum(Context context) throws Exception {
		List<SupplierGroupTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("supplier.getNotLoopCreditAmountSum",context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplierGroupTO>();
		}
		
		return resultList;
	}
	
	public SupplierGroupTO getSuplGroupCredit(Context context) throws Exception {
		SupplierGroupTO result=null;
		
		result=(SupplierGroupTO)this.getSqlMapClientTemplate().queryForObject("supplier.getSuplGroupCredit",context.contextMap);
		
		if(result==null) {
			result=new SupplierGroupTO();
		}
		
		return result;
	}
	
	public void removeGroupCredit(SupplierGroupTO to) throws Exception {
		this.getSqlMapClientTemplate().update("supplier.removeGroupCredit", to);
	}
	public void addGroupCredit(SupplierGroupTO to) throws Exception {
		this.getSqlMapClientTemplate().insert("supplier.addGroupCredit", to);
	}
	
	public List<SupplierGroupTO> getCreditLog(Context context) throws Exception {
		List<SupplierGroupTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("supplier.getCreditLog",context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplierGroupTO>();
		}
		
		return resultList;
	}
	
	public SupplierGroupTO getPayBeforeLoop(Context context) throws Exception {
		SupplierGroupTO result=null;
		
		result=(SupplierGroupTO)this.getSqlMapClientTemplate().queryForObject("supplier.getPayBeforeLoop",context.contextMap);
		
		if(result==null) {
			result=new SupplierGroupTO();
		}
		
		return result;
	}
	
	public SupplierGroupTO getPayBeforeNotLoop(Context context) throws Exception {
		SupplierGroupTO result=null;
		
		result=(SupplierGroupTO)this.getSqlMapClientTemplate().queryForObject("supplier.getPayBeforeNotLoop",context.contextMap);
		
		if(result==null) {
			result=new SupplierGroupTO();
		}
		
		return result;
	}
}
