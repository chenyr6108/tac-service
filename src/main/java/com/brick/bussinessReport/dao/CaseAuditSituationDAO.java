package com.brick.bussinessReport.dao;

import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;

public class CaseAuditSituationDAO extends BaseDAO {

	public List<Map<String,Object>> getCurrentWorkingDaySituation() throws Exception {
		return this.getSqlMapClientTemplate().queryForList("businessReport.getCurrentWorkingDaySituation");
	}
	
	public List<Map<String,Object>> getLastWorkingDaySituation() throws Exception {
		return this.getSqlMapClientTemplate().queryForList("businessReport.getLastWorkingDaySituation");
	}
}
