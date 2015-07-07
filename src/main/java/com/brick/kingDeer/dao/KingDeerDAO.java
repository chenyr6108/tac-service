package com.brick.kingDeer.dao;

import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;

public class KingDeerDAO extends BaseDAO {

	public String checkResult(Context context) {
		return (String)this.getSqlMapClientTemplate().queryForObject("kingDeer.checkResult",context.contextMap);
	}
	
	public List<Map<String,Object>> batchQueryForCar(Map<String,Object> param) throws Exception {
		return (List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("kingDeer.batchQueryForCar",param);
	}
	
	public Map<String,Object> paymentQueryForCar(Context context) {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("kingDeer.paymentQueryForCar",context.contextMap);
	}
	
	public void paymentForCarI(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("kingDeer.paymentForCarI",param);
	}
	public void k3Transfer(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("kingDeer.k3Transfer",param);
	}
	
	public void updateBatchNum(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("kingDeer.updateBatchNum",param);
	}
	
	public String getKingDeerBaseSubject(Map<String,Object> param) throws Exception {
		return (String)this.getSqlMapClientTemplate().queryForObject("kingDeer.getKingDeerBaseSubject",param);
	}
	
	public String getKingDeerBaseCustomer(Map<String,Object> param) throws Exception {
		return (String)this.getSqlMapClientTemplate().queryForObject("kingDeer.getKingDeerBaseCustomer",param);
	}
	public String getKingDeerBaseLease(Map<String,Object> param) throws Exception {
		return (String)this.getSqlMapClientTemplate().queryForObject("kingDeer.getKingDeerBaseLease",param);
	}
	public String getKingDeerBaseSupplier(Map<String,Object> param) throws Exception {
		return (String)this.getSqlMapClientTemplate().queryForObject("kingDeer.getKingDeerBaseSupplier",param);
	}
	
	//获得金蝶基本档数据
	public List<Map<String,Object>> getKingDeerBaseCustInfo() throws Exception {
		return (List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("kingDeer.getKingDeerBaseCustInfo");
	}
	public List<Map<String,Object>> getKingDeerBaseLeaseInfo() throws Exception {
		return (List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("kingDeer.getKingDeerBaseLeaseInfo");
	}
	public List<Map<String,Object>> getKingDeerBaseSuplInfo() throws Exception {
		return (List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("kingDeer.getKingDeerBaseSuplInfo");
	}
	public List<Map<String,Object>> getKingDeerBaseUserInfo() throws Exception {
		return (List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("kingDeer.getKingDeerBaseUserInfo");
	}
	public List<Map<String,Object>> getKingDeerBaseCmpyInfo() throws Exception {
		return (List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("kingDeer.getKingDeerBaseCmpyInfo");
	}
	public long insertKingDeerBaseInfoForTac(Map<String,Object> param) throws Exception {
		return (Long)this.getSqlMapClientTemplate().insert("kingDeer.insertKingDeerBaseInfoForTac",param);
	}
	public void updateKingDeerBaseInfoForTac(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("kingDeer.updateKingDeerBaseInfoForTac",param);
	}
	
	public void insertKingDeerBaseInfoForKingDeer(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("kingDeer.insertKingDeerBaseInfoForKingDeer",param);
	}
	
	public void insertKingDeerOrganization(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("kingDeer.insertKingDeerOrganization",param);
	}
	public void insertKingDeerSupplier(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("kingDeer.insertKingDeerSupplier",param);
	}
	public void insertKingDeerEmp(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("kingDeer.insertKingDeerEmp",param);
	}
}
