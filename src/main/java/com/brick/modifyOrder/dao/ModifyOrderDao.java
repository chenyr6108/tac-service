package com.brick.modifyOrder.dao;

import java.util.HashMap;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;

public class ModifyOrderDao extends BaseDAO{
	
	
	public void deleteModifyOrder(Map<String, Object> map)throws DaoException {
		try {
			getSqlMapClientTemplate().update("modifyOrder.updateModifyOrder",map);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	public void updateFile(Map<String, Object> map)throws DaoException {
		try {
			getSqlMapClientTemplate().update("modifyOrder.updateFile",map);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public int getDemandCountByStateAndUserId(int stateCode, int userId){
		Map param = new HashMap();
		param.put("stateCode", stateCode);
		param.put("userId", userId);
		int count = (Integer)this.getSqlMapClientTemplate().queryForObject("demand.getDemandCountByStateAndUserId",param);
		return count;
	}
	
}
