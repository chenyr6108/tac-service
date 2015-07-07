package com.brick.contribution.dao;

import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;

public class ContributionDao extends BaseDAO {

	public List<Map<String, Object>> getContributions(Map<String, Object> param) throws Exception {
		List<Map<String, Object>> result = this.getSqlMapClientTemplate().queryForList("contribution.getContributions", param);
		return result;
	}

}
