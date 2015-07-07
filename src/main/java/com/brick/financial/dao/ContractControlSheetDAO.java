package com.brick.financial.dao;

import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;

public class ContractControlSheetDAO extends BaseDAO {
	
	public List<Map<String,Object>> getContractControlSheetDetail(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentContract.getContractControlSheetDetail",context.contextMap);
	}
	
	public double getContractControlSheetPayMoneyTotal(Context context) throws Exception {
		return (Double)this.getSqlMapClientTemplate().queryForObject("rentContract.getContractControlSheetPayMoneyTotal",context.contextMap);
	}
}
