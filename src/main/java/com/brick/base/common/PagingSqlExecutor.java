package com.brick.base.common;

import java.sql.Connection;
import java.sql.SQLException;

import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.StatementScope;

public class PagingSqlExecutor extends SqlExecutor {

	private Dialect dialect;
	
	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public void executeQuery(StatementScope statementScope, Connection conn,
			String sql, Object[] parameters, int skipResults, int maxResults,
			RowHandlerCallback callback) throws SQLException {
		//System.out.println("=========================================");
		super.executeQuery(statementScope, conn, sql, parameters, skipResults,
				maxResults, callback);
	}
	
	public void executeQueryForPaging(StatementScope statementScope, Connection conn, String sql, Object[] parameters, int skipResults, int maxResults,
			RowHandlerCallback callback) throws SQLException {
		super.executeQuery(statementScope, conn, sql, parameters, NO_SKIPPED_RESULTS, NO_MAXIMUM_RESULTS, callback);
	}
	
	public void queryTotalCount(){}
	
}
