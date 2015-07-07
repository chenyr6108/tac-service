package com.brick.payer.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.payer.to.PayerTo;

public class PayerDAO extends BaseDAO{

	public Integer insertPayer(PayerTo payer) throws DaoException{
		return (Integer)this.getSqlMapClientTemplate().insert("payer.insertPayer", payer);		
	}
	public void deletePayer(PayerTo payer){
		this.getSqlMapClientTemplate().update("payer.deletePayer", payer);
	}
	
	public void updatePayer(PayerTo payer){
		this.getSqlMapClientTemplate().update("payer.updatePayer", payer);
	}
	
	public PayerTo getPayerById(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return (PayerTo)this.getSqlMapClientTemplate().queryForObject("payer.getPayerById", param);
	}
	
	public List<PayerTo> getPayersByCreditId(Integer creditId){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("creditId", creditId);
		return this.getSqlMapClientTemplate().queryForList("payer.getPayers", param);
	}
}
