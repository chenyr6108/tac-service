package com.brick.businessSupport.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.businessSupport.to.SqlTO;

public class BusinessSupportDAO extends BaseDAO {

	public Integer updateExtensionProjectValidDate(String string,Map<String, Object> paramMap) throws DaoException {
		try {
			return this.getSqlMapClientTemplate().update(string, paramMap);
		} catch (Exception e) {
			throw new DaoException(e);
		}
		
	}

	public SqlTO getSql(String id) {
		SqlTO sql = new SqlTO();
		sql.setId(id);
		return (SqlTO) this.getSqlMapClientTemplate().queryForObject("businessSupport.getSqlById", sql);
	}

	public Integer executeUpdateSql(SqlTO sql) {
		return this.getSqlMapClientTemplate().update("businessSupport.executeUpdateSql", sql);
	}

	public Integer executeInsertSql(SqlTO sql) {
		return (Integer) this.getSqlMapClientTemplate().insert("businessSupport.executeInsertSql", sql);
	}
	
	public List<LinkedHashMap<String, Object>> executeSelectSql(SqlTO sql) throws SQLException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sql", sql.getSql());
		this.getSqlMapClient().flushDataCache(); 
		return this.getSqlMapClient().queryForList("businessSupport.executeSelectSql", paramMap);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void updateSql(SqlTO sql) {
		this.getSqlMapClientTemplate().update("businessSupport.updateSql", sql);
		
	}

}
