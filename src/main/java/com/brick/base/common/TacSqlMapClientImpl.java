package com.brick.base.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.brick.service.core.DataAccessor;
import com.ibatis.common.util.PaginatedList;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.execution.BatchException;

@SuppressWarnings("deprecation")
public class TacSqlMapClientImpl implements SqlMapClient {
	Logger transLogger = Logger.getLogger("trans");  
	
	private SqlMapClient sqlMapClient;

	public SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}
	
	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;
	}
	
	public void initSqlMapClient() throws Exception{
		if (this.sqlMapClient != null) {
			if (DataAccessor.getSession() == null) {
				DataAccessor.setSqlMapper(this);
			}
		} else {
			throw new Exception("No SqlMapClient!");
		}
	}
	
	@Override
	public void startTransaction() throws SQLException{
		try {
			//if (this.sqlMapClient.getCurrentConnection() == null) {
			this.sqlMapClient.startTransaction();
			this.sqlMapClient.getCurrentConnection().setAutoCommit(false);
			int hashCode = this.sqlMapClient.getCurrentConnection() == null ? 0 : this.sqlMapClient.getCurrentConnection().hashCode();
			transLogger.info(hashCode + ".Start");
		//}
		} catch (Exception e) {
			transLogger.warn("[startTransaction]:" + e.getMessage());
			throw new SQLException(e);
		}
		
	}
	
	public void commitTransaction() throws SQLException{
		int hashCode = 0;
		try {
			hashCode = this.sqlMapClient.getCurrentConnection() == null ? 0 : this.sqlMapClient.getCurrentConnection().hashCode();
			transLogger.info(hashCode + ".Commit");
			this.sqlMapClient.getCurrentConnection().commit();
			this.sqlMapClient.commitTransaction();
		} catch (Exception e) {
			transLogger.warn("[commitTransaction]:" + e.getMessage());
			throw new SQLException(e);
		} finally {
			transLogger.info(hashCode + ".End");
			this.sqlMapClient.endTransaction();
		}
	}
	
	public void endTransaction() throws SQLException{
		int hashCode = 0;
		try {
			hashCode = this.sqlMapClient.getCurrentConnection() == null ? 0 : this.sqlMapClient.getCurrentConnection().hashCode();
			if (hashCode != 0){
				if (!this.sqlMapClient.getCurrentConnection().isClosed()) {
					this.sqlMapClient.getCurrentConnection().rollback();
					transLogger.info(hashCode + ".Rollback");
				}
				transLogger.info(hashCode + ".End");
			}
		} catch (Exception e) {
			transLogger.warn("[endTransaction]:" + e.getMessage());
			throw new SQLException(e);
		} finally {
			this.sqlMapClient.endTransaction();
		}
	}
	
	@Override
	public Object insert(String id, Object parameterObject) throws SQLException {
		return this.sqlMapClient.insert(id, parameterObject);
	}

	@Override
	public Object insert(String id) throws SQLException {
		return this.sqlMapClient.insert(id);
	}

	@Override
	public int update(String id, Object parameterObject) throws SQLException {
		return this.sqlMapClient.update(id, parameterObject);
	}

	@Override
	public int update(String id) throws SQLException {
		return this.sqlMapClient.update(id);
	}

	@Override
	public int delete(String id, Object parameterObject) throws SQLException {
		return this.sqlMapClient.delete(id, parameterObject);
	}

	@Override
	public int delete(String id) throws SQLException {
		return this.sqlMapClient.delete(id);
	}

	@Override
	public Object queryForObject(String id, Object parameterObject)
			throws SQLException {
		return this.sqlMapClient.queryForObject(id, parameterObject);
	}

	@Override
	public Object queryForObject(String id) throws SQLException {
		return this.sqlMapClient.queryForObject(id);
	}

	@Override
	public Object queryForObject(String id, Object parameterObject,
			Object resultObject) throws SQLException {
		return this.sqlMapClient.queryForObject(id, parameterObject, resultObject);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List queryForList(String id, Object parameterObject)
			throws SQLException {
		return this.sqlMapClient.queryForList(id, parameterObject);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List queryForList(String id) throws SQLException {
		return this.sqlMapClient.queryForList(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List queryForList(String id, Object parameterObject, int skip,
			int max) throws SQLException {
		return this.sqlMapClient.queryForList(id, parameterObject, skip, max);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List queryForList(String id, int skip, int max) throws SQLException {
		return this.sqlMapClient.queryForList(id, skip, max);
	}

	@Override
	public void queryWithRowHandler(String id, Object parameterObject,
			RowHandler rowHandler) throws SQLException {
		this.sqlMapClient.queryWithRowHandler(id, parameterObject, rowHandler);
		
	}

	@Override
	public void queryWithRowHandler(String id, RowHandler rowHandler)
			throws SQLException {
		this.sqlMapClient.queryWithRowHandler(id, rowHandler);
		
	}

	@Override
	public PaginatedList queryForPaginatedList(String id,
			Object parameterObject, int pageSize) throws SQLException {
		return this.sqlMapClient.queryForPaginatedList(id, parameterObject, pageSize);
	}

	@Override
	public PaginatedList queryForPaginatedList(String id, int pageSize)
			throws SQLException {
		return this.sqlMapClient.queryForPaginatedList(id, pageSize);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map queryForMap(String id, Object parameterObject, String keyProp)
			throws SQLException {
		return this.sqlMapClient.queryForMap(id, parameterObject, keyProp);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map queryForMap(String id, Object parameterObject, String keyProp,
			String valueProp) throws SQLException {
		return this.sqlMapClient.queryForMap(id, parameterObject, keyProp, valueProp);
	}

	@Override
	public void startBatch() throws SQLException {
		this.sqlMapClient.startBatch();
	}

	@Override
	public int executeBatch() throws SQLException {
		return this.sqlMapClient.executeBatch();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List executeBatchDetailed() throws SQLException, BatchException {
		return this.sqlMapClient.executeBatchDetailed();
	}

	@Override
	public void setUserConnection(Connection connnection) throws SQLException {
		this.sqlMapClient.setUserConnection(connnection);
	}

	@Override
	public Connection getUserConnection() throws SQLException {
		return this.sqlMapClient.getUserConnection();
	}

	@Override
	public Connection getCurrentConnection() throws SQLException {
		return this.sqlMapClient.getCurrentConnection();
	}

	@Override
	public DataSource getDataSource() {
		return this.sqlMapClient.getDataSource();
	}

	@Override
	public SqlMapSession openSession() {
		return this.sqlMapClient.openSession();
	}

	@Override
	public SqlMapSession openSession(Connection conn) {
		return this.sqlMapClient.openSession(conn);
	}

	@Override
	public SqlMapSession getSession() {
		return this.sqlMapClient.getSession();
	}

	@Override
	public void flushDataCache() {
		this.sqlMapClient.flushDataCache();
	}

	@Override
	public void flushDataCache(String cacheId) {
		this.sqlMapClient.flushDataCache(cacheId);
		
	}

	@Override
	public void startTransaction(int transactionIsolation) throws SQLException {
		this.sqlMapClient.startTransaction(transactionIsolation);
	}
}
